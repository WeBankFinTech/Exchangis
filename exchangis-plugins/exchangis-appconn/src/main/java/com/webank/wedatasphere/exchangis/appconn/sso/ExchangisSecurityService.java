package com.webank.wedatasphere.exchangis.appconn.sso;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ExchangisSecurityService {
    private static Logger LOGGER = LoggerFactory.getLogger(ExchangisSecurityService.class);

    private static final String SESSION_ID_KEY = "example.browser.session.id";

    private static ExchangisSecurityService instance;

    private static Properties userToken;

    private String securityUrl = "";

    private Cache<String, Cookie> cookieCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1800L, TimeUnit.SECONDS)
            .build();

    private ExchangisSecurityService(String baseUrl) {
        this.securityUrl = baseUrl.endsWith("/") ? (baseUrl + "dss-appconn-example-instance/user/login") : (baseUrl + "/dss-appconn-example-instance/user/login");
    }

    public static ExchangisSecurityService getInstance(String baseUrl) {
        if (null == instance)
            synchronized (ExchangisSecurityService.class) {
                if (null == instance)
                    instance = new ExchangisSecurityService(baseUrl);
            }
        return instance;
    }

    public Cookie login(String user) throws Exception {
        System.out.println("before synchronized login ");
        synchronized (user.intern()) {
            System.out.println("do login....");
            Cookie session = (Cookie)this.cookieCache.getIfPresent(user);
            if (session != null)
                return session;
            System.out.println("before getCookie()");
            Cookie newCookie = getCookie(user, getUserToken(user));
            System.out.println("newCookie => " + newCookie);
            this.cookieCache.put(user, newCookie);
            return newCookie;
        }
    }

    private String getUserToken(String user) {
        Object token = userToken.get(user);
        if (token == null)
            return "";
        System.out.println("token.toString() => " + token);
        return token.toString();
    }

    private Cookie getCookie(String user, String token) throws Exception {
        HttpClientContext context;
        String responseContent;
        System.out.println(this.securityUrl);
        HttpPost httpPost = new HttpPost(this.securityUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", "nutsjian"));
        params.add(new BasicNameValuePair("password", "abc123"));
        httpPost.setEntity((HttpEntity)new UrlEncodedFormEntity(params));
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(basicCookieStore).build();
            context = HttpClientContext.create();
            response = httpClient.execute(httpPost, context);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "utf-8");
            LOGGER.info("Get Example AppConn Instance response code is " + response.getStatusLine().getStatusCode() + ",response: " + responseContent);
            if (response.getStatusLine().getStatusCode() != 200)
                throw new ErrorException(90041, responseContent);
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        List<Cookie> cookies = context.getCookieStore().getCookies();
        Optional<Cookie> cookie = cookies.stream().filter(this::findSessionId).findAny();
        return cookie.orElseThrow(() -> new ErrorException(90041, "Get example session is null : " + responseContent));
    }

    private boolean findSessionId(Cookie cookie) {
        return "example.browser.session.id".equals(cookie.getName());
    }
}
