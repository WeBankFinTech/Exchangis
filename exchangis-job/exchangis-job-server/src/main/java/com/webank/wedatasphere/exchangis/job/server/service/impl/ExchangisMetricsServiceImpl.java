package com.webank.wedatasphere.exchangis.job.server.service.impl;

import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisEngineResourceMetricsDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskProcessMetricsDTO;
import com.webank.wedatasphere.exchangis.job.server.dto.ExchangisTaskStatusMetricsDTO;
import com.webank.wedatasphere.exchangis.job.server.mapper.ExchangisLaunchTaskMapper;
import com.webank.wedatasphere.exchangis.job.server.service.ExchangisMetricsService;
import org.apache.linkis.server.Message;
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
        List<ExchangisTaskStatusMetricsDTO> metrices = new ArrayList<>();
        // TODO hard code
        ExchangisTaskStatusMetricsDTO success = exchangisLaunchTaskMapper.getTaskMetricsByStatus("SUCCESS");
        ExchangisTaskStatusMetricsDTO failed = exchangisLaunchTaskMapper.getTaskMetricsByStatus("FAILED");
        ExchangisTaskStatusMetricsDTO running = exchangisLaunchTaskMapper.getTaskMetricsByStatus("RUNNING");
        ExchangisTaskStatusMetricsDTO busy = exchangisLaunchTaskMapper.getTaskMetricsByStatus("BUSY");
        ExchangisTaskStatusMetricsDTO idle = exchangisLaunchTaskMapper.getTaskMetricsByStatus("IDLE");
        ExchangisTaskStatusMetricsDTO unlock = exchangisLaunchTaskMapper.getTaskMetricsByStatus("UNLOCK");

        Optional.ofNullable(success).ifPresent(metrices::add);
        Optional.ofNullable(failed).ifPresent(metrices::add);
        Optional.ofNullable(running).ifPresent(metrices::add);
        Optional.ofNullable(busy).ifPresent(metrices::add);
        Optional.ofNullable(idle).ifPresent(metrices::add);
        Optional.ofNullable(unlock).ifPresent(metrices::add);

        Message message = Message.ok();
        message.setMethod("/dss/exchangis/main/metrics/taskstate");
        message.data("metrices", metrices);
        return message;
    }

    @Override
    public Message getTaskProcessMetrics(HttpServletRequest request) {
        // TODO mock data for process metrics
        List<ExchangisTaskProcessMetricsDTO> list = new ArrayList<>();

        // total
        ExchangisTaskProcessMetricsDTO total = new ExchangisTaskProcessMetricsDTO();
        total.setKey("total");
        total.setTitle("总进度");
        total.setRunning(50);
        total.setInitialized(10);
        total.setTotal(120);
        total.setPercentOfComplete("48%");
        list.add(total);

        // bdp
        ExchangisTaskProcessMetricsDTO bdp = new ExchangisTaskProcessMetricsDTO();
        bdp.setKey("bdp");
        bdp.setTitle("BDP");
        bdp.setRunning(20);
        bdp.setInitialized(10);
        bdp.setTotal(60);
        bdp.setPercentOfComplete("33%");
        list.add(bdp);

        // es
        ExchangisTaskProcessMetricsDTO es = new ExchangisTaskProcessMetricsDTO();
        es.setKey("es");
        es.setTitle("ES");
        es.setRunning(20);
        es.setInitialized(0);
        es.setTotal(40);
        es.setPercentOfComplete("50%");
        list.add(es);

        // fps
        ExchangisTaskProcessMetricsDTO fps = new ExchangisTaskProcessMetricsDTO();
        fps.setKey("fps");
        fps.setTitle("FPS");
        fps.setRunning(10);
        fps.setInitialized(0);
        fps.setTotal(20);
        fps.setPercentOfComplete("50%");
        list.add(fps);

        Message message = Message.ok();
        message.setMethod("/dss/exchangis/main/metrics/taskprocess");
        message.data("list", list);
        return message;
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
        Date parsedFrom;
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
//        header.add("数据源");
//        for (int i = 1; i <= loopNum; i++) {
//            header.add(sdf.format(calendar.getTime()));
//            calendar.add(Calendar.MINUTE, 1);
//        }
        header.add("时间");
        header.add("ds1");
        header.add("ds2");
        header.add("ds3");
        dataset.add(header);

        List<Object> realData;
        int max = 10240;
        int min = 512;
        for (int i = 1; i <= loopNum; i++) {
            realData = new ArrayList<>();
            realData.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 1);
            realData.add(Math.random() * (max - min) + min);
            realData.add(Math.random() * (max - min) + min);
            realData.add( Math.random() * (max - min) + min);
            dataset.add(realData);
        }

        // 添加数据信息
//        List<Object> ds1Data = new ArrayList<>();
//        ds1Data.add("ds1");
//
//        List<Object> ds2Data = new ArrayList<>();
//        ds2Data.add("ds2");
//
//        List<Object> ds3Data = new ArrayList<>();
//        ds3Data.add("ds3");
//        for (int i = 1; i <= loopNum; i++) {
//            ds1Data.add(i * RandomUtils.nextInt(1024));
//            ds2Data.add(i * RandomUtils.nextInt(512));
//            ds3Data.add(i * RandomUtils.nextInt(2048));
//        }
//        dataset.add(ds1Data);
//        dataset.add(ds2Data);
//        dataset.add(ds3Data);
        Message message = Message.ok();
        message.setMethod("/dss/exchangis/main/metrics/datasourceflow");
        message.data("dataset", dataset);
        return message;
    }

    @Override
    public Message getEngineResourceCpuMetrics(HttpServletRequest request) {
        // make last past 4 hours data, dimension is min
        String fromDateTime = "2021-10-25 15:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedFrom;
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
        header.add("时间");
        header.add("datax");
        header.add("sqoop");
        header.add("linkis");
//        for (int i = 1; i <= loopNum; i++) {
//            header.add(sdf.format(calendar.getTime()));
//            calendar.add(Calendar.MINUTE, 1);
//        }
        dataset.add(header);

        // 添加数据信息
        List<Object> realData;
//        ds1Data.add("datax");

//        List<Object> ds2Data = new ArrayList<>();
//        ds2Data.add("sqoop");
//
//        List<Object> ds3Data = new ArrayList<>();
//        ds3Data.add("linkis");
        int min = 1;
        int max = 8;
        for (int i = 1; i <= loopNum; i++) {
            realData = new ArrayList<>();
            realData.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 1);
            realData.add(Math.random() * (max - min) + min);
            realData.add(Math.random() * (max - min) + min);
            realData.add( Math.random() * (max - min) + min);
            dataset.add(realData);
        }
//        dataset.add(ds1Data);
//        dataset.add(ds2Data);
//        dataset.add(ds3Data);
//        dataset.add(realData);
        Message message = Message.ok();
        message.setMethod("/dss/exchangis/main/metrics/engineresourcecpu");
        message.data("dataset", dataset);
        return message;


    }

    @Override
    public Message getEngineResourceMemMetrics(HttpServletRequest request) {
        // make last past 4 hours data, dimension is min
        String fromDateTime = "2021-10-25 15:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parsedFrom;
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
//        header.add("引擎");
//        for (int i = 1; i <= loopNum; i++) {
//            header.add(sdf.format(calendar.getTime()));
//            calendar.add(Calendar.MINUTE, 1);
//        }
//        dataset.add(header);
//
//        // 添加数据信息
//        List<Object> ds1Data = new ArrayList<>();
//        ds1Data.add("datax");
//
//        List<Object> ds2Data = new ArrayList<>();
//        ds2Data.add("sqoop");
//
//        List<Object> ds3Data = new ArrayList<>();
//        ds3Data.add("linkis");
//        for (int i = 1; i <= loopNum; i++) {
//            ds1Data.add(i * RandomUtils.nextInt(4192));
//            ds2Data.add(i * RandomUtils.nextInt(2048));
//            ds3Data.add(i * RandomUtils.nextInt(1024));
//        }

        // 添加第一行，头信息
        header.add("时间");
        header.add("datax");
        header.add("sqoop");
        header.add("linkis");
//        for (int i = 1; i <= loopNum; i++) {
//            header.add(sdf.format(calendar.getTime()));
//            calendar.add(Calendar.MINUTE, 1);
//        }
        dataset.add(header);

        // 添加数据信息
        List<Object> realData;
        int max = 8192;
        int min = 1024;
        for (int i = 1; i <= loopNum; i++) {
            realData = new ArrayList<>();
            realData.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 1);
            realData.add(Math.random() * (max - min) + min);
            realData.add(Math.random() * (max - min) + min);
            realData.add( Math.random() * (max - min) + min);
//            realData.add(i * RandomUtils.nextInt(4));
//            realData.add(i * RandomUtils.nextInt(4));
//            realData.add(i * RandomUtils.nextInt(4));
            dataset.add(realData);
        }
//        dataset.add(ds1Data);
//        dataset.add(ds2Data);
//        dataset.add(ds3Data);
        Message message = Message.ok();
        message.setMethod("/dss/exchangis/main/metrics/engineresourcemem");
        message.data("dataset", dataset);
        return message;
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
        linkis.setEngine("linkis");
        linkis.setCpu("78%");
        linkis.setMem("4196Mi");
        list.add(linkis);

        Message message = Message.ok();
        message.setMethod("/dss/exchangis/main/metrics/engineresource");
        message.data("list", list);
        return message;
    }


}
