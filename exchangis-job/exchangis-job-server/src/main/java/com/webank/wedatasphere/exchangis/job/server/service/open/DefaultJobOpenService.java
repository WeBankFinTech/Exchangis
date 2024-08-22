package com.webank.wedatasphere.exchangis.job.server.service.open;

import com.github.pagehelper.PageHelper;
import com.webank.wedatasphere.exchangis.datasource.mapper.DataSourceModelRelationMapper;
import com.webank.wedatasphere.exchangis.job.api.ExchangisJobOpenService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobEntityDao;
import com.webank.wedatasphere.exchangis.job.utils.JobUtils;
import com.webank.wedatasphere.exchangis.job.vo.ExchangisJobQueryVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode.JOB_EXCEPTION_CODE;


/**
 * Default implement
 */
@Service
public class DefaultJobOpenService implements ExchangisJobOpenService {

    @Resource
    private ExchangisJobEntityDao jobEntityDao;

    @Resource
    private DataSourceModelRelationMapper dataSourceModelRelationMapper;

    @Override
    public ExchangisJobEntity getJobById(Long id, boolean basic) throws ExchangisJobException {
        try {
            return basic ? this.jobEntityDao.getBasicInfo(id) : this.jobEntityDao.getDetail(id);
        } catch (Exception e){
            throw new ExchangisJobException(JOB_EXCEPTION_CODE.getCode(),
                    "Fail to the information of job [id: " + id + "]", e);
        }
    }

    @Override
    public List<ExchangisJobEntity> queryJobs(ExchangisJobQueryVo queryVo, boolean inPage) throws ExchangisJobException {
        try {
            if (inPage) {
                PageHelper.startPage(queryVo.getPage(), queryVo.getPageSize());
                try {
                    return this.jobEntityDao.queryPageList(queryVo);
                } finally {
                    PageHelper.clearPage();
                }
            }
            return this.jobEntityDao.queryPageList(queryVo);
        } catch (Exception e){
            throw new ExchangisJobException(JOB_EXCEPTION_CODE.getCode(),
                    "Fail to query job list", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobBatch(List<Long> idList) throws ExchangisJobException {
        try {
            this.jobEntityDao.deleteBatch(idList);
        } catch (Exception e){
            throw new ExchangisJobException(JOB_EXCEPTION_CODE.getCode(),
                    "Fail to delete batch job ids, id list: [" + StringUtils.join(idList,",") + "]", e);
        }
    }

    @Override
    public boolean isRunWithDataSourceModel(Long id) {
        // Get all datasources and jobs
        List<Long> dsIds = dataSourceModelRelationMapper.queryDataSourceIdsByModel(id);
        List<ExchangisJobEntity> jobEntities = jobEntityDao.queryPageList(null);

        AtomicBoolean empty = new AtomicBoolean(false);
        if (Objects.nonNull(jobEntities) && jobEntities.size() > 0) {
            for (ExchangisJobEntity jobEntity : jobEntities) {
                List<ExchangisJobInfoContent> jobInfoContents = JobUtils.parseJobContent(jobEntity.getJobContent());
                jobInfoContents.stream().map(content -> content.getDataSources())
                        .forEach(item -> {
                            if (dsIds.contains(item.getSource().getId()) ||
                                    dsIds.contains(item.getSource().getId())) {
                                empty.set(true);
                            }
                        });
            }
        }
        return empty.get();
    }
}
