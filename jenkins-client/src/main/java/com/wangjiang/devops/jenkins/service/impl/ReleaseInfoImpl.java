package com.wangjiang.devops.jenkins.service.impl;

import com.wangjiang.devops.jenkins.mapper.ProjectMapper;
import com.wangjiang.devops.jenkins.mapper.ReleaseInfoMapper;
import com.wangjiang.devops.jenkins.model.Project;
import com.wangjiang.devops.jenkins.model.ReleaseInfo;
import com.wangjiang.devops.jenkins.service.ProjectService;
import com.wangjiang.devops.jenkins.service.ReleaseInfoService;
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
public class ReleaseInfoImpl implements ReleaseInfoService {


    @Override
    public List<ReleaseInfo> getReleaseInfos(String envMark) {
        return releaseInfoMapper.findByEnv(envMark);
    }

    @Autowired
    private ReleaseInfoMapper releaseInfoMapper;
}
