package com.webank.wedatasphere.exchangis.dss.appconn.enums;

import java.util.Arrays;

/**
 * @author tikazhang
 * @Date 2022/5/4 12:22
 */
public enum ExchangisStatusEnum {
    Scheduled(0, "Scheduled"),
    Running(1, "Running"),
    Succeed(2, "Succeed"),
    Failed(3,"Failed"),
    Cannelled(4, "Cancelled"),
    Inited(5, "Inited"),
    Timeout(6, "Timeout");

    private int code;
    private String status;
    private ExchangisStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static ExchangisStatusEnum getEnum(String status) {
        return Arrays.stream(ExchangisStatusEnum.values()).filter(e -> e.getStatus().equals(status)).findFirst().orElseThrow(NullPointerException::new);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
