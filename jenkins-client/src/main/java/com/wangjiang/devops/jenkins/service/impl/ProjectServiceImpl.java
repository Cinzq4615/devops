package com.wangjiang.devops.jenkins.service.impl;

import com.wangjiang.devops.jenkins.mapper.ProjectMapper;
import com.wangjiang.devops.jenkins.model.Project;
import com.wangjiang.devops.jenkins.service.ProjectService;
import com.wangjiang.devops.jenkins.vo.EnvVO;
import com.wangjiang.devops.jenkins.vo.ProjectVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangqun
 * @create 2021-02-05 13:14
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    public static final String PORT_80 ="80";

    public static final String PORT_20880 = "20880";

    public static final String GIT_URL_TEMPLATE = "http://gitlab.xiniunet.com/%s/%s.git";

    public static final String MODULE_TEMPLATE = "%s-business";

    @Autowired
    ProjectMapper projectMapper;

    @Override
    public List<ProjectVO> getProjects(EnvVO envVO){
        List<Project> projects =  projectMapper.findAll();
        List<ProjectVO> projectVOS = new ArrayList<>(projects.size());
        projects.forEach(
                project -> {
                    ProjectVO projectVO = new ProjectVO();
                    projectVO.setType(project.getType());
                    projectVO.setMark(project.getMark());
                    projectVO.setGroup(project.getGitNamespace());
                    projectVO.setGitUrl(String.format(GIT_URL_TEMPLATE,project.getGitNamespace(),project.getMark()));
                    if(PORT_80.equals(project.getPorts().trim())){
                        projectVO.setEnabled80("true");
                        projectVO.setEnabled20880("false");
                        projectVO.setIngressEnabled("true");
                        if(StringUtils.isNotEmpty(project.getDescription())) {
                            projectVO.setHost(String.format("%s-%s.xiniunet.com", project.getDescription().split("\\.")[0], envVO.getMark()));
                        }
                    }else if(PORT_20880.equals(project.getPorts().trim())){
                        projectVO.setEnabled80("false");
                        projectVO.setEnabled20880("true");
                        projectVO.setIngressEnabled("false");
                        projectVO.setModule(String.format(MODULE_TEMPLATE,project.getMark().trim()));
                    }else{
                        projectVO.setEnabled80("true");
                        projectVO.setEnabled20880("true");
                        projectVO.setIngressEnabled("true");
                        if(StringUtils.isNotEmpty(project.getDescription())) {
                            projectVO.setHost(String.format("%s-%s.xiniunet.com", project.getDescription().split("\\.")[0], envVO.getMark()));
                        }
                    }
                    projectVOS.add(projectVO);

                }
        );
        return projectVOS;
    }
}
