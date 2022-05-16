package com.webank.wedatasphere.exchangis.metrics.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.webank.wedatasphere.exchangis.metrics.api.Counter;
import com.webank.wedatasphere.exchangis.metrics.api.MetricName;
import com.webank.wedatasphere.exchangis.metrics.dao.entity.ExchangisMetric;
import com.webank.wedatasphere.exchangis.metrics.dao.mapper.ExchangisMetricMapper;
import com.webank.wedatasphere.exchangis.metrics.support.SpringContextHolder;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class JdbcCounterImpl implements Counter {
    private static final long MAX_LOCK_WAIT = 60 * 1000;
    private final MetricName name;
    private final ExchangisMetricMapper mapper;
    private long ts;

    @Override
    public MetricName getMetricName() {
        return name;
    }

    public JdbcCounterImpl(MetricName name) {
        this.name = name;

        ApplicationContext ctx = SpringContextHolder.getApplicationContext();
        this.mapper = ctx.getBean(ExchangisMetricMapper.class);
    }

    @Override
    public long lastUpdateTime() {
        return ts;
    }

    @Override
    public void inc() {
        update(1);
    }

    @Override
    public void inc(long n) {
        update(n);
    }

    @Override
    public void dec() {
        update(-1);
    }

    @Override
    public void dec(long n) {
        update(-n);
    }

    @Override
    public long getCount() {
        Optional<ExchangisMetric> metricOptional = mapper.getByNorm(name.getNorm());
        if (metricOptional.isPresent()) {
            ExchangisMetric metric = metricOptional.get();
            String value = metric.getValue();
            return Long.parseLong(value);
        }
        return 0;
    }

    private void update(long n) {
        String norm = name.getNorm();
        String title = name.getTitle();
        // TODO 是否要做锁超时处理
//        long lockWaitStartMs = System.currentTimeMillis();
        ApplicationContext ctx = SpringContextHolder.getApplicationContext();
        ExchangisMetricMapper mapper = ctx.getBean(ExchangisMetricMapper.class);
        Optional<ExchangisMetric> metricOptional = mapper.getByNorm(norm);
        if (metricOptional.isPresent()) {
            ExchangisMetric metric = metricOptional.get();
            long metricId = metric.getId();
            int update;
            ExchangisMetric dblCheckMetric;
            do {
                long ts = System.currentTimeMillis();
                dblCheckMetric = mapper.selectById(metricId);
                if (null == dblCheckMetric) {
                    break;
                }
                // update
                Long oldVersion = dblCheckMetric.getVersion();
                String value = dblCheckMetric.getValue();
                long longVal;
                try {
                    longVal = Long.parseLong(value);
                } catch (Exception e) {
                    longVal = 0;
                }

                UpdateWrapper<ExchangisMetric> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", dblCheckMetric.getId());
                updateWrapper.eq("version", oldVersion);

                ExchangisMetric updateBean = new ExchangisMetric();
                updateBean.setValue((longVal + n) + "");
                updateBean.setVersion(oldVersion + 1);
                updateBean.setTs(new Date(ts));
                update = mapper.update(updateBean, updateWrapper);
                if (1 != update) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(30);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
                this.ts = ts;
            } while (update != 1);
        } else {
            long ts = System.currentTimeMillis();

            // new create
            ExchangisMetric metric = new ExchangisMetric();
            metric.setNorm(norm);
            metric.setTitle(title);
            metric.setValue(n+"");
            metric.setVersion(1L);
            metric.setTs(new Date(ts));
            mapper.insert(metric);

            this.ts = ts;
        }
    }
}
