package com.wangjiang.devops.jenkins.mapper;

import com.wangjiang.devops.jenkins.model.Project;
import com.wangjiang.devops.jenkins.model.ReleaseInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xiniu
 */
@Mapper
@Component
public interface ReleaseInfoMapper {

    /**
     * @param envMark
     * @return
     */
    @Select("select * from release_info where ENV_MARK=#{envMark}")
    @Results(
           value = {
                   @Result(property = "envMark", column = "env_mark"),
                   @Result(property = "projectMark", column = "project_mark"),
                   @Result(property = "releaseVersion", column = "release_version")
           }
    )
    List<ReleaseInfo> findByEnv(@Param("envMark") String envMark);

}
