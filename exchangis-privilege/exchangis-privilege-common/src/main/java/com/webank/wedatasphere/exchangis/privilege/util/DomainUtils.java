package com.webank.wedatasphere.exchangis.privilege.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainUtils {

    private static final Pattern DOMAIN_REGEX = Pattern.compile("[a-zA-Z][a-zA-Z0-9\\.]+");
    private static final Pattern IP_REGEX = Pattern.compile("([^:]+):.+");
    private static final Integer DEFAULT_DOMAIN_LEVEL = 3;

    public static String getCookieDomain(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null) {
            referer = request.getServerName();
        }
        return getCookieDomain(referer);
    }

    public static String getCookieDomain(String host) {
        int level = DEFAULT_DOMAIN_LEVEL;
        if (host.startsWith("https://")) {
            host = host.substring(8);
        } else if (host.startsWith("http://")) {
            host = host.substring(7);
        }
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        if (host.contains(":")) {
            host = host.substring(0, host.lastIndexOf(":"));
        }
        if (DOMAIN_REGEX.matcher(host).find()) {
            String[] domains = host.split("\\.");
            int index = level;
            if (domains.length == level) {
                index = level - 1;
            } else if (domains.length < level) {
                index = domains.length;
            }
            if (index < 0) {
                return host;
            }
            String[] parsedDomains = Arrays.copyOfRange(domains, index, domains.length);
            if (parsedDomains.length < level) {
                return host;
            }
            String domain = String.join(".", parsedDomains);
            if (domains.length >= level) {
                return "." + domain;
            }
            return domain;
        }
        Matcher matcher = IP_REGEX.matcher(host);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return host;
        }
    }
}
