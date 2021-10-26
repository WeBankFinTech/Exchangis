package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisEngineResourceMetricsDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskProcessMetricsDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskStatusMetricsDTO;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisMetricsService;
import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExchangisMetricsServiceImpl implements ExchangisMetricsService {


    private final ExchangisLaunchTaskMapper exchangisLaunchTaskMapper;

    @Autowired
    public ExchangisMetricsServiceImpl(ExchangisLaunchTaskMapper exchangisLaunchTaskMapper) {
        this.exchangisLaunchTaskMapper = exchangisLaunchTaskMapper;
    }

    @Override
    public Message getTaskStateMetrics(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        List<ExchangisTaskStatusMetricsDTO> metrices = exchangisLaunchTaskMapper.statTaskStatus(params);
        return Message.ok().data("metrices", metrices);
    }

    @Override
    public Message getTaskProcessMetrics(HttpServletRequest request) {
        // TODO mock data for process metrics
        List<ExchangisTaskProcessMetricsDTO> list = new ArrayList<>();

        // total
        ExchangisTaskProcessMetricsDTO total = new ExchangisTaskProcessMetricsDTO();
        total.setRunning(50);
        total.setInitialized(10);
        total.setTotal(120);
        total.setPercentOfComplete("48%");
        list.add(total);

        // bdp
        ExchangisTaskProcessMetricsDTO bdp = new ExchangisTaskProcessMetricsDTO();
        bdp.setRunning(20);
        bdp.setInitialized(10);
        bdp.setTotal(60);
        bdp.setPercentOfComplete("33%");
        list.add(bdp);

        // es
        ExchangisTaskProcessMetricsDTO es = new ExchangisTaskProcessMetricsDTO();
        es.setRunning(20);
        es.setInitialized(0);
        es.setTotal(40);
        es.setPercentOfComplete("50%");
        list.add(es);

        // fps
        ExchangisTaskProcessMetricsDTO fps = new ExchangisTaskProcessMetricsDTO();
        fps.setRunning(10);
        fps.setInitialized(0);
        fps.setTotal(20);
        fps.setPercentOfComplete("50%");
        list.add(fps);

        return Message.ok().data("list", list);
    }

    // mock data for echarts

    /**
     *
     * dataset: {
     *     source: [
     *       ['datasource', '2021-10-25 15:00', '2021-10-25 15:01', '2021-10-25 15:02', '2021-10-25 15:03', '2021-10-25 15:04'],
     *       ['ds1', 41.1, 30.4, 65.1, 53.3, 44.2],
     *       ['ds2', 86.5, 92.1, 85.7, 83.1, 93.2],
     *       ['ds3', 24.1, 67.2, 79.5, 86.4, 76.2]
     *     ]
     *   },
     */
    @Override
    public Message getDataSourceFlowMetrics(HttpServletRequest request) {
        // TODO
        // make last past 4 hours data, dimension is min
        String fromDateTime = "2021-10-25 15:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedFrom = new Date();
        try {
            parsedFrom = sdf.parse(fromDateTime);
        } catch (Exception e) {
            parsedFrom = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedFrom);

        List<List<Object>> dataset = new ArrayList<>();
        List<Object> header = new ArrayList<>();
        int loopNum = 4 * 60;

        // 添加第一行，头信息
        header.add("数据源");
        for (int i = 1; i <= loopNum; i++) {
            header.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 1);
        }
        dataset.add(header);

        // 添加数据信息
        List<Object> ds1Data = new ArrayList<>();
        ds1Data.add("ds1");

        List<Object> ds2Data = new ArrayList<>();
        ds1Data.add("ds2");

        List<Object> ds3Data = new ArrayList<>();
        ds1Data.add("ds3");
        for (int i = 1; i <= loopNum; i++) {
            ds1Data.add(i * RandomUtils.nextInt(1024));
            ds2Data.add(i * RandomUtils.nextInt(512));
            ds3Data.add(i * RandomUtils.nextInt(2048));
        }
        dataset.add(ds1Data);
        dataset.add(ds2Data);
        dataset.add(ds3Data);

        return Message.ok().data("dataset", dataset);
    }

    @Override
    public Message getEngineResourceMetrics(HttpServletRequest request) {
        List<ExchangisEngineResourceMetricsDTO> list = new ArrayList<>();
        ExchangisEngineResourceMetricsDTO sqoop = new ExchangisEngineResourceMetricsDTO();
        sqoop.setEngine("sqoop");
        sqoop.setCpu("45%");
        sqoop.setMem("1782Mi");
        list.add(sqoop);

        ExchangisEngineResourceMetricsDTO datax = new ExchangisEngineResourceMetricsDTO();
        datax.setEngine("datax");
        datax.setCpu("32%");
        datax.setMem("512Mi");
        list.add(datax);

        ExchangisEngineResourceMetricsDTO linkis = new ExchangisEngineResourceMetricsDTO();
        datax.setEngine("linkis");
        datax.setCpu("78%");
        datax.setMem("4196Mi");
        list.add(datax);

        return Message.ok().data("list", list);
    }


}
