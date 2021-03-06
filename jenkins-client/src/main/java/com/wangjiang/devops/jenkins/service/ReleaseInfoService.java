package com.wangjiang.devops.jenkins.service;

import com.wangjiang.devops.jenkins.model.ReleaseInfo;
import com.wangjiang.devops.jenkins.vo.EnvVO;
import com.wangjiang.devops.jenkins.vo.ProjectVO;

import java.util.List;

/**
 * @author zhangqun
 * @create 2021-02-05 13:13
 */
public interface ReleaseInfoService {
    List<ReleaseInfo> getReleaseInfos(String envMark);
}
