package com.webank.wedatasphere.exchangis.datasource.loader.loader;

import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.loader.ExchangisDataSourceLoader;
import com.webank.wedatasphere.exchangis.datasource.loader.clazzloader.ExchangisDataSourceClassLoader;
import com.webank.wedatasphere.exchangis.datasource.loader.utils.ExceptionHelper;
import com.webank.wedatasphere.exchangis.datasource.loader.utils.ExtDsUtils;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class LocalExchangisDataSourceLoader implements ExchangisDataSourceLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalExchangisDataSourceLoader.class);

    private ClassLoader classLoader;
    private ExchangisDataSourceContext context;

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setContext(ExchangisDataSourceContext context) {
        this.context = context;
    }

    @Override
    public void init() throws Exception {
        // 初始化磁盘扫描加载
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        String loadClassPath =  Objects.requireNonNull(currentClassLoader.getResource("")).getPath();
        String libPathUrl = loadClassPath + ".." + File.separator + ".." + File.separator + APPCONN_DIR_NAME;
        LOGGER.info("libPath url is {}",  libPathUrl);

        List<URL> jars = ExtDsUtils.getJarsUrlsOfPath(libPathUrl);
        ClassLoader classLoader = new ExchangisDataSourceClassLoader(jars.toArray(new URL[1]), currentClassLoader);

        List<String> classNames = ExtDsUtils.getExchangisExtDataSourceClassNames(libPathUrl, classLoader);
        for (String clazzName: classNames) {
            Class<?> clazz = null;
            try {
                clazz = classLoader.loadClass(clazzName);
            } catch (ClassNotFoundException e) {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
                ExceptionHelper.dealErrorException(70062, clazzName + " class not found ", e, ErrorException.class);
            }

            if (clazz == null) {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            } else {
                ExchangisDataSource exchangisDataSource = (ExchangisDataSource) clazz.newInstance();
                Thread.currentThread().setContextClassLoader(currentClassLoader);
                LOGGER.info("ExchangisDataSource is {}", exchangisDataSource.getClass().toString());

                context.addExchangisDataSource(exchangisDataSource);
            }
        }


//        jars.forEach(jar -> {
//            String path = jar.getPath();
//            System.out.println(path);
//        });
//        for (URL jar: jars) {
//            // 为了加载jar包的类，需要URLClassLoader
//            ;
//            URL[] urls = new URL[]{jar};
//            ClassLoader classLoader = new ExchangisDataSourceClassLoader(urls/*jars.toArray(new URL[1])*/, currentClassLoader);
//
//            Thread.currentThread().setContextClassLoader(classLoader);
//            String fullClassName = null;
//            try {
//                fullClassName = ExtDsUtils.getExchangisExtDataSourceClassName(libPathUrl, classLoader);
//            } catch (NoSuchExchangisExtDataSourceException e) {
//                Thread.currentThread().setContextClassLoader(currentClassLoader);
//                throw e;
//            }
//            System.out.println(libPathUrl);
//            System.out.println(fullClassName);
//
//            Class<?> clazz = null;
//            try {
//                clazz = classLoader.loadClass(fullClassName);
//            } catch (ClassNotFoundException e) {
//                Thread.currentThread().setContextClassLoader(currentClassLoader);
//                ExceptionHelper.dealErrorException(70062, fullClassName + " class not found ", e, ErrorException.class);
//            }
//
//            if (clazz == null) {
//                Thread.currentThread().setContextClassLoader(currentClassLoader);
//            } else {
//                ExchangisDataSource exchangisDataSource = (ExchangisDataSource) clazz.newInstance();
//                Thread.currentThread().setContextClassLoader(currentClassLoader);
//                LOGGER.info("ExchangisDataSource is {}", exchangisDataSource.getClass().toString());
//
//                context.addExchangisDataSource(exchangisDataSource);
//            }
//        }



//        URL finalURL = null;
//        try {
//            String finalUrlStr = libPathUrl.endsWith("/") ? libPathUrl + "*" : libPathUrl + "/*";
//            finalURL = new URL(ExchangisDataSourceLoader.FILE_SCHEMA +  finalUrlStr);
//        } catch (MalformedURLException e) {
//            DSSExceptionUtils.dealErrorException(70061, libPathUrl + " url is wrong", e, ErrorException.class);
//        }
//        List<URL> jars = ExtDsUtils.getJarsUrlsOfPath(libPathUrl);
//        // 为了加载jar包的类，需要URLClassLoader
//        ClassLoader classLoader = new ExchangisDataSourceClassLoader(jars.toArray(new URL[1]), currentClassLoader);
//        Thread.currentThread().setContextClassLoader(classLoader);
//        String fullClassName;
//        try {
//            fullClassName = ExtDsUtils.getExchangisExtDataSourceClassName(dataSourceName, libPathUrl, classLoader);
//        } catch (NoSuchExchangisExtDataSourceException e) {
//            Thread.currentThread().setContextClassLoader(currentClassLoader);
//            throw e;
//        }
//        Class<?> clazz = null;
//        try {
//            clazz = classLoader.loadClass(fullClassName);
//        } catch (ClassNotFoundException e) {
//            Thread.currentThread().setContextClassLoader(currentClassLoader);
//            DSSExceptionUtils.dealErrorException(70062, fullClassName + " class not found ", e, ErrorException.class);
//        }
//        if (clazz == null) {
//            Thread.currentThread().setContextClassLoader(currentClassLoader);
//            return null;
//        } else {
//            AppConn retAppConn = (AppConn) clazz.newInstance();
//            Thread.currentThread().setContextClassLoader(currentClassLoader);
//            LOGGER.info("AppConn is {},  retAppConn is {}", appConnName, retAppConn.getClass().toString());
//            return retAppConn;
//        }

    }

    @Override
    public ExchangisDataSource load(String dataSourceType) {
        return null;
    }

    @Override
    public ExchangisDataSource get(String dataSourceType, boolean reload) {
        return null;
    }
}
