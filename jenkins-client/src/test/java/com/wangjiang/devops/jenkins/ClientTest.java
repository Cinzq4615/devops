package com.wangjiang.devops.jenkins;

import com.alibaba.fastjson.JSON;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Job;
import com.wangjiang.devops.jenkins.config.JenkinsConfig;
import com.wangjiang.devops.jenkins.mapper.ProjectMapper;
import com.wangjiang.devops.jenkins.model.Project;
import com.wangjiang.devops.jenkins.service.JenkinsClient;
import com.wangjiang.devops.jenkins.service.ProjectService;
import com.wangjiang.devops.jenkins.util.Dom4jUtil;
import com.wangjiang.devops.jenkins.vo.EnvVO;
import com.wangjiang.devops.jenkins.vo.JenkinsPipelineVo;
import com.wangjiang.devops.jenkins.vo.PipeLineStage;
import com.wangjiang.devops.jenkins.vo.ProjectVO;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientTest {

    private static final String JOB_TYPE = "org.jenkinsci.plugins.workflow.job.WorkflowJob";

    @Autowired
    private JenkinsServerHolder jenkinsServerHolder;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JenkinsClient jenkinsClient;

    @Autowired
    private JenkinsConfig jenkinsConfig;

    @Test
    public void TestJobInfo() {
        JenkinsServer jenkinsServer = jenkinsServerHolder.getJenkinsServer("20", "192.168.33.20", null);
        try {
            Map<String, Job> jobs = jenkinsServer.getJobs();
            for (String k : jobs.keySet()) {
                Job job = jobs.get(k);
                System.out.println(k);
                System.out.println(job);
                System.out.println("=========");
                String jobXml = jenkinsServer.getJobXml(k);
                System.out.println(jobXml);
                System.out.println("=========");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.notNull(jenkinsServer, "对象为空");
    }

    /**
     * 生成 daily 环境 jenkins job
     */
    @Test
    public void GenerateDailyJobConfig() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://conf-meta-daily.xiniunet.com:8007");
        envVO.setMark("daily");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("192.168.20.200:50000");
        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
            String jobName = String.format("%s-%s-%s",projectVO.getGroup(),projectVO.getMark(),envVO.getMark());
            Long buidId = jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);

            JenkinsHttpClient jenkinsHttpClient = null;
            try {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                jenkinsHttpClient.post(String.format(jenkinsConfig.getViewUrl(),envVO.getMark(), jobName),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void deleteDailyJobConfig() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://conf-meta-daily.xiniunet.com:8007");
        envVO.setMark("daily");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("192.168.20.200:50000");
        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
            //删除 开始
            String jobName = String.format("%s-%s",projectVO.getMark(),envVO.getMark());
            jenkinsClient.deleteJob(jenkinsServer,jobName,true);
            //删除 结束
        });
    }

    @Test
    public void deleteTestJobConfig() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://conf-meta-test.xiniunet.com:8009");
        envVO.setMark("test");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("192.168.20.200:50000");
        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
            //删除 开始
            String jobName = String.format("%s-%s",projectVO.getMark(),envVO.getMark());
            jenkinsClient.deleteJob(jenkinsServer,jobName,true);
            //删除 结束
        });
    }

    /**
     * 生成 test 环境 jenkins job
     */
    @Test
    public void GenerateTestJobConfig() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://conf-meta-test.xiniunet.com:8009");
        envVO.setMark("test");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("192.168.20.200:50000");
        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
            String jobName = String.format("%s-%s-%s",projectVO.getGroup(),projectVO.getMark(),envVO.getMark());
            Long buidId = jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);

            JenkinsHttpClient jenkinsHttpClient = null;
            try {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                jenkinsHttpClient.post(String.format(jenkinsConfig.getViewUrl(),envVO.getMark(), jobName),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 生成 dev 环境 jenkins job
     */
    @Test
    public void GenerateDevJobConfig() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://conf-meta-dev.xiniunet.com:8005");
        envVO.setMark("dev");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("192.168.20.200:50000");
        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
            String jobName = String.format("%s-%s-%s",projectVO.getGroup(),projectVO.getMark(),envVO.getMark());
            Long buidId = jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);

            JenkinsHttpClient jenkinsHttpClient = null;
            try {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                jenkinsHttpClient.post(String.format(jenkinsConfig.getViewUrl(),envVO.getMark(), jobName),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 生成 dev1 环境 jenkins job
     */
    @Test
    public void GenerateDev1JobConfig() throws  IOException, URISyntaxException  {
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://apollo-dev1.xiniunet.com");
        envVO.setMark("dev1");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("harbor-dev1.xiniunet.com");
        List<ProjectVO> projectVOList =  projectService.getProjects(envVO);
        projectVOList.forEach(projectVO -> {
            String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
            String jobName = String.format("%s-%s",projectVO.getGroup(),projectVO.getMark());
            Long buidId = jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);

            JenkinsHttpClient jenkinsHttpClient = null;
            try {
                jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try {
                jenkinsHttpClient.post(String.format(jenkinsConfig.getViewUrl(),envVO.getMark(), jobName),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * getProjects
     */
    @Test
    public void testGetProjects() throws IOException {
        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://conf-meta-daily.xiniunet.com:8007");
        envVO.setMark("daily");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("192.168.20.200:50000");

        ProjectVO projectVO = new ProjectVO("jdk", "http://gitlab.xiniunet.com/rd/log.git", "log-businiess",
                " ", "false", "true", "false","log","rd");
        File file = new File("D:\\workspace\\devops\\jenkins-client\\src\\main\\resources\\xml.templates\\log-config.xml");
        FileUtils.writeStringToFile(file,Dom4jUtil.generateJobConfig(envVO,projectVO));
    }

    /**
     * 测试生成jenkins job
     */
    @Test
    public void TestCreateJob() throws IOException, URISyntaxException {

        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());

        EnvVO envVO = new EnvVO();
        envVO.setApolloMeta("http://conf-meta-daily.xiniunet.com:8007");
        envVO.setMark("daily");
        envVO.setNamespace("xiniunet");
        envVO.setRepository("192.168.20.200:50000");

        ProjectVO projectVO = new ProjectVO("jdk", "http://gitlab.xiniunet.com/rd/log.git", "log-business",
                " ", "false", "true", "false","log","rd");
        File file = new File("D:\\workspace\\devops\\jenkins-client\\src\\main\\resources\\xml.templates\\log-config.xml");
        String jobxml = Dom4jUtil.generateJobConfig(envVO,projectVO);
        String jobName = String.format("%s-%s",projectVO.getMark(),envVO.getMark());
        Long buidId = jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, jobName);

        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI(jenkinsConfig.getUrl()), jenkinsConfig.getUsername(), jenkinsConfig.getPassword());
        jenkinsHttpClient.post(String.format(jenkinsConfig.getViewUrl(),envVO.getMark(), jobName),true);

        Assert.notNull(buidId, "对象制作失败");
    }

    /**
     * 测试任务创建
     */
    @Test
    public void TestJobCreate() {

        JenkinsServer jenkinsServer = jenkinsServerHolder.getJenkinsServer("dev", "192.168.33.20", null);

        JenkinsPipelineVo pipelineVo = new JenkinsPipelineVo();
        // 流水线类型 1. tomcat 2. jdk 3. npm
        pipelineVo.setType("tomcat");
        //1. 拉取代码阶段
        PipeLineStage gitStage = new PipeLineStage("git stage");
        gitStage.appendScript("echo '==== git pull start ===='");
        gitStage.appendScript("echo '||'");
        gitStage.appendScript("git branch: 'master', credentialsId: '02550e58-1a5c-40d8-9d67-efd78eff2436', url: " +
                "'http://192.168.33.11:10080/root/ordev-svc.git'");
        gitStage.appendScript("env.check_to_tag='v1.0.0'");
        gitStage.appendScript("sh '[ -n \"${check_to_tag}\" ] &&  git checkout ${check_to_tag} ||  { echo -e " +
                "\"切换至指定的tag的版本，tag：${check_to_tag} 不存在或为空，请检查输入的tag!\" && exit 111; }'");
        gitStage.appendScript("echo '||'");
        gitStage.appendScript("echo '==== git pull end   ===='");

        //2. mvn 打包阶段
        PipeLineStage mvnStage = new PipeLineStage("mvn build stage");
        mvnStage.appendScript("echo '==== mvn begin ===='");
        mvnStage.appendScript("echo '||'");
        mvnStage.appendShellScript("mvn clean package -Dmaven.test.skip=true  -Dmaven.javadoc.skip=true -Dmaven" +
                ".compile.fork=true -P local -T 1C");
        mvnStage.appendScript("echo '||'");
        mvnStage.appendScript("echo '==== mvn end   ===='");

        //3. docker 镜像构建并推送到镜像库
        PipeLineStage dockerStage = new PipeLineStage("docker build and push stage");
        String imageVersion = "v1.0.0";
        dockerStage.appendScript("echo '==== docker build and push begin ===='");
        dockerStage.appendShellScript("mv ./order-api/target/*.jar ./docker/");
        dockerStage.appendShellScript("pwd");
        dockerStage.appendShellScript("cd ./docker && docker build -t 192.168.33.20/java-test/k8s-order-svc:" + imageVersion + " .");
        dockerStage.appendShellScript("pwd");
        dockerStage.appendShellScript("docker push 192.168.33.20/java-test/k8s-order-svc:" + imageVersion);
        dockerStage.appendScript("echo '==== docker build and push end   ===='");

        // 4. helm包制作并生成helm包并推送到仓库
        PipeLineStage helmStage = new PipeLineStage("helm stage");
        helmStage.appendScript("echo '==== helm stage begin ===='");
        helmStage.appendScript("echo 'processing...'");
        helmStage.appendScript("echo '==== helm stage end ===='");

        // 需要顺序放入
        pipelineVo.setPipeLineStages(Arrays.asList(gitStage, mvnStage, dockerStage, helmStage));

        String jobxml = Dom4jUtil.getJenkinsJobXml(pipelineVo);
        Long test_pipeline_v2 = jenkinsClient.createJob(jenkinsServer, jobxml, JOB_TYPE, "test_pipeline_v2");
        Assert.notNull(test_pipeline_v2, "对象制作失败");
    }

}
