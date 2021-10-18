package com.webank.wedatasphere.exchangis.job.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangisSubJob extends ExchangisJobInfoContent {

}
