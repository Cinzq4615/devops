package com.wangjiang.devops.jenkins.mapper;

import com.wangjiang.devops.jenkins.model.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ProjectMapper {

    @Select("select * from project where type in ('jdk','jetty')")
    @Results(
           value = {
                   @Result(property = "typeName", column = "type_name"),
                   @Result(property = "gitNamespace", column = "git_namespace")
           }
    )
    List<Project> findAll();

}
