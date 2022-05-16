package com.webank.wedatasphere.exchangis.metrics.api;

import com.webank.wedatasphere.exchangis.metrics.Metric;

import java.lang.reflect.Method;

/**
 * The design concept is heavily borrowed from alibaba metrics.
 *
 * alibaba metrics
 * ====================================
 * The design concept is heavily borrowed from SLF4j (http://www.slf4j.org/), the logging framework.
 * The application only depends on the metrics api.
 * The implementation will be dynamically bound.
 * If the implementation if not found in classpath, by default the {@link NOPMetricManager} will be bound.
 *
 * 这里采用了 alibaba metrics 代码，在其之上做了一些简化，取消了 group 分组，目前只考虑针对 exchangis 的服务做一些监控
 */
public class MetricManager {

    private static final String BINDER_CLASS = "com.webank.wedatasphere.exchangis.metrics.api.MetricManagerBinder";
    public static final IMetricManager NOP_METRIC_MANAGER = new NOPMetricManager();

    private static volatile IMetricManager iMetricManager;

    /**
     * Create a {@link Counter} metric in given group, and name.
     * if not exist, an instance will be created.
     * 根据给定的group和name, 获取一个Counter实例，如果不存在则会创建
     * Counter(计数器), 主要用于用于计数，支持+1, -1, +n, -n等操作
     *
     * @param name the name of the metric
     * @return an instance of counter
     */
    public static Counter getCounter(MetricName name) {
        IMetricManager manager = getIMetricManager();
        return manager.getCounter(name);
    }

    public static Counter getJdbcCounter(MetricName name) {
        IMetricManager manager = getIMetricManager();
        return manager.getJdbcCounter(name);
    }

    /**
     * Register a customized metric to specified group.
     * @param metric the metric to register
     */
    public static void register(MetricName name, Metric<?> metric) {
        IMetricManager manager = getIMetricManager();
        manager.register(name, metric);
    }

    /**
     * get dynamically bound {@link IMetricManager} instance
     * @return the {@link IMetricManager} instance bound
     */
    public static IMetricManager getIMetricManager() {
        if (iMetricManager == null) {
            synchronized (MetricManager.class) {
                if (iMetricManager == null) {
                    try {
                        Class<?> binderClazz = MetricManager.class.getClassLoader().loadClass(BINDER_CLASS);
                        Method getSingleton = binderClazz.getMethod("getSingleton");
                        Object binderObject = getSingleton.invoke(null);
                        Method getMetricManager = binderClazz.getMethod("getMetricManager");
                        iMetricManager = (IMetricManager) getMetricManager.invoke(binderObject);
                    } catch (Exception e) {
                        iMetricManager = NOP_METRIC_MANAGER;
                    }
                }
            }
        }
        return iMetricManager;
    }
}
