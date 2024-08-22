package com.webank.wedatasphere.exchangis.common;

import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import org.apache.linkis.server.security.ProxyUserSSOUtils;
import org.apache.linkis.server.security.SecurityFilter;
import scala.Option;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tikazhang
 * @Date 2022/9/22 16:54
 */
public class UserUtils {
    public static String getLoginUser(HttpServletRequest request) {
        Option<String> proxyUserUsername =
                ProxyUserSSOUtils.getProxyUserUsername(request);
        String loginUser = null;
        if (proxyUserUsername.isDefined() &&
                GlobalConfiguration.PROXY_MODE.getValue()) {
            loginUser = proxyUserUsername.get();
        } else {
            loginUser = SecurityFilter.getLoginUsername(request);
        }
        return loginUser;
    }

}
