package com.webank.wedatasphere.exchangis.job.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.exchangis.job.server.domain.ExchangisJob;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobBasicInfoDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisJobContentDTO;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisJobErrorException;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisJobMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisJobService;
import com.webank.wedatasphere.exchangis.job.server.vo.ExchangisJobBasicInfoVO;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yuxin.yuan
 * @since 2021-08-10
 */
@Service
public class ExchangisJobServiceImpl extends ServiceImpl<ExchangisJobMapper, ExchangisJob> implements ExchangisJobService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExchangisJobService exchangisJobService;

    // @Autowired
    // private ExchangisDataSourceService exchangisDataSourceService;

    @Override
    public ExchangisJobBasicInfoVO createJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO) {
        ExchangisJob exchangisJob = modelMapper.map(exchangisJobBasicInfoDTO, ExchangisJob.class);
        exchangisJobService.save(exchangisJob);
        return modelMapper.map(exchangisJob, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public List<ExchangisJobBasicInfoVO> getJobList(long projectId, String type, String name) {
        List<ExchangisJob> joblist = exchangisJobService.list();
        joblist = joblist.stream().filter(job -> job.getProjectId().equals(projectId) && job.getJobType() != null && job.getJobType().equals(type)).collect(Collectors.toList());
        if (name != null) {
            joblist = joblist.stream().filter(job -> job.getJobName() != null && job.getJobName().equals(name)).collect(Collectors.toList());
        }
        List<ExchangisJobBasicInfoVO> returnlist = new ArrayList<>();
        joblist.stream().forEach(job -> {
            returnlist.add(modelMapper.map(job, ExchangisJobBasicInfoVO.class));
        });
        return returnlist;
    }

    @Override
    public ExchangisJobBasicInfoVO copyJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long sourceJobId) {
        ExchangisJob oldJob = exchangisJobService.getById(sourceJobId);
        ExchangisJob newJob = modelMapper.map(exchangisJobBasicInfoDTO, ExchangisJob.class);
        newJob.setProjectId(oldJob.getProjectId());
        newJob.setJobType(oldJob.getJobType());
        newJob.setEngineType(oldJob.getEngineType());
        exchangisJobService.save(newJob);
        return modelMapper.map(newJob, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public ExchangisJobBasicInfoVO updateJob(ExchangisJobBasicInfoDTO exchangisJobBasicInfoDTO, Long id) {
        ExchangisJob job = exchangisJobService.getById(id);
        job.setJobName(exchangisJobBasicInfoDTO.getJobName());
        job.setJobLabels(exchangisJobBasicInfoDTO.getJobLabels());
        job.setJobDesc(exchangisJobBasicInfoDTO.getJobDesc());
        exchangisJobService.updateById(job);

        return modelMapper.map(job, ExchangisJobBasicInfoVO.class);
    }

    @Override
    public ExchangisJobBasicInfoVO importSingleJob(MultipartFile multipartFile) {

        return null;
    }

    @Override
    public void deleteJob(Long id) {
        exchangisJobService.removeById(id);
    }

    @Override
    public ExchangisJob getJob(Long id) throws ExchangisJobErrorException {
        ExchangisJob exchangisJob = exchangisJobService.getById(id);
        // generate subjobs ui content
        // ExchangisDataSourceUIViewer jobDataSourceUIs = exchangisDataSourceService.getJobDataSourceUIs(id);
        String content = null; // convertToString(jobDataSourceUIs);

        exchangisJob.setContent(content);
        return exchangisJob;
    }

    @Override
    public ExchangisJob updateJob(ExchangisJobContentDTO exchangisJobContentDTO, Long id)
        throws ExchangisJobErrorException {
        ExchangisJob exchangisJob = exchangisJobService.getById(id);
        exchangisJob.setContent(exchangisJobContentDTO.getContent());
        exchangisJob.setProxyUser(exchangisJobContentDTO.getProxyUser());
        exchangisJob.setExecuteNode(exchangisJobContentDTO.getExecuteNode());
        exchangisJob.setSyncType(exchangisJobContentDTO.getSyncType());
        exchangisJob.setJobParams(exchangisJobContentDTO.getJobParams());
        exchangisJobService.updateById(exchangisJob);
        return this.getJob(id);
    }

    // private String convertToString(ExchangisDataSourceUIViewer jobDataSourceUIs) throws ExchangisJobErrorException {
    // ObjectMapper mapper = new ObjectMapper();
    // try {
    // return mapper.writeValueAsString(jobDataSourceUIs);
    // } catch (JsonProcessingException e) {
    // throw new ExchangisJobErrorException(20001, "exchangis.subjob.ui.create.error", e);
    // }
    // }
}
