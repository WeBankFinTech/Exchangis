package com.webank.wedatasphere.exchangis.metrics.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("exchangis_metric")
public class ExchangisMetric {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String norm;

    private String value;

    private Date ts;

    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
