package com.wangjiang.devops.jenkins.model;
/*
 * @(#)InfoPO.java
 *
 * Copyright (c) 2014-2017  苏州犀牛网络科技有限公司 版权所有
 * xiniunet. All rights reserved.
 *
 * This software is the confidential and proprietary
 * information of  xiniunet.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with xiniunet.
 */


import lombok.Data;

/**
 * 发布信息的数据库模型.
 * @author 犀牛
 */
@Data
public class ReleaseInfo {

    /**
     * 主键,
     */
    private Long id;

    /**
     * 环境标识,
     */
    private String envMark;

    /**
     * 项目标识,
     */
    private String projectMark;

    /**
     * 发布版本 branch 或 tag,
     */
    private String releaseVersion;

    /**
     * 版本信息,
     */
    private String description;

}