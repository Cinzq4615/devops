package com.wangjiang.devops.jenkins.service.impl;

import com.wangjiang.devops.jenkins.mapper.ProjectMapper;
import com.wangjiang.devops.jenkins.model.Project;
import com.wangjiang.devops.jenkins.service.ProjectService;
import com.wangjiang.devops.jenkins.vo.EnvVO;
import com.wangjiang.devops.jenkins.vo.ProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    public static final String PORT_80 ="80";

    public static final String PORT_20880 = "20880";

    public static final String GIT_URL_TEMPLATE = "git@gitlab.xiniunet.com:%s/%s.git";

    public static final String MODULE_TEMPLATE = "%s-business";

    private static final String ENV_PRE="pre";

    private static final String ENV_PROD="prod";

    @Autowired
    ProjectMapper projectMapper;

    @Override
    public List<ProjectVO> getProjects(EnvVO envVO){
        List<Project> projects =  projectMapper.findAll();
        if(!CollectionUtils.isEmpty(projects)){
            return generateProjectVOs(envVO,projects);
        }else{
            log.error("项目类型%d:不存在","jdk,jetty");
            return null;
        }
    }

    @Override
    public List<ProjectVO> getProjects(EnvVO envVO, String type) {
        List<Project> projects =  projectMapper.findAllNginx();
        if(!CollectionUtils.isEmpty(projects)){
            return generateProjectVOs(envVO,projects);
        }else{
            log.error("项目类型%d:不存在","nginx");
            return null;
        }
    }

    @Override
    public List<ProjectVO> getProjects(EnvVO envVO,Integer id){
        List<Project> projects =  projectMapper.findAllById(id);
        if(!CollectionUtils.isEmpty(projects)){
            return generateProjectVOs(envVO,projects);
        }else{
            log.error("项目%d:不存在",id);
            return null;
        }
    }

    private List<ProjectVO> generateProjectVOs(EnvVO envVO,List<Project> projects){
        List<ProjectVO> projectVOS = new ArrayList<>(projects.size());
        projects.forEach(
                project -> {
                    ProjectVO projectVO = new ProjectVO();
                    projectVO.setType(project.getType());
                    projectVO.setMark(project.getMark());
                    projectVO.setGroup(project.getGitGroup());
                    projectVO.setGitUrl(String.format(GIT_URL_TEMPLATE,project.getGitGroup(),project.getMark()));
                    if(PORT_80.equals(project.getPorts().trim())){
                        projectVO.setEnabled80("true");
                        projectVO.setEnabled20880("false");
                        projectVO.setIngressEnabled("true");
                        if(StringUtils.isNotEmpty(project.getHost())) {
                            if(ENV_PRE.equals(envVO.getMark())||ENV_PROD.equals(envVO.getMark())){
                                projectVO.setHost(String.format("%s.xiniunet.com", project.getHost().split("\\.")[0]));
                            }else{
                                projectVO.setHost(String.format("%s-%s.xiniunet.com", project.getHost().split("\\.")[0], envVO.getMark()));
                            }
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
                        if(StringUtils.isNotEmpty(project.getHost())) {
                            if(ENV_PRE.equals(envVO.getMark())||ENV_PROD.equals(envVO.getMark())){
                                projectVO.setHost(String.format("%s.xiniunet.com", project.getHost().split("\\.")[0]));
                            }else{
                                projectVO.setHost(String.format("%s-%s.xiniunet.com", project.getHost().split("\\.")[0], envVO.getMark()));
                            }
                        }
                    }
                    projectVOS.add(projectVO);
                }
        );
        return projectVOS;
    }
}
