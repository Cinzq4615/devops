package com.wangjiang.devops.jenkins.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangqun
 * @create 2021-02-02 18:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO {

    private String type;

    private String gitUrl;

    private String module;

    private String host;

    private String enabled80;

    private String enabled20880;

    private String ingressEnabled;

    private String mark;

    private String group;

}
