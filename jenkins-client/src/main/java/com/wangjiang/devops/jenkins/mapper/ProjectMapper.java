package com.wangjiang.devops.jenkins.mapper;

import com.wangjiang.devops.jenkins.model.Project;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ProjectMapper {

    @Select("select * from project where type in ('jdk','jetty')")
    @Results(
           value = {
                   @Result(property = "typeName", column = "type_name"),
                   @Result(property = "gitGroup", column = "git_group")
           }
    )
    List<Project> findAll();

    @Select("select * from project where type in ('nginx')")
    @Results(
            value = {
                    @Result(property = "typeName", column = "type_name"),
                    @Result(property = "gitGroup", column = "git_group")
            }
    )
    List<Project> findAllNginx();

    @Select("select * from project where id=#{id}")
    @Results(
            value = {
                    @Result(property = "typeName", column = "type_name"),
                    @Result(property = "gitGroup", column = "git_group")
            }
    )
    List<Project> findAllById(@Param("id") Integer id);

}
