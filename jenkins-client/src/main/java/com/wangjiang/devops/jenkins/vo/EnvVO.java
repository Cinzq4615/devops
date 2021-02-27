package com.wangjiang.devops.jenkins.vo;

import lombok.Data;

/**
 * @author zhangqun
 * @create 2021-02-02 18:38
 */
@Data
public class EnvVO {

    private String mark;

    private String apolloMeta;

    private String repository;

    //默认发布空间
    private String namespace;

    //git 凭据
    private String gitCreadential;

}
