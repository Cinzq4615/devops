package com.wangjiang.devops.jenkins;

import com.wangjiang.devops.jenkins.model.Project;
import com.wangjiang.devops.jenkins.model.ReleaseInfo;
import org.apache.ibatis.type.MappedTypes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wangjiang.devops.jenkins.mapper")
@MappedTypes({Project.class, ReleaseInfo.class})
public class RunApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunApplication.class, args);
    }
}
