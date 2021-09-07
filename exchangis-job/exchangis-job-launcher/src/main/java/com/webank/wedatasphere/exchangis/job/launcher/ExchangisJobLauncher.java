package com.webank.wedatasphere.exchangis.job.launcher;

public interface ExchangisJobLauncher<T> {

    void launch(T launchTask);
}
