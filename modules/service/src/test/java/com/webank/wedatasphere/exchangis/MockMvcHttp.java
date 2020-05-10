package com.webank.wedatasphere.exchangis;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * MockMvc utils
 * @author davidhua
 * 2018/9/5
 */
public class MockMvcHttp {
    private static final String DEFAULT_ENCODING = "utf-8";

    private static Cookie[] cookies = new Cookie[]{new Cookie("mockMvc", "default")};

    public static void cookie(Cookie... cookies){
        MockMvcHttp.cookies = cookies;
    }

    public static void mockJson(MockMvc mockMvc, String uri,
                                String type, String json) throws Exception{
        MockHttpServletRequestBuilder builder = checkType(uri, type);
        if(null == json){
            json = "";
        }

        if(null != builder){
            mockMvc.perform(builder.characterEncoding(DEFAULT_ENCODING)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(cookies)
                    .content(json))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    public static void mockParams(MockMvc mockMvc, String uri,
                                  String type, Map<String, String> params) throws Exception{
        MockHttpServletRequestBuilder builder = checkType(uri, type);
        if(null != builder){
            if(null != params && params.size() > 0){
                for(Map.Entry<String, String> entry : params.entrySet()){
                    builder = builder.param(entry.getKey(), entry.getValue());
                }
            }
            mockMvc.perform(builder.characterEncoding(DEFAULT_ENCODING)
                    .accept(MediaType.ALL_VALUE)
                    .cookie(cookies)
                    .contentType(MediaType.ALL_VALUE))
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }

    public static void mockPages(MockMvc mockMvc, String uri, String forward) throws Exception{
        mockMvc.perform(get(uri))
            .andExpect(status().isOk())
            .andExpect(forwardedUrlPattern(forward)).andDo(print());
    }

    public static void mockFile(MockMvc mockMvc, String uri, String name, String path,
                                Map<String, String> params) throws Exception{
        MockMultipartHttpServletRequestBuilder builder = fileUpload(uri);
        InputStream in = new FileInputStream(path);
        MockMultipartFile mockFile = new MockMultipartFile(name, in);
        builder = builder.file(mockFile);
        MockHttpServletRequestBuilder b = builder;
        if(null != params && params.size() > 0){
            for(Map.Entry<String, String> entry : params.entrySet()){
                b = b.param(entry.getKey(), entry.getValue());
            }
        }
        mockMvc.perform(b.cookie(cookies)
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
                .andDo(print());
    }

    private static MockHttpServletRequestBuilder checkType(String uri, String type){
        MockHttpServletRequestBuilder builder = null;
        switch(type){
            case "post": return post(uri);
            case "get": return get(uri);
            case "put": return put(uri);
            case "delete": return delete(uri);
        }
        return builder;
    }
}
