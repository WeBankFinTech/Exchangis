package com.webank.wedatasphere.exchangis.metrics.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedatasphere.exchangis.metrics.dao.entity.ExchangisMetric;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface ExchangisMetricMapper extends BaseMapper<ExchangisMetric> {

    @Select("select * from exchangis_metric where norm = #{norm}")
    Optional<ExchangisMetric> getByNorm(@Param("norm") String norm);

}
