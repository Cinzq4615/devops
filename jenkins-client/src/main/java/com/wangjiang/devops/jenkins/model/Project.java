package com.wangjiang.devops.jenkins.model;
/*
 * @(#)Project.java
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
import org.apache.ibatis.type.Alias;

/**
 * 项目表实体对象.
 * @author 犀牛
 */
@Data
public class Project {

    /**
     * 主键
     */
    private Long id;

    /**
     * 项目所属企业
     */
    private String company;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目所属模块
     */
    private String group;

    /**
     * 代码在哪个group下
     */
    private String gitGroup;

    /**
     * 项目标识
     */
    private String mark;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目类型
     */
    private String type;

    /**
     * 域名
     */
    private String host;

    /**
     * 项目要监听的端口
     */
    private String ports;




    @Override
    public String toString() {
        return super.toString();
    }
}