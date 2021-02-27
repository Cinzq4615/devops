package com.wangjiang.devops.jenkins.util;

import com.wangjiang.devops.jenkins.vo.EnvVO;
import com.wangjiang.devops.jenkins.vo.JenkinsPipelineVo;
import com.wangjiang.devops.jenkins.vo.ProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


@Slf4j
public class Dom4jUtil {

    private static SAXReader reader = new SAXReader();

    private static  final String JOB_CLASS_MAVEN = "hudson.maven.MavenModuleSet";

    private static  final String JOB_CLASS_FREE = "hudson.model.FreeStyleProject";

    private static final String XML_PATH = "/xml.templates";

    public static synchronized String getJenkinsJobXml(JenkinsPipelineVo jenkinsJobVO){
        try {
            String jobxmlPath = String.format("%s/%s", XML_PATH, "defaultJob.xml");;
            String path = Dom4jUtil.class.getResource(jobxmlPath).toString();

            Document document = reader.read(Dom4jUtil.class.getResource(jobxmlPath).getFile());

            // 获取pipeline节点
            Element rootElem = document.getRootElement();
            Element pipelineScript = rootElem.element("definition")
                    .element("script");

            pipelineScript.setText(jenkinsJobVO.getPipelineScript());

            return document.asXML();
        } catch (DocumentException e) {
            log.error("getJenkinsJobXml exception",e);
        }
        return null;
    }


    public static synchronized String generateJobConfig(EnvVO envVO, ProjectVO projectVO){
        try {
            String jobxmlPath = String.format("%s/%s", XML_PATH, "config.xml");
            //根据环境信息 设置环境标识 applo 注册中心地址
            Document document = reader.read(Dom4jUtil.class.getResource(jobxmlPath).getFile());
            // 获取root节点
            Element rootElem = document.getRootElement();
            //获取propery节点
            Element parameterDefinitions =  rootElem.element("properties").element("hudson.model.ParametersDefinitionProperty").element("parameterDefinitions");
            parameterDefinitions.elements().stream().forEach(
                    e -> {
                        Element ele = (Element)e;
                        switch (ele.element("name").getText()){
                            case "APOLLO_META":
                                ele.element("choices").element("a").element("string").setText(envVO.getApolloMeta());
                                 break;
                            case "ENV":
                                ele.element("choices").element("a").element("string").setText(envVO.getMark());
                                break;
                            case "REPOSITORY":
                                ele.element("defaultValue").setText(envVO.getRepository());
                                break;
                            case "TYPE":
                                ele.element("defaultValue").setText(projectVO.getType());
                                break;
                            case "MODULE":
                                if(StringUtils.isNotEmpty(projectVO.getModule())) {
                                    ele.element("defaultValue").setText(projectVO.getModule());
                                }
                                break;
                            case "NAMESPACE":
                                ele.element("choices").element("a").element("string").setText(envVO.getNamespace());
                                break;
                            case "GIT_URL":
                                ele.element("defaultValue").setText(projectVO.getGitUrl());
                                break;
                            case "BRANCH_OR_TAG":
                                ele.element("useRepository").setText(projectVO.getGitUrl());
                                break;
                            case "GIT_CREADENTIAL":
                                ele.element("defaultValue").setText(envVO.getGitCreadential());
                                break;
                            case "HOST":
                                if(StringUtils.isNotEmpty(projectVO.getHost())) {
                                    ele.element("defaultValue").setText(projectVO.getHost());
                                }
                                break;
                            case "INGRESS_ENABLED":
                                ele.element("defaultValue").setText(projectVO.getIngressEnabled());
                                break;
                            case "ENABLED_80":
                                ele.element("defaultValue").setText(projectVO.getEnabled80());
                                break;
                            case "ENABLED_20880":
                                ele.element("defaultValue").setText(projectVO.getEnabled20880());
                                break;
                        }

                    }
            );

            return document.asXML();
        } catch (DocumentException e) {
            log.error("getJenkinsJobXml exception",e);
        }
        return null;
    }

    public static synchronized String generateNginxJobConfig(EnvVO envVO, ProjectVO projectVO){
        try {
            String jobxmlPath = String.format("%s/%s", XML_PATH, "config.xml");
            //根据环境信息 设置环境标识 applo 注册中心地址
            Document document = reader.read(Dom4jUtil.class.getResource(jobxmlPath).getFile());
            // 获取root节点
            Element rootElem = document.getRootElement();
            //获取propery节点
            Element parameterDefinitions =  rootElem.element("properties").element("hudson.model.ParametersDefinitionProperty").element("parameterDefinitions");
            parameterDefinitions.elements().stream().forEach(
                    e -> {
                        Element ele = (Element)e;
                        switch (ele.element("name").getText()){
                            case "APOLLO_META":
                                ele.element("choices").element("a").element("string").setText(envVO.getApolloMeta());
                                break;
                            case "ENV":
                                ele.element("choices").element("a").element("string").setText(envVO.getMark());
                                break;
                            case "REPOSITORY":
                                ele.element("defaultValue").setText(envVO.getRepository());
                                break;
                            case "TYPE":
                                ele.element("defaultValue").setText(projectVO.getType());
                                break;
                            case "MODULE":
                                if(StringUtils.isNotEmpty(projectVO.getModule())) {
                                    ele.element("defaultValue").setText(projectVO.getModule());
                                }
                                break;
                            case "NAMESPACE":
                                ele.element("choices").element("a").element("string").setText(envVO.getNamespace());
                                break;
                            case "GIT_URL":
                                ele.element("defaultValue").setText(projectVO.getGitUrl());
                                break;
                            case "BRANCH_OR_TAG":
                                ele.element("useRepository").setText(projectVO.getGitUrl());
                                break;
                            case "GIT_CREADENTIAL":
                                ele.element("defaultValue").setText(envVO.getGitCreadential());
                                break;
                            case "HOST":
                                if(StringUtils.isNotEmpty(projectVO.getHost())) {
                                    ele.element("defaultValue").setText(projectVO.getHost());
                                }
                                break;
                            case "INGRESS_ENABLED":
                                ele.element("defaultValue").setText(projectVO.getIngressEnabled());
                                break;
                            case "ENABLED_80":
                                ele.element("defaultValue").setText(projectVO.getEnabled80());
                                break;
                            case "ENABLED_20880":
                                ele.element("defaultValue").setText(projectVO.getEnabled20880());
                                break;
                        }

                    }
            );

            return document.asXML();
        } catch (DocumentException e) {
            log.error("getJenkinsJobXml exception",e);
        }
        return null;
    }

}