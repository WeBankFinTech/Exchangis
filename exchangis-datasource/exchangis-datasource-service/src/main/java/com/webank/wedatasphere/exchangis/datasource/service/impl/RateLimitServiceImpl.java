package com.webank.wedatasphere.exchangis.datasource.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.datasource.SpringContext;
import com.webank.wedatasphere.exchangis.datasource.core.domain.*;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.service.RateLimitService;
import com.webank.wedatasphere.exchangis.datasource.utils.RateLimitTool;
import com.webank.wedatasphere.exchangis.job.api.ExchangisJobOpenService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobDataSourcesContent;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.mapper.RateLimitMapper;
import com.webank.wedatasphere.exchangis.datasource.mapper.RateLimitUsedMapper;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitNoLeftException;
import com.webank.wedatasphere.exchangis.datasource.exception.RateLimitOperationException;
import com.webank.wedatasphere.exchangis.job.utils.JobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private static Logger LOG = LoggerFactory.getLogger(RateLimitServiceImpl.class);

    @Resource
    private ExchangisJobOpenService jobOpenService;

    @Resource
    private RateLimitMapper rateLimitMapper;

    @Resource
    private RateLimitUsedMapper rateLimitUsedMapper;

    private RateLimitService selfService;

    protected RateLimitMapper getDao() {
        return rateLimitMapper;
    }

    @Transactional
    public boolean add(RateLimit rateLimit) throws RateLimitOperationException {
        RateLimit selectOne = rateLimitMapper.selectOne(rateLimit);
        if (Objects.nonNull(selectOne)) {
            throw new RateLimitOperationException("The " + rateLimit.getLimitRealmId() + ":" + rateLimit.getLimitRealmId() + " has been bound!(对应模板已被绑定)");
        }
        boolean insertResult = rateLimitMapper.insert(rateLimit) > 0;
        if (!insertResult) {
            throw new RateLimitOperationException("Add failed!(数据库插入失败，请稍后重试)");
        }
        List<RateLimitUsed> rateLimitUsedList = RateLimitTool.generateRateLimitUsed(rateLimit);
        if (rateLimitUsedList.size() > 0) {
            return rateLimitUsedMapper.insert(rateLimitUsedList) > 0;
        }
        return true;
    }

    @Transactional
    public boolean update(RateLimit rateLimit) throws RateLimitOperationException {
        rateLimitMapper.update(rateLimit);
        List<RateLimitUsed> rateLimitUsedList = RateLimitTool.generateRateLimitUsed(rateLimit);
        sortRateLimitUsed(rateLimitUsedList);
        rateLimitUsedMapper.update(rateLimitUsedList);
        return true;
    }

    public boolean delete(String ids) throws RateLimitOperationException {
        String[] idsStr = ids.split(",");
        List<Object> list = new ArrayList<Object>();
        for (String id : idsStr) {
            list.add(Long.valueOf(id));
        }
        boolean deleteResult = rateLimitMapper.delete(list) > 0;
        if (!deleteResult) {
            throw new RateLimitOperationException("Delete failed!(删除限速信息失败，请稍后重试)");
        }
        rateLimitUsedMapper.delete(list);
        return true;
    }

    @Transactional
    public boolean delete(RateLimit rateLimit) throws RateLimitOperationException {
        if (jobOpenService.isRunWithDataSourceModel(rateLimit.getLimitRealmId())) {
            throw new RateLimitOperationException("Current model has been bound!(当前数据源模板已被任务绑定)");
        }
        if (!delete(String.valueOf(rateLimit.getId()))) {
            throw new RateLimitOperationException("Delete failed!(删除失败)");
        }
        return true;
    }

    @Override
    public PageResult<RateLimitVo> findRateLimitPage(RateLimitQuery pageQuery) {
        int currentPage = pageQuery.getPage();
        int pageSize = pageQuery.getPageSize();
        PageHelper.startPage(currentPage, pageSize);
        try {
            List<RateLimitVo> rateLimitVoList = rateLimitMapper.findPageVo(pageQuery);
            if (Objects.isNull(rateLimitVoList) || rateLimitVoList.size() <= 0) {
                return new PageResult<>();
            }
            List<RateLimitUsed> rateLimitUsedList = rateLimitUsedMapper.selectUsedInLimitIds(rateLimitVoList.stream().map(RateLimitVo::getId).collect(Collectors.toList()));
            rateLimitVoList.forEach(vo -> {
                List<RateLimitUsed> collect = rateLimitUsedList.stream().filter(used -> Objects.equals(vo.getId(), used.getRateLimitId())).collect(Collectors.toList());
                collect.forEach(used -> {
                    RateLimitIdentify.RateLimitType rateLimitType = RateLimitIdentify.RateLimitType.valueOfType(used.getRateLimitType());
                    if (Objects.nonNull(rateLimitType) && !Objects.equals(rateLimitType, RateLimitIdentify.RateLimitType.NONE)) {
                        switch (rateLimitType) {
                            case FLOW_RATE_LIMIT:
                                vo.setFlowRateLimitUsed(used.getRateLimitUsed());
                                break;
                            case RECORD_RATE_LIMIT:
                                vo.setRecordRateLimitUsed(used.getRateLimitUsed());
                                break;
                            case PARALLEL_LIMIT:
                                vo.setParallelLimitUsed(used.getRateLimitUsed());
                                break;
                            default:
                                break;
                        }
                    }
                });
            });
            PageInfo<RateLimitVo> pageInfo = new PageInfo<>(rateLimitVoList);
            return new PageResult<>(pageInfo);
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public RateLimit selectOne(RateLimit rateLimit) {
        return rateLimitMapper.selectOne(rateLimit);
    }

    public boolean rateLimit(String rateParams, Map<String, Object> rateParamMap) throws RateLimitNoLeftException {
        Map<String, Object> rateMap = Json.fromJson(rateParams, Map.class);
        List<Long> modelIds = new ArrayList<>();
        Map<String, Long> source = (Map<String, Long>) rateMap.get("source");
        if (Objects.nonNull(source)) {
            Long sourceModelId = Long.valueOf(String.valueOf(source.get("realm")));
            modelIds.add(sourceModelId);
        }
        Map<String, Long> sink = (Map<String, Long>) rateMap.get("sink");
        if (Objects.nonNull(sink)) {
            Long sinkModelId = Long.valueOf(String.valueOf(sink.get("realm")));
            modelIds.add(sinkModelId);
        }
        List<RateLimit> rateLimits = new ArrayList<>();
        if (!modelIds.isEmpty()) {
            rateLimits = rateLimitMapper.selectByRealmIds(RateLimit.DEFAULT_LIMIT_REALM, modelIds);
        }
        if (Objects.nonNull(rateParams) && !rateParams.isEmpty()) {
            List<RateLimitUsed> applyUsed = new ArrayList<>();
            List<Long> rateLimitIds = new ArrayList<>();//todo
            for (RateLimit rateLimit : rateLimits) {
                applyUsed.addAll(getJobRateLimitUsed(rateLimit, rateParamMap));
            }
            rateLimits.forEach(rateLimit -> {
                rateLimitIds.add(rateLimit.getId());
            });
            if (!applyUsed.isEmpty()) {
                sortRateLimitUsed(applyUsed);
                try {
                    getSelfService().rateLimit(applyUsed);
                    return true;
                } catch (RateLimitNoLeftException e) {
                    Map<Long, Integer> limitDirect = rateLimits.stream().collect(Collectors
                            .toMap(RateLimit::getId, rateLimit -> modelIds.contains(rateLimit.getLimitRealmId())
                                    ? 1 : 0));
                    for (RateLimit rateLimit : rateLimits) {
                        limitDirect.put(rateLimit.getId(), 1);
                    }

                    List<RateLimitUsed> actualUsed = this.rateLimitUsedMapper.selectUsedInLimitIds(rateLimitIds);
                    throw new RateLimitNoLeftException(RateLimitTool.getLimitLog(applyUsed,
                            actualUsed.stream().sorted(Comparator.comparing(RateLimitUsed::getId)).filter(used -> limitDirect.get(used.getRateLimitId()) == 0).collect(Collectors.toList()),
                            actualUsed.stream().sorted(Comparator.comparing(RateLimitUsed::getId)).filter(used -> limitDirect.get(used.getRateLimitId()) == 1).collect(Collectors.toList())));
                }
            }
        }
        return false;
    }

    public List<RateLimitUsed> getRateLimitUsed(String rateParams, Map<String, Object> rateParamMap) {
        Map<String, Object> rateMap = Json.fromJson(rateParams, Map.class);
        List<Long> modelIds = new ArrayList<>();
        Map<String, Long> source = (Map<String, Long>) rateMap.get("source");
        if (Objects.nonNull(source)) {
            Long sourceModelId = Long.valueOf(String.valueOf(source.get("realm")));
            modelIds.add(sourceModelId);
        }
        Map<String, Long> sink = (Map<String, Long>) rateMap.get("sink");
        if (Objects.nonNull(sink)) {
            Long sinkModelId = Long.valueOf(String.valueOf(sink.get("realm")));
            modelIds.add(sinkModelId);
        }
        List<RateLimit> rateLimits = new ArrayList<>();
        if (!modelIds.isEmpty()) {
            rateLimits = rateLimitMapper.selectByRealmIds(RateLimit.DEFAULT_LIMIT_REALM, modelIds);
        }
        if (!rateParams.isEmpty()) {
            List<RateLimitUsed> applyUsed = new ArrayList<>();
            List<Long> rateLimitIds = new ArrayList<>();
            for (RateLimit rateLimit : rateLimits) {
                applyUsed.addAll(getJobRateLimitUsed(rateLimit, rateParamMap));
            }
            rateLimits.forEach(rateLimit -> {
                rateLimitIds.add(rateLimit.getId());
            });
            return applyUsed;
        }
        return null;
    }

    @Deprecated
    public boolean rateLimit(ExchangisJobInfo jobInfo) throws RateLimitNoLeftException {
        List<RateLimit> rateLimits = getJobRateLimits(jobInfo);
        if (!rateLimits.isEmpty()) {
            List<RateLimitUsed> applyUsed = new ArrayList<>();
            List<Long> rateLimitIds = new ArrayList<>();
            rateLimits.forEach(rateLimit -> {
                applyUsed.addAll(getJobRateLimitUsed(rateLimit, jobInfo));
                rateLimitIds.add(rateLimit.getId());
            });
            if (!applyUsed.isEmpty()) {
                sortRateLimitUsed(applyUsed);
                try {
                    getSelfService().rateLimit(applyUsed);
                    return true;
                } catch (RateLimitNoLeftException e) {
                    List<Long> sourceModelIds = new ArrayList<>();
                    List<Long> sinkModelIds = new ArrayList<>();
                    JobUtils.parseJobContent(jobInfo.getJobContent()).stream().forEach(jobContent -> {
                        sourceModelIds.add(jobContent.getDataSources().getSource().getModelId());
                        sinkModelIds.add(jobContent.getDataSources().getSink().getModelId());
                    });
                    Map<Long, Integer> limitDirect = rateLimits.stream().collect(Collectors
                            .toMap(RateLimit::getId, rateLimit -> sourceModelIds.contains(rateLimit.getLimitRealmId())
                                    ? 0 : 1));
                    List<RateLimitUsed> actualUsed = this.rateLimitUsedMapper.selectUsedInLimitIds(rateLimitIds);
                    throw new RateLimitNoLeftException(RateLimitTool.getLimitLog(applyUsed,
                            actualUsed.stream().sorted(Comparator.comparing(RateLimitUsed::getId)).filter(used -> limitDirect.get(used.getRateLimitId()) == 0).collect(Collectors.toList()),
                            actualUsed.stream().sorted(Comparator.comparing(RateLimitUsed::getId)).filter(used -> limitDirect.get(used.getRateLimitId()) == 1).collect(Collectors.toList())));
                }
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rateLimit(List<RateLimitUsed> applyUsed) throws RateLimitNoLeftException {
        if (Objects.nonNull(applyUsed) && applyUsed.size() > 0) {
            for (RateLimitUsed apply : applyUsed) {
                int success = this.rateLimitUsedMapper.applyRateLimitUsed(apply);
                if (success <= 0) {
                    // Has no left resource applied for using of limitation
                    throw new RateLimitNoLeftException("No left resource applied for using in rate limitation.");
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseRateLimit(String rateParams, Map<String, Object> rateParamsMap) {
        List<RateLimitUsed> rateLimitUsed = this.getRateLimitUsed(rateParams, rateParamsMap);
        if (Objects.nonNull(rateLimitUsed) && !rateLimitUsed.isEmpty()) {
            sortRateLimitUsed(rateLimitUsed);
            this.rateLimitUsedMapper.releaseRateLimitUsed(rateLimitUsed);
        }
    }

    public void resetRateLimitUsed(RateLimit rateLimit) {
        rateLimitUsedMapper.resetRateLimitUsed(rateLimit);
    }

    /**
     * Get rate limit used from job
     *
     * @param rateParams rateParams
     * @return limit used list
     */
    private List<RateLimitUsed> getJobRateLimitUsed(RateLimit rateLimit, Map<String, Object> rateParams) {
        return RateLimitTool.generateRateLimitUsed(rateLimit, rateParams);
    }

    /**
     * Get rate limit used from job
     *
     * @param rateLimit rate limit
     * @param jobInfo   job info
     * @return limit used list
     */
    @Deprecated
    private List<RateLimitUsed> getJobRateLimitUsed(RateLimit rateLimit, ExchangisJobInfo jobInfo) {
        return RateLimitTool.generateRateLimitUsed(rateLimit, jobInfo);
    }

    /**
     * Get rate limits of job info
     *
     * @param jobInfo job info
     * @return rate limit list
     */
    private List<RateLimit> getJobRateLimits(ExchangisJobInfo jobInfo) {
        List<Long> modelIds = new ArrayList<>();
        JobUtils.parseJobContent(jobInfo.getJobContent()).stream().forEach(jobContent -> {
            Optional.ofNullable(jobContent)
                    .map(ExchangisJobInfoContent::getDataSources)
                    .map(ExchangisJobDataSourcesContent::getSource)
                    .ifPresent(source -> {
                        modelIds.add(source.getModelId());
                    });
            Optional.ofNullable(jobContent)
                    .map(ExchangisJobInfoContent::getDataSources)
                    .map(ExchangisJobDataSourcesContent::getSink)
                    .ifPresent(sink -> {
                        modelIds.add(sink.getModelId());
                    });
        });
        // Add model id list
        if (!modelIds.isEmpty()) {
            // Filter by the rate limit switch
            return this.rateLimitMapper.selectByRealmIds(RateLimit.DEFAULT_LIMIT_REALM, modelIds)
                    .stream().filter(RateLimit::getOpenLimit).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * General sort strategy
     *
     * @param usedList used list
     */
    private void sortRateLimitUsed(List<RateLimitUsed> usedList) {
        usedList.sort((left, right) -> {
            if (left.getId() == null) {
                return 0;
            } else if (right.getId() == null) {
                return 1;
            } else {
                return (int) (left.getId() - right.getId());
            }
        });
    }

    /**
     * Self service
     *
     * @return limit service
     */
    private RateLimitService getSelfService() {
        if (Objects.isNull(this.selfService)) {
            this.selfService = SpringContext.getBean(RateLimitService.class);
        }
        return this.selfService;
    }
}
