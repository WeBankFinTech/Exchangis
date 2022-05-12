package com.webank.wedatasphere.exchangis.job.server.utils;

import com.webank.wedatasphere.exchangis.project.server.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author tikazhang
 * @Date 2022/5/10 20:10
 */
public class AuthorityUtils {

    @Autowired
    private static ProjectMapper projectMapper;

    public static boolean hasViewAuthority(long projectId, String loginUser) {
        List<String> authoritis = projectMapper.getAuthoritis(projectId, loginUser);
        if (authoritis.contains("1")) {
            return true;
        }
        return false;
    }

    public static boolean hasExecAuthority(long projectId, String loginUser) {
        List<String> authoritis = projectMapper.getAuthoritis(projectId, loginUser);
        if (authoritis.contains("2")) {
            return true;
        }
        return false;
    }

    public static boolean hasEditAuthority(long projectId, String loginUser) {
        List<String> authoritis = projectMapper.getAuthoritis(projectId, loginUser);
        if (authoritis.contains("3")) {
            return true;
        }
        return false;
    }

    public static boolean hasOwnAuthority(long projectId, String loginUser) {
        List<String> authoritis = projectMapper.getAuthoritis(projectId, loginUser);
        if (authoritis.contains("0")) {
            return true;
        }
        return false;
    }
}
