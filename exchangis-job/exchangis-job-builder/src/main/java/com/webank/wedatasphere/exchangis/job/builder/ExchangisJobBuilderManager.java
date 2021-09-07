package com.webank.wedatasphere.exchangis.job.builder;

public class ExchangisJobBuilderManager {

    private volatile static ExchangisJobBuilder jobBuilder;

    public static ExchangisJobBuilder getJobBuiler(String engineType) {
        if (jobBuilder == null) {
            synchronized (ExchangisJobBuilderManager.class) {
                if (jobBuilder == null) {
                    switch (engineType) {
                        default:
                            jobBuilder = new DataXJobBuilder();
                            break;
                    }
                }
            }
        }
        return jobBuilder;
    }
}
