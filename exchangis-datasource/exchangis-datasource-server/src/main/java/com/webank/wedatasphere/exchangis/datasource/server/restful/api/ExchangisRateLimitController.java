package com.webank.wedatasphere.exchangis.datasource.server.restful.api;

import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceModel;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimit;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitVo;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitOperationException;
import com.webank.wedatasphere.exchangis.datasource.core.domain.RateLimitQuery;
import com.webank.wedatasphere.exchangis.datasource.service.DataSourceModelService;
import com.webank.wedatasphere.exchangis.datasource.service.RateLimitService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "dss/exchangis/main/datasources/rate-limit", produces = {"application/json;charset=utf-8"})
public class ExchangisRateLimitController {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisRateLimitController.class);

    @Resource
    private DataSourceModelService dataSourceModelService;

    @Resource
    private RateLimitService rateLimitService;

    @RequestMapping( value = "pageList", method = RequestMethod.GET)
    public Message pageList(HttpServletRequest request, RateLimitQuery pageQuery) {
        String username = UserUtils.getLoginUser(request);
        pageQuery.setCreateUser(username);

        try {
            List<RateLimitVo> rateLimitPage = rateLimitService.findRateLimitPage(pageQuery);
            Message message = Message.ok();
            message.data("list", rateLimitPage);
            message.data("sourceType", pageQuery.getSourceType());
            if (!rateLimitPage.isEmpty()) {
                message.data("total", rateLimitPage.size());
            }
            return message;
        } catch (Exception t) {
            LOG.error("Failed to query project list for user {}", username, t);
            return Message.error("Failed to query rateLimit list (获取限速列表失败) " + t.getMessage());
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Message add(@RequestBody @Valid RateLimit rateLimit, HttpServletRequest request) {
        if (Objects.isNull(rateLimit.getLimitRealm()) || Objects.isNull(rateLimit.getLimitRealmId())) {
            return Message.error("Please check the params!(参数校验失败)");
        }

        // Param valid
        String username = UserUtils.getLoginUser(request);
        ExchangisDataSourceModel dataSourceModel = dataSourceModelService.get(rateLimit.getLimitRealmId());
        MutablePair<Boolean, String> checkResult = checkDataSourceModel(rateLimit, dataSourceModel);
        if (!checkResult.getLeft()) {
            return Message.error("Please check the params!(参数校验失败), Cause by : " + checkResult.getRight());
        }

        try {
            rateLimitService.add(rateLimit);
        } catch (RateLimitOperationException e) {
            return Message.error("Failed to add the rateLimit!(添加限速信息失败), cause by : " + e.getMessage());
        }
        return Message.ok();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Message update(@Valid @RequestBody RateLimit rateLimit,HttpServletRequest request) {
        if (Objects.isNull(rateLimit.getLimitRealm()) || Objects.isNull(rateLimit.getLimitRealmId())) {
            return Message.error("Please check the params!(参数校验失败)");
        }
//        if(!hasDataAuth(RateLimit.class,DataAuthScope.WRITE, request, rateLimitService.get(rateLimit))){
//            return new Response<RateLimit>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchangis.rate-limit.error.access.rights"));
//        }

        // Param valid
        ExchangisDataSourceModel dataSourceModel = dataSourceModelService.get(rateLimit.getLimitRealmId());
        MutablePair<Boolean, String> checkResult = checkDataSourceModel(rateLimit, dataSourceModel);
        if (!checkResult.getLeft()) {
            return Message.error("Please check the params!(参数校验失败), Cause by : " + checkResult.getRight());
        }

        // Do update operation
//        security.bindUserInfo(rateLimit, request);
        try {
            rateLimitService.update(rateLimit);
        } catch (RateLimitOperationException e) {
            return Message.error("Failed to update the rateLimit!(修改限速信息失败), cause by : " + e.getMessage());
        }
        return Message.ok();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Message delete(@PathVariable Long id, HttpServletRequest request) {
        RateLimit rateLimit = rateLimitService.selectOne(new RateLimit(id));
        if (Objects.isNull(rateLimit)) {
            return Message.error("Please check the params!(参数校验失败)");
        }
//        if(!hasDataAuth(RateLimit.class,DataAuthScope.WRITE, request, rateLimit)){
//            return new Response<RateLimit>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchangis.rate-limit.error.access.rights"));
//        }

        // Param valid
        ExchangisDataSourceModel dataSourceModel = dataSourceModelService.get(rateLimit.getLimitRealmId());
        MutablePair<Boolean, String> checkResult = checkDataSourceModel(rateLimit, dataSourceModel);
        if (!checkResult.getLeft()) {
            return Message.error("Please check the params!(参数校验失败)" + checkResult.getRight());
        }

        // Do delete operation
        try {
            rateLimitService.delete(rateLimit);
        } catch (RateLimitOperationException e) {
            return Message.error("Failed to delete the rateLimit!(删除限速信息失败), cause by : " + e.getMessage());
        }
        return Message.ok();
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public Message resetRateLimitUsed(@RequestBody RateLimit rateLimit, HttpServletRequest request){

        // Param valid
        RateLimit queryRateLimit = rateLimitService.selectOne(rateLimit);
        if (Objects.isNull(queryRateLimit)) {
            return Message.error("RateLimit Id : " + rateLimit.getId() + " is illegal!(非法参数)");
        }
//        if(!hasDataAuth(RateLimit.class,DataAuthScope.WRITE, request, queryRateLimit)){
//            return new Response<Object>().errorResponse(CodeConstant.AUTH_ERROR, null, super.informationSwitch("exchangis.rate-limit.error.access.rights"));
//        }

        rateLimitService.resetRateLimitUsed(rateLimit);
        return Message.ok();
    }

    private MutablePair<Boolean, String> checkDataSourceModel(RateLimit rateLimit, ExchangisDataSourceModel dataSourceModel) {
        MutablePair<Boolean, String> pair = new MutablePair<>(true, null);
        if (Objects.isNull(dataSourceModel)) {
            pair.setLeft(false);
            pair.setRight("ModelId not exist!(模板不存在)");
            return pair;
        }
        if (Objects.nonNull(rateLimit) && Objects.nonNull(rateLimit.getLimitRealm()) && Objects.nonNull(rateLimit.getLimitRealmId())) {
            if (StringUtils.equals(RateLimit.DEFAULT_LIMIT_REALM, rateLimit.getLimitRealm()) &&
                StringUtils.equals("ElasticSearch", dataSourceModel.getSourceType())) {
                String uniqueId = "elasticUrls";
                // Check unique id
                Map<String, Object> paramMap = dataSourceModel.resolveParams();
                List<ExchangisDataSourceModel> dataSourceModels = dataSourceModelService.queryWithRateLimit();
                if (Objects.nonNull(dataSourceModels) && dataSourceModels.size() > 0) {
                    for (ExchangisDataSourceModel dsm : dataSourceModels) {
                        Map<String, Object> params = dsm.resolveParams();
                        if (!Objects.equals(rateLimit.getLimitRealmId(), dsm.getId()) &&
                                Objects.equals(paramMap.get(uniqueId), params.get(uniqueId))) {
                            pair.setLeft(false);
                            pair.setRight("Current cluster has been bound！（当前集群已绑定限速信息）");
                            return pair;
                        }
                    }
                }
            }
        }
        return pair;
    }

}
