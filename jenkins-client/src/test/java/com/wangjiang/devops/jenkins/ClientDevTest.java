package com.wangjiang.devops.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.wangjiang.devops.jenkins.config.JenkinsConfig;
import com.wangjiang.devops.jenkins.model.ReleaseInfo;
import com.wangjiang.devops.jenkins.service.JenkinsClient;
import com.wangjiang.devops.jenkins.service.ProjectService;
import com.wangjiang.devops.jenkins.service.ReleaseInfoService;
import com.wangjiang.devops.jenkins.util.Dom4jUtil;
import com.wangjiang.devops.jenkins.vo.EnvVO;
import com.wangjiang.devops.jenkins.vo.ProjectVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientDevTest {

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
        envVO.setApolloMeta("http://apollo-dev1.xiniunet.com");
        envVO.setMark("dev1");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("harbor-dev1.xiniunet.com");
        envVO.setGitCreadential("64f9d1cf-2de8-4398-b828-1a1654adaa13");
    }


    /**
     * 生成 dev1 环境 jenkins job
     */
    @Test
    public void GenerateDev1JobConfigById() throws  IOException, URISyntaxException  {

        Integer id = Integer.valueOf(539);
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        List<ProjectVO> projectVOList =  projectService.getProjects(envVO,id);
        projectVOList.forEach(projectVO -> {
            if(!"web-svc".equals(projectVO.getMark())&&!"web-my".equals(projectVO.getMark())){
                String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
                String jobName = String.format("%s-%s",projectVO.getGroup(),projectVO.getMark());
                Long buidId = jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);
                Assert.notNull(buidId, "job制作失败");
            }

        });
    }










}
