package com.webank.wedatasphere.exchangis.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobInfoMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.*;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.DefaultDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.builder.ExchangisDataSourceUIViewBuilder;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsItem;
import com.webank.wedatasphere.exchangis.datasource.dto.ExchangisDataSourceDTO;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceUpdateVO;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.MetadataGetColumnsAction;
import com.webank.wedatasphere.linkis.datasource.client.request.MetadataGetDatabasesAction;
import com.webank.wedatasphere.linkis.datasource.client.request.MetadataGetTablesAction;
import com.webank.wedatasphere.linkis.datasource.client.response.MetadataGetColumnsResult;
import com.webank.wedatasphere.linkis.datasource.client.response.MetadataGetDatabasesResult;
import com.webank.wedatasphere.linkis.datasource.client.response.MetadataGetTablesResult;
import com.webank.wedatasphere.linkis.metadatamanager.common.domain.MetaColumnInfo;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class ExchangisDataSourceService implements DataSourceUIGetter, DataSourceServiceDispatcher, MetadataServiceDispatcher {

    private final ExchangisDataSourceContext context;
    private final ExchangisJobParamConfigMapper exchangisJobParamConfigMapper;
    private final ExchangisJobInfoMapper exchangisJobInfoMapper;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ExchangisDataSourceService(ExchangisDataSourceContext context, ExchangisJobInfoMapper exchangisJobInfoMapper, ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.context = context;
        this.exchangisJobInfoMapper = exchangisJobInfoMapper;
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }

    @Override
    public ExchangisDataSourceUIViewer getJobDataSourceUIs(Long jobId) {
        if (Objects.isNull(jobId)) {
            return null;
        }

        ExchangisJobInfo job = this.exchangisJobInfoMapper.selectById(jobId);
        if (Objects.isNull(job)) {
            return null;
        }

        String jobContent = job.getContent();
        ExchangisJobInfoContent content;
        // 转换 content
        if (Strings.isNullOrEmpty(jobContent)) {
            content = new ExchangisJobInfoContent();
        } else {
            try {
                content = this.mapper.readValue(jobContent, ExchangisJobInfoContent.class);
            } catch (JsonProcessingException e) {
                content = new ExchangisJobInfoContent();
            }
        }

        // ----------- 构建 dataSourceIdsUI
        ExchangisDataSourceIdsUI dataSourceIdsUI = ExchangisDataSourceUIViewBuilder.getDataSourceIdsUI(content.getDataSources());


        // ----------- 构建 dataSourceParamsUI
        ExchangisJobParamsContent params = content.getParams();
        List<ExchangisJobParamConfig> sourceParamConfigs = Collections.emptyList();
        List<ExchangisJobParamConfig> sinkParamConfigs = Collections.emptyList();
        if (null != dataSourceIdsUI) {
            ExchangisDataSourceIdUI source = dataSourceIdsUI.getSource();
            if (null != source) {
                String type = source.getType();
                ExchangisDataSource exchangisSourceDataSource = this.context.getExchangisDataSource(type);
                if (null != exchangisSourceDataSource) {
                    sourceParamConfigs = exchangisSourceDataSource.getDataSourceParamConfigs();
                }
            }

            ExchangisDataSourceIdUI sink = dataSourceIdsUI.getSink();
            if (null != sink) {
                String type = sink.getType();
                ExchangisDataSource exchangisSinkDataSource = this.context.getExchangisDataSource(type);
                if (null != exchangisSinkDataSource) {
                    sinkParamConfigs = exchangisSinkDataSource.getDataSourceParamConfigs();
                }
//                sinkParamsUI = exchangisSinkDataSource.getDataSourceParamsUI();
            }
        }

        List<ExchangisJobParamsContent.ExchangisJobParamsItem> sourceParamsItems = Collections.emptyList();
        List<ExchangisJobParamsContent.ExchangisJobParamsItem> sinkParamsItems = Collections.emptyList();
        if (null != params && null != params.getSources()) {
            sourceParamsItems = params.getSources();
        }
        if (null != params && null != params.getSinks()) {
            sinkParamsItems = params.getSinks();
        }

        List<ElementUI> jobDataSourceParamsUI1 = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(sourceParamConfigs, sourceParamsItems);
        List<ElementUI> jobDataSourceParamsUI2 = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(sinkParamConfigs, sinkParamsItems);
        ExchangisDataSourceParamsUI paramsUI = new ExchangisDataSourceParamsUI();
        paramsUI.setSources(jobDataSourceParamsUI1);
        paramsUI.setSinks(jobDataSourceParamsUI2);

        // ----------- 构建 dataSourceTransformsUI
        List<ExchangisJobTransformsItem> transforms = content.getTransforms();
        ExchangisDataSourceTransformsUI dataSourceTransFormsUI = ExchangisDataSourceUIViewBuilder.getDataSourceTransFormsUI(transforms);

        // ----------- 构建 dataSourceSettingsUI
        List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings = content.getSettings();
        String engineType = job.getEngineType();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", engineType);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        List<ExchangisJobParamConfig> settingParamConfigs = exchangisJobParamConfigMapper.selectList(queryWrapper);
        List<ElementUI> jobDataSourceSettingsUI = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(settingParamConfigs, settings);

        return new DefaultDataSourceUIViewer(dataSourceIdsUI, paramsUI, dataSourceTransFormsUI, jobDataSourceSettingsUI);
    }

    // 根据数据源类型获取参数
    @Override
    public List<ElementUI> getDataSourceParamsUI(String dsType) {
        ExchangisDataSource exchangisDataSource = this.context.getExchangisDataSource(dsType);
        List<ExchangisJobParamConfig> paramConfigs = exchangisDataSource.getDataSourceParamConfigs();
        return ExchangisDataSourceUIViewBuilder.getDataSourceParamsUI(paramConfigs);
    }

    @Override
    public List<ElementUI> getJobEngineSettingsUI(String engineType) {
        if (Strings.isNullOrEmpty(engineType)) {
            return Collections.emptyList();
        }
        engineType = engineType.trim().toUpperCase();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", engineType);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        List<ExchangisJobParamConfig> jobSettingConfigs = this.exchangisJobParamConfigMapper.selectList(queryWrapper);
        return ExchangisDataSourceUIViewBuilder.getDataSourceParamsUI(jobSettingConfigs);
    }

    /**
     * 根据 LocalExchangisDataSourceLoader 加载到的本地的数据源与 Linkis 支持的数据源
     * 做比较，筛选出可以给前端展示的数据源类型
     */
    public Message listDataSources(HttpServletRequest request) {
        Collection<ExchangisDataSource> all = this.context.all();
        List<ExchangisDataSourceDTO> dtos = new ArrayList<>();
        for (ExchangisDataSource item : all) {
            dtos.add(new ExchangisDataSourceDTO(
                    item.type(),
                    item.description(),
                    item.icon()
            ));
        }
//        String userName = SecurityFilter.getLoginUsername(request);
//        String userName = "hdfs";
//        List<DataSourceType> dataSourceTypes = LinkisDataSourceClient.queryDataSourceTypes(userName);
//        让 dataSourceTypes 和自己加载的数据源类型做交集得到最终可以展示的数据源类型返回给UI
//        System.out.println(dataSourceTypes);

        return Message.ok().data("list", dtos);
    }

    public Message create(HttpServletRequest request, Map<String, Object> json) {

        return Message.ok();
    }

    public Message updateDataSource(HttpServletRequest request, Map<String, Object> json) throws Exception {
        DataSourceUpdateVO vo = null;
        try {
            vo = mapper.readValue(mapper.writeValueAsString(json), DataSourceUpdateVO.class);
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(30401, e.getMessage());
        }

        Long id = vo.getId();
//        LinkisDataSourceClient.dataSourceClient().

        return Message.ok();
    }

    public Message deleteDataSource(HttpServletRequest request, String type, Long id) {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisDataSourceRemoteClient dataSourceRemoteClient = exchangisDataSource.getDataSourceRemoteClient();
        // 删除数据源 TODO

        return Message.ok();
    }

    public Message queryDataSourceDBs(HttpServletRequest request, String type, Long id) {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = exchangisDataSource.getMetaDataRemoteClient();

        MetadataGetDatabasesResult databases = metaDataRemoteClient.getDatabases(MetadataGetDatabasesAction.builder()
                .setSystem("system")
                .setDataSourceId(id)
                .setUser("hdfs")
                .build());

        List<String> dbs = databases.getDbs();

        return Message.ok().data("dbs", dbs);
    }

    public Message queryDataSourceDBTables(HttpServletRequest request, String type, Long id, String dbName) {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = exchangisDataSource.getMetaDataRemoteClient();
        MetadataGetTablesResult tables = metaDataRemoteClient.getTables(MetadataGetTablesAction.builder()
                .setSystem("system")
                .setDataSourceId(id)
                .setDatabase(dbName)
                .setUser("hdfs")
                .build()
        );

        List<String> tbs = tables.getTables();

        return Message.ok().data("tbs", tbs);
    }

    public Message getJobDataSourceParamsUI(Long jobId) {
        if (Objects.isNull(jobId)) {
            return null;
        }

        ExchangisJobInfo job = this.exchangisJobInfoMapper.selectById(jobId);
        if (Objects.isNull(job)) {
            return null;
        }

        String jobContent = job.getContent();
        ExchangisJobInfoContent content;
        // 转换 content
        if (Strings.isNullOrEmpty(jobContent)) {
            content = new ExchangisJobInfoContent();
        } else {
            try {
                content = this.mapper.readValue(jobContent, ExchangisJobInfoContent.class);
            } catch (JsonProcessingException e) {
                content = new ExchangisJobInfoContent();
            }
        }

        // ----------- 构建 dataSourceIdsUI
        ExchangisDataSourceIdsUI dataSourceIdsUI = ExchangisDataSourceUIViewBuilder.getDataSourceIdsUI(content.getDataSources());

        // ----------- 构建 dataSourceParamsUI
        ExchangisJobParamsContent params = content.getParams();
        List<ExchangisJobParamConfig> sourceParamConfigs = Collections.emptyList();
        List<ExchangisJobParamConfig> sinkParamConfigs = Collections.emptyList();
        if (null != dataSourceIdsUI) {
            ExchangisDataSourceIdUI source = dataSourceIdsUI.getSource();
            if (null != source) {
                String type = source.getType();
                ExchangisDataSource exchangisSourceDataSource = this.context.getExchangisDataSource(type);
                if (null != exchangisSourceDataSource) {
                    sourceParamConfigs = exchangisSourceDataSource.getDataSourceParamConfigs();
                }
            }

            ExchangisDataSourceIdUI sink = dataSourceIdsUI.getSink();
            if (null != sink) {
                String type = sink.getType();
                ExchangisDataSource exchangisSinkDataSource = this.context.getExchangisDataSource(type);
                if (null != exchangisSinkDataSource) {
                    sinkParamConfigs = exchangisSinkDataSource.getDataSourceParamConfigs();
                }
            }
        }

        List<ExchangisJobParamsContent.ExchangisJobParamsItem> sourceParamsItems = Collections.emptyList();
        List<ExchangisJobParamsContent.ExchangisJobParamsItem> sinkParamsItems = Collections.emptyList();
        if (null != params && null != params.getSources()) {
            sourceParamsItems = params.getSources();
        }
        if (null != params && null != params.getSinks()) {
            sinkParamsItems = params.getSinks();
        }

        List<ElementUI> jobDataSourceParamsUI1 = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(sourceParamConfigs, sourceParamsItems);
        List<ElementUI> jobDataSourceParamsUI2 = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(sinkParamConfigs, sinkParamsItems);
        ExchangisDataSourceParamsUI paramsUI = new ExchangisDataSourceParamsUI();
        paramsUI.setSources(jobDataSourceParamsUI1);
        paramsUI.setSinks(jobDataSourceParamsUI2);

        return Message.ok().data("ui", paramsUI);
    }

    public Message getJobDataSourceTransformsUI(Long jobId) {
        if (Objects.isNull(jobId)) {
            return null;
        }

        ExchangisJobInfo job = this.exchangisJobInfoMapper.selectById(jobId);
        if (Objects.isNull(job)) {
            return null;
        }

        String jobContent = job.getContent();
        ExchangisJobInfoContent content;
        // 转换 content
        if (Strings.isNullOrEmpty(jobContent)) {
            content = new ExchangisJobInfoContent();
        } else {
            try {
                content = this.mapper.readValue(jobContent, ExchangisJobInfoContent.class);
            } catch (JsonProcessingException e) {
                content = new ExchangisJobInfoContent();
            }
        }

        // ----------- 构建 dataSourceTransformsUI
        List<ExchangisJobTransformsItem> transforms = content.getTransforms();
        ExchangisDataSourceTransformsUI dataSourceTransFormsUI = ExchangisDataSourceUIViewBuilder.getDataSourceTransFormsUI(transforms);

        return Message.ok().data("ui", dataSourceTransFormsUI);
    }

    public Message getJobDataSourceSettingsUI(Long jobId) {
        if (Objects.isNull(jobId)) {
            return null;
        }

        ExchangisJobInfo job = this.exchangisJobInfoMapper.selectById(jobId);
        if (Objects.isNull(job)) {
            return null;
        }

        String jobContent = job.getContent();
        ExchangisJobInfoContent content;
        // 转换 content
        if (Strings.isNullOrEmpty(jobContent)) {
            content = new ExchangisJobInfoContent();
        } else {
            try {
                content = this.mapper.readValue(jobContent, ExchangisJobInfoContent.class);
            } catch (JsonProcessingException e) {
                content = new ExchangisJobInfoContent();
            }
        }

        // ----------- 构建 dataSourceSettingsUI
        List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings = content.getSettings();
        String engineType = job.getEngineType();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", engineType);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        List<ExchangisJobParamConfig> settingParamConfigs = exchangisJobParamConfigMapper.selectList(queryWrapper);
        List<ElementUI> jobDataSourceSettingsUI = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(settingParamConfigs, settings);

        return Message.ok().data("ui", jobDataSourceSettingsUI);
    }

    public Message queryDataSourceDBTableFields(HttpServletRequest request, String type, Long id, String dbName, String tableName) {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = exchangisDataSource.getMetaDataRemoteClient();

        MetadataGetColumnsResult columns = metaDataRemoteClient.getColumns(MetadataGetColumnsAction.builder()
                .setSystem("system")
                .setDataSourceId(id)
                .setDatabase(dbName)
                .setTable(tableName)
                .setUser("hdfs")
                .build());

        List<MetaColumnInfo> allColumns = columns.getAllColumns();

        return Message.ok().data("columns", allColumns);
    }
}
