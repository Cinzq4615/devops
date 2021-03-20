package com.wangjiang.devops.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import com.wangjiang.devops.jenkins.config.JenkinsConfig;
import com.wangjiang.devops.jenkins.model.ReleaseInfo;
import com.wangjiang.devops.jenkins.service.JenkinsClient;
import com.wangjiang.devops.jenkins.service.ProjectService;
import com.wangjiang.devops.jenkins.service.ReleaseInfoService;
import com.wangjiang.devops.jenkins.util.Dom4jUtil;
import com.wangjiang.devops.jenkins.vo.EnvVO;
import com.wangjiang.devops.jenkins.vo.JenkinsPipelineVo;
import com.wangjiang.devops.jenkins.vo.PipeLineStage;
import com.wangjiang.devops.jenkins.vo.ProjectVO;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientProdTest {

    private static final String JOB_TYPE = "org.jenkinsci.plugins.workflow.job.WorkflowJob";

    @Autowired
    private JenkinsServerHolder jenkinsServerHolder;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JenkinsClient jenkinsClient;

    @Autowired
    private JenkinsConfig jenkinsConfig;

    @Autowired
    private ReleaseInfoService releaseInfoService;


    private static final String NAME_BRANCH_OR_TAG="BRANCH_OR_TAG";

    private static final String PROJECT_TYPE_JDK="jdk";

    private static final String PROJECT_TYPE_JETTY="jetty";

    private static final String JOB_PREFIX_PRE_WEB="w";

    private static final String JOB_PREFIX_PRE_APP="s";

    private EnvVO envVO=new EnvVO();

    @Before
    public void setUp() throws Exception {
        envVO.setApolloMeta("http://conf-meta-pre.xiniunet.com:8001/");
        envVO.setMark("prod");
        envVO.setNamespace("xn-prod");
        envVO.setRepository("harbor.xiniunet.com");
        envVO.setGitCreadential("c5baf3f6-e189-4aa7-8cd0-f0b0c49d3a44");
    }



    @Test
    public void deletePreJob() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        //String prefix ="";
        projectVOList.forEach(projectVO -> {
            String jobName;
            if("true".equals(projectVO.getIngressEnabled())){
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_WEB,projectVO.getGroup(),projectVO.getMark());
            }else{
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_APP,projectVO.getGroup(),projectVO.getMark());
            }
            //删除 开始
            jenkinsClient.deleteJob(jenkinsServer,jobName,true);
            //删除 结束
        });
    }

    /**
     * 生成 pre 环境 jenkins job
     */
    @Test
    public void GeneratePreJob() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateProdJobConfig(envVO,projectVO);
            String jobName;
            if("true".equals(projectVO.getIngressEnabled())){
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_WEB,projectVO.getGroup(),projectVO.getMark());
            }else{
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_APP,projectVO.getGroup(),projectVO.getMark());
            }
            jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);

            JenkinsHttpClient jenkinsHttpClient = null;
            try {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                jenkinsHttpClient.post(String.format(jenkinsConfig.getViewUrl(),envVO.getMark().toUpperCase(), jobName),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 生成 pre 环境
     */
    @Test
    public void GeneratePreJobById() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
        Integer projectId = Integer.valueOf(92);
        List<ProjectVO> projectVOList =  projectService.getProjects(envVO,projectId);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateProdJobConfig(envVO,projectVO);
            String jobName;
            if("true".equals(projectVO.getIngressEnabled())){
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_WEB,projectVO.getGroup(),projectVO.getMark());
            }else{
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_APP,projectVO.getGroup(),projectVO.getMark());
            }
            jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);

            JenkinsHttpClient jenkinsHttpClient = null;
            try {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                jenkinsHttpClient.post(String.format(jenkinsConfig.getViewUrl(),envVO.getMark().toUpperCase(), jobName),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    /**
     * 发布 test1 环境 jenkins job
     */
    @Test
    public void BuildPreJdkAndJettyJob() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());


        //获取发布信息
        List<ReleaseInfo> releaseInfoList =  releaseInfoService.getReleaseInfos(envVO.getMark());
        Map<String,String> releaseMap =  new HashMap<>(releaseInfoList.size());
        if(!CollectionUtils.isEmpty(releaseInfoList)) {
            releaseInfoList.forEach(info -> {
                releaseMap.put(info.getProjectMark(), info.getReleaseVersion());
            });
        }

        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobName;
            if("true".equals(projectVO.getIngressEnabled())){
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_WEB,projectVO.getGroup(),projectVO.getMark());
            }else{
                jobName = String.format("%s-%s-%s",JOB_PREFIX_PRE_APP,projectVO.getGroup(),projectVO.getMark());
            }
            Map<String,String> prams =  new HashMap<>();
            if(releaseMap.get(projectVO.getMark())!=null){
                prams.put(NAME_BRANCH_OR_TAG,releaseMap.get(projectVO.getMark()));
            }
            jenkinsClient.build(jenkinsServer,jobName,prams);
        });

    }
}
