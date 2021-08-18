package com.webank.wedatasphere.exchangis.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsItem;
import com.webank.wedatasphere.exchangis.datasource.dto.DataSourceDTO;
import com.webank.wedatasphere.exchangis.datasource.dto.DataSourceDbTableColumnDTO;
import com.webank.wedatasphere.exchangis.datasource.dto.ExchangisDataSourceDTO;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVO;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVO;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceUpdateVO;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.*;
import com.webank.wedatasphere.linkis.datasource.client.response.*;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSource;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;
import com.webank.wedatasphere.linkis.metadatamanager.common.domain.MetaColumnInfo;
import com.webank.wedatasphere.linkis.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<ExchangisDataSourceUIViewer> getJobDataSourceUIs(Long jobId) {
        if (Objects.isNull(jobId)) {
            return null;
        }

        ExchangisJobInfo job = this.exchangisJobInfoMapper.selectById(jobId);
        if (Objects.isNull(job)) {
            return null;
        }

        String jobContent = job.getContent();
        ExchangisJobInfoContent content;
        List<ExchangisJobInfoContent> jobInfoContents;
        // 转换 content
        if (Strings.isNullOrEmpty(jobContent)) {
//            content = new ExchangisJobInfoContent();
            jobInfoContents = new ArrayList<>();
        } else {
            try {
//                content = this.mapper.readValue(jobContent, ExchangisJobInfoContent.class);
                jobInfoContents = this.mapper.readValue(jobContent, new TypeReference<List<ExchangisJobInfoContent>>() {});
            } catch (JsonProcessingException e) {
//                content = new ExchangisJobInfoContent();
                jobInfoContents = new ArrayList<>();
            }
        }
        List<ExchangisDataSourceUIViewer> uis = new ArrayList<>();
        for (ExchangisJobInfoContent cnt : jobInfoContents) {
            ExchangisDataSourceUIViewer viewer = wrapJobDataSourceUI(job, cnt);
            uis.add(viewer);
        }

        return uis;


//        // ----------- 构建 dataSourceIdsUI
//        ExchangisDataSourceIdsUI dataSourceIdsUI = ExchangisDataSourceUIViewBuilder.getDataSourceIdsUI(content.getDataSources());
//
//
//        // ----------- 构建 dataSourceParamsUI
//        ExchangisJobParamsContent params = content.getParams();
//        List<ExchangisJobParamConfig> sourceParamConfigs = Collections.emptyList();
//        List<ExchangisJobParamConfig> sinkParamConfigs = Collections.emptyList();
//        if (null != dataSourceIdsUI) {
//            ExchangisDataSourceIdUI source = dataSourceIdsUI.getSource();
//            if (null != source) {
//                String type = source.getType();
//                ExchangisDataSource exchangisSourceDataSource = this.context.getExchangisDataSource(type);
//                if (null != exchangisSourceDataSource) {
//                    sourceParamConfigs = exchangisSourceDataSource.getDataSourceParamConfigs();
//                }
//            }
//
//            ExchangisDataSourceIdUI sink = dataSourceIdsUI.getSink();
//            if (null != sink) {
//                String type = sink.getType();
//                ExchangisDataSource exchangisSinkDataSource = this.context.getExchangisDataSource(type);
//                if (null != exchangisSinkDataSource) {
//                    sinkParamConfigs = exchangisSinkDataSource.getDataSourceParamConfigs();
//                }
////                sinkParamsUI = exchangisSinkDataSource.getDataSourceParamsUI();
//            }
//        }
//
//        List<ExchangisJobParamsContent.ExchangisJobParamsItem> sourceParamsItems = Collections.emptyList();
//        List<ExchangisJobParamsContent.ExchangisJobParamsItem> sinkParamsItems = Collections.emptyList();
//        if (null != params && null != params.getSources()) {
//            sourceParamsItems = params.getSources();
//        }
//        if (null != params && null != params.getSinks()) {
//            sinkParamsItems = params.getSinks();
//        }
//
//        List<ElementUI> jobDataSourceParamsUI1 = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(sourceParamConfigs, sourceParamsItems);
//        List<ElementUI> jobDataSourceParamsUI2 = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(sinkParamConfigs, sinkParamsItems);
//        ExchangisDataSourceParamsUI paramsUI = new ExchangisDataSourceParamsUI();
//        paramsUI.setSources(jobDataSourceParamsUI1);
//        paramsUI.setSinks(jobDataSourceParamsUI2);
//
//        // ----------- 构建 dataSourceTransformsUI
//        ExchangisJobTransformsContent transforms = content.getTransforms();
////        ExchangisDataSourceTransformsUI dataSourceTransFormsUI = ExchangisDataSourceUIViewBuilder.getDataSourceTransFormsUI(transforms);
//
//        // ----------- 构建 dataSourceSettingsUI
//        List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings = content.getSettings();
//        String engineType = job.getEngineType();
//        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("type", engineType);
//        queryWrapper.eq("is_hidden", 0);
//        queryWrapper.eq("status", 1);
//        List<ExchangisJobParamConfig> settingParamConfigs = exchangisJobParamConfigMapper.selectList(queryWrapper);
//        List<ElementUI> jobDataSourceSettingsUI = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(settingParamConfigs, settings);
//
//        return new DefaultDataSourceUIViewer(dataSourceIdsUI, paramsUI, dataSourceTransFormsUI, jobDataSourceSettingsUI);
    }


    private ExchangisDataSourceUIViewer wrapJobDataSourceUI(ExchangisJobInfo job, ExchangisJobInfoContent content) {
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
        ExchangisJobTransformsContent transforms = content.getTransforms();
//        ExchangisDataSourceTransformsUI dataSourceTransFormsUI = ExchangisDataSourceUIViewBuilder.getDataSourceTransFormsUI(transforms);

        // ----------- 构建 dataSourceSettingsUI
        List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings = content.getSettings();
        String engineType = job.getEngineType();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", engineType);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        List<ExchangisJobParamConfig> settingParamConfigs = exchangisJobParamConfigMapper.selectList(queryWrapper);
        List<ElementUI> jobDataSourceSettingsUI = ExchangisDataSourceUIViewBuilder.getJobDataSourceParamsUI(settingParamConfigs, settings);

        return new DefaultDataSourceUIViewer(content.getSubJobName(), dataSourceIdsUI, paramsUI, transforms, jobDataSourceSettingsUI);
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
                    item.category(),
                    item.description(),
                    item.icon()
            ));
        }
//        String userName = SecurityFilter.getLoginUsername(request);
        String userName = "hdfs";

        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        GetAllDataSourceTypesResult result = linkisDataSourceRemoteClient.getAllDataSourceTypes(GetAllDataSourceTypesAction.builder()
                .setUser(userName)
                .build()
        );

        List<DataSourceType> allDataSourceType = result.getAllDataSourceType();

//        List<DataSourceType> dataSourceTypes = LinkisDataSourceClient.queryDataSourceTypes(userName);
//        让 dataSourceTypes 和自己加载的数据源类型做交集得到最终可以展示的数据源类型返回给UI
//        System.out.println(dataSourceTypes);

        return Message.ok().data("list", dtos);
    }

    @Transactional
    public Message create(HttpServletRequest request, String type, Map<String, Object> json) throws Exception {
        DataSourceCreateVO vo = null;
        try {
            vo = mapper.readValue(mapper.writeValueAsString(json), DataSourceCreateVO.class);
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(30401, e.getMessage());
        }

        String name = vo.getName();
        Map<String, Object> connectParams = vo.getConnectParams();
        //

        LinkisDataSourceRemoteClient client = context.getExchangisDataSource(type).getDataSourceRemoteClient();

//        client.execute()
        return Message.ok();
    }

    @Transactional
    public Message updateDataSource(HttpServletRequest request, String type, Long id, Map<String, Object> json) throws Exception {
        DataSourceUpdateVO vo = null;
        try {
            vo = mapper.readValue(mapper.writeValueAsString(json), DataSourceUpdateVO.class);
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(30401, e.getMessage());
        }

//        LinkisDataSourceClient.dataSourceClient().

        return Message.ok();
    }

    @Transactional
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
        ExchangisJobTransformsContent transforms = content.getTransforms();
//        List<ExchangisJobTransformsItem> transforms = content.getTransforms();
//        ExchangisDataSourceTransformsUI dataSourceTransFormsUI = ExchangisDataSourceUIViewBuilder.getDataSourceTransFormsUI(transforms);

        return Message.ok().data("ui", transforms);
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

        List<DataSourceDbTableColumnDTO> list = new ArrayList<>();
        allColumns.forEach(col -> {
            DataSourceDbTableColumnDTO item = new DataSourceDbTableColumnDTO();
            item.setName(col.getName());
            item.setType(col.getType());
            list.add(item);
        });

        return Message.ok().data("columns", list);
    }

    public Message queryDataSources(HttpServletRequest request, DataSourceQueryVO vo) throws Exception {
        if (null == vo) {
            vo = new DataSourceQueryVO();
        }
        String name = vo.getName();
        String type = vo.getType();
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisDataSourceRemoteClient dataSourceRemoteClient = exchangisDataSource.getDataSourceRemoteClient();
        QueryDataSourceResult result = dataSourceRemoteClient.queryDataSource(QueryDataSourceAction.builder()
                        .setSystem("")
                        .setName("linkis")
                        .setTypeId(1)
                        .setIdentifies("")
                        .setCurrentPage(1)
                        .setUser("hdfs")
                        .setPageSize(20).build()
        );

        List<DataSource> allDataSource = result.getAllDataSource();

        List<DataSourceDTO> dataSources = new ArrayList<>();
        allDataSource.forEach(ds -> {
            DataSourceDTO item = new DataSourceDTO();
            item.setId(ds.getId());
            item.setCreateIdentify(ds.getCreateIdentify());
            item.setName(ds.getDataSourceName());
            item.setType(type);
            dataSources.add(item);
        });


        return Message.ok().data("list", dataSources);
    }

    public Message listAllDataSources(HttpServletRequest request) {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(QueryDataSourceAction.builder()
                .setSystem("linkis")
                .setName("linkis")
                .setTypeId(1)
                .setIdentifies("")
                .setCurrentPage(1)
                .setUser("hdfs")
                .setPageSize(20).build()
        );

        List<DataSource> allDataSource = result.getAllDataSource();

        List<DataSourceDTO> dataSources = new ArrayList<>();
        allDataSource.forEach(ds -> {
            DataSourceDTO item = new DataSourceDTO();
            item.setId(ds.getId());
            item.setCreateIdentify(ds.getCreateIdentify());
            item.setName(ds.getDataSourceName());
            item.setType(ds.getDataSourceType().getName());
            dataSources.add(item);
        });


        return Message.ok().data("list", dataSources);
    }
}
