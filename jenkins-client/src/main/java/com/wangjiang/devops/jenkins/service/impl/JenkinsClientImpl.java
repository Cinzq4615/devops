package com.wangjiang.devops.jenkins.service.impl;

import com.alibaba.fastjson.JSON;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.BuildConsoleStreamListener;
import com.offbytwo.jenkins.model.*;
import com.wangjiang.devops.jenkins.exception.JenkinsOperateException;
import com.wangjiang.devops.jenkins.intf.JenkinsLogHandler;
import com.wangjiang.devops.jenkins.service.JenkinsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @create 2018-05-03 9:54
 **/
@Slf4j
@Service
public class JenkinsClientImpl implements JenkinsClient {

    private static final String PREFIX_MARK = "ANALYSIS SUCCESSFUL, you can browse";

    private static final String SUFFIX_MARK = "[INFO] Note that you will be able to access the updated dashboard once" +
            " the server has processed the submitted analysis report";


    @Override
    public Long createJob(JenkinsServer jenkinsServer, String jobXml, String jobType, String jobName) {
        if (jobXml != null) {
            try {
                JobWithDetails jobWithDetails = jenkinsServer.getJob(jobName);
                if (jobWithDetails == null) {
                    //jenkins 创建应用
                    jenkinsServer.createJob(jobName, jobXml, true);
                } else {
                    System.out.println(String.format("job: %s 已经存在！",jobName));
                    deleteJob(jenkinsServer, jobName, true);
                    jenkinsServer.createJob(jobName, jobXml, true);

                }
//                获取jenkins应用 build
//                jobWithDetails = jenkinsServer.getJob(jobName);
//                jobWithDetails.build(false);
//                Integer buildNumber = jobWithDetails.getNextBuildNumber();
//                log.info("JenkinsClientImpl.createJob ----------- jobName : {}, buildNumber : {}", jobName,
//                 buildNumber);
                return Long.valueOf(1);
            } catch (IOException e) {
                throw new JenkinsOperateException(e);
            }
        } else {
            throw new NullPointerException("jenkins jobxml null pointerexception");
        }
    }

    @Override
    public Build getJobBuild(JenkinsServer jenkinsServer, String jobName, Integer buildNumber) {
        Build build = null;
        try {
            JobWithDetails jobWithDetails = jenkinsServer.getJob(jobName);
            if (null == jobWithDetails) {
                return null;
            }
            build = jobWithDetails.getBuildByNumber(buildNumber);

        } catch (IOException e) {
            log.error("jenkins getJobBuild exception", e);
        } finally {
            return build;
        }
    }

    @Override
    public void build(JenkinsServer jenkinsServer, String jobName, Map<String,String> parms) {
        log.info(String.format("job%s:开始构建",jobName));
        log.info(String.format("构建参数:%s", JSON.toJSONString(parms)));
        try {
            JobWithDetails jobWithDetails = jenkinsServer.getJob(jobName);
            if (null == jobWithDetails) {
                log.info(String.format("%s:不存在",jobName));
            }
            jobWithDetails.build(parms,true);

        } catch (IOException e) {
            log.error("jenkins build exception", e);
        }
    }

    @Override
    public String addJobToView(JenkinsServer jenkinsServer, String jobName, String view) {
        return null;
    }

    @Override
    public void stopJob(JenkinsServer jenkinsServer, String jobName, Integer buildNumber) {
        try {
            JobWithDetails jobWithDetails = jenkinsServer.getJob(jobName);
            if (null == jobWithDetails) {
                return;
            }
            Build build = jobWithDetails.getBuildByNumber(buildNumber);
            if (null == build) {
                return;
            }
            build.Stop();

        } catch (IOException e) {
            log.error("jenkins stopJob exception", e);
        }
    }


    @Override
    public void deleteJob(JenkinsServer jenkinsServer, String jobName, boolean crumbFlag) {
        try {
            JobWithDetails jobWithDetails = jenkinsServer.getJob(jobName);
            if (null == jobWithDetails) {
                return;
            }
            jenkinsServer.deleteJob(jobName, crumbFlag);
        } catch (IOException e) {
            log.error("jenkins deleteJob exception", e);
        }
    }

    @Override
    public void stopJob(JenkinsServer jenkinsServer, String queueItemUrl) {
        if (queueItemUrl != null) {
            try {
                Build build = jenkinsServer.getBuild(jenkinsServer.getQueueItem(new QueueReference(queueItemUrl)));
                build.Stop();
            } catch (IOException e) {
                log.error("jenkins stopJob exception", e);
            }
        }
    }

    @Override
    public String getLog(JenkinsServer jenkinsServer, String jobName, Integer number) {
        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);
            if (job != null) {
                List<Build> builds = job.getBuilds();
                for (Build build : builds) {
                    if (build.getNumber() == number) {
                        return build.details().getConsoleOutputText();
                    }
                }
            }
        } catch (IOException e) {
            log.error("jenkinsclient getlog exception", e);
        }
        return null;
    }

    @Override
    public void tailLog(JenkinsServer jenkinsServer, String jobName, Integer buildNumber,
                        JenkinsLogHandler jenkinsLogHandler) {
        try {
            log.info("JenkinsClientImpl.tailLog ==========start============ jobName:{}, buildNumber:{}", jobName,
                    buildNumber);
            Build build = jenkinsServer.getJob(jobName).getBuildByNumber(buildNumber);
            if (null == build) {
                for (int i = 0; i < 300; i++) {
                    try {
                        log.info("JenkinsClientImpl.tailLog ==========sleep============ jobName:{}, buildNumber:{}, i" +
                                " : {}", jobName, buildNumber, i);
                        Thread.sleep(1000);
                        build = jenkinsServer.getJob(jobName).getBuildByNumber(buildNumber);
                        if (null != build) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (null == build) {
                log.error("JenkinsClientImpl.tailLog ====================== build is null jobName:{}, buildNumber:{}"
                        , jobName, buildNumber);
                jenkinsLogHandler.finished(jobName, buildNumber, BuildResult.CANCELLED);
                return;
            }
            BuildWithDetails buildWithDetails = build.details();
            Build finalBuild = build;
            buildWithDetails.streamConsoleOutput(new BuildConsoleStreamListener() {
                @Override
                public void onData(String newLogChunk) {
                    jenkinsLogHandler.handle(newLogChunk);
                }

                @Override
                public void finished() {
                    try {
                        jenkinsLogHandler.finished(jobName, buildNumber, finalBuild.details().getResult());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1, 30 * 60);
        } catch (Exception e) {
            log.error("JenkinsClientImpl.tailLog ================== jobName:{}, buildNumber:{} , exception : {}",
                    jobName, buildNumber, e);
        }
    }

    @Override
    public Boolean checkLastBuildIsCompiling(JenkinsServer jenkinsServer, String jobName) {

        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);

            if (job == null) {
                return false;
            }
            Build lastBuild = job.getLastBuild();

            if (lastBuild == null) {
                return false;
            }
            if (lastBuild.details().isBuilding()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public String getSonarReport(JenkinsServer jenkinsServer, String jobName, Integer number) {
        StringBuffer url = new StringBuffer();
        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);
            if (job != null) {
                List<Build> builds = job.getBuilds();
                for (Build build : builds) {
                    if (build.getNumber() == number) {
                        String log = build.details().getConsoleOutputText();
                        if (!"".equals(log)) {
                            if (log.indexOf(PREFIX_MARK) > 0 && log.indexOf(SUFFIX_MARK) > 0) {
                                url.append(log.substring(log.indexOf(PREFIX_MARK) + PREFIX_MARK.length() + 1,
                                        log.indexOf(SUFFIX_MARK) - 2));
                                if (url.length() > 0) {
                                    return url.toString();
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("jenkinsclient get sonar test report exception", e);
        }
        return null;
    }

    @Override
    public JobWithDetails getJobWithDetails(JenkinsServer jenkinsServer, String jobName) {
        try {
            return jenkinsServer.getJob(jobName);
        } catch (IOException e) {
            throw new JenkinsOperateException(e);
        }
    }
}