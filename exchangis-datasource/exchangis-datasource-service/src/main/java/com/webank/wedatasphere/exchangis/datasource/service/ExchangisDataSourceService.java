package com.webank.wedatasphere.exchangis.datasource.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobInfoMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ExchangisDataSourceParamsUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsContent;
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
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class ExchangisDataSourceService extends AbstractDataSourceService implements DataSourceUIGetter, DataSourceServiceDispatcher, MetadataServiceDispatcher {

    @Autowired
    public ExchangisDataSourceService(ExchangisDataSourceContext context, ExchangisJobInfoMapper exchangisJobInfoMapper, ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        super(context, exchangisJobParamConfigMapper, exchangisJobInfoMapper);
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

        List<ExchangisJobInfoContent> jobInfoContents = this.parseJobContent(job.getContent());
        List<ExchangisDataSourceUIViewer> uis = new ArrayList<>();
        for (ExchangisJobInfoContent cnt : jobInfoContents) {
            ExchangisDataSourceUIViewer viewer = buildAllUI(job, cnt);
            uis.add(viewer);
        }

        return uis;
    }

    // 根据数据源类型获取参数
    @Override
    public List<ElementUI> getDataSourceParamsUI(String dsType) {
        ExchangisDataSource exchangisDataSource = this.context.getExchangisDataSource(dsType);
        List<ExchangisJobParamConfig> paramConfigs = exchangisDataSource.getDataSourceParamConfigs();
        return this.buildDataSourceParamsUI(paramConfigs);
    }

    @Override
    public List<ElementUI> getJobEngineSettingsUI(String engineType) {
        return this.buildJobSettingsUI(engineType);
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
        String userName = SecurityFilter.getLoginUsername(request);
//        String userName = "hdfs";

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
        DataSourceCreateVO vo;
        try {
            vo = mapper.readValue(mapper.writeValueAsString(json), DataSourceCreateVO.class);
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(30401, e.getMessage());
        }

        String name = vo.getName();
        Map<String, Object> connectParams = vo.getConnectParams();
        //

        LinkisDataSourceRemoteClient client = context.getExchangisDataSource(type).getDataSourceRemoteClient();

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

        List<ExchangisJobInfoContent> jobInfoContents = this.parseJobContent(job.getContent());
        List<ExchangisDataSourceParamsUI> uis = new ArrayList<>();
        for (ExchangisJobInfoContent cnt : jobInfoContents) {
            uis.add(this.buildDataSourceParamsUI(cnt));
        }

        return Message.ok().data("ui", uis);
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

    public Message getJobDataSourceSettingsUI(Long jobId, String jobName) {
        if (Objects.isNull(jobId) || Strings.isNullOrEmpty(jobName)) {
            return null;
        }

        ExchangisJobInfo job = this.exchangisJobInfoMapper.selectById(jobId);
        if (Objects.isNull(job)) {
            return null;
        }

        List<ExchangisJobInfoContent> contents = this.parseJobContent(job.getContent());

        for (ExchangisJobInfoContent content : contents) {
            if (content.getSubJobName().equalsIgnoreCase(jobName)) {
                List<ElementUI> uis = this.buildJobSettingsUI(job.getEngineType(), content);
                return Message.ok().data("uis", uis);
            }
        }

        return Message.ok().data("ui", Collections.emptyList());

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
