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
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ExchangisDataSourceParamsUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsContent;
import com.webank.wedatasphere.exchangis.datasource.dto.*;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVO;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVO;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceUpdateVO;
import com.webank.wedatasphere.exchangis.datasource.vo.FieldMappingVO;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.*;
import com.webank.wedatasphere.linkis.datasource.client.response.*;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSource;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;
import com.webank.wedatasphere.linkis.httpclient.response.Result;
import com.webank.wedatasphere.linkis.metadatamanager.common.Json;
import com.webank.wedatasphere.linkis.metadatamanager.common.domain.MetaColumnInfo;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class ExchangisDataSourceService extends AbstractDataSourceService implements DataSourceUIGetter, DataSourceServiceDispatcher, MetadataServiceDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangisDataSourceService.class);

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
    public List<ElementUI> getDataSourceParamsUI(String dsType, String engineAndDirection) {

        ExchangisDataSource exchangisDataSource = this.context.getExchangisDataSource(dsType);
        List<ExchangisJobParamConfig> paramConfigs = exchangisDataSource.getDataSourceParamConfigs();
        List<ExchangisJobParamConfig> filteredConfigs = new ArrayList<>();
        for (ExchangisJobParamConfig paramConfig : paramConfigs) {
            if (Optional.ofNullable(paramConfig.getConfigDirection()).orElse("").equalsIgnoreCase(engineAndDirection)) {
                filteredConfigs.add(paramConfig);
            }
        }
        return this.buildDataSourceParamsUI(filteredConfigs);
    }

    @Override
    public List<ElementUI> getJobEngineSettingsUI(String engineType) {
        return this.buildJobSettingsUI(engineType);
    }

    /**
     * 根据 LocalExchangisDataSourceLoader 加载到的本地的数据源与 Linkis 支持的数据源
     * 做比较，筛选出可以给前端展示的数据源类型
     */
    public Message listDataSources(HttpServletRequest request) throws Exception {
        Collection<ExchangisDataSource> all = this.context.all();
        List<ExchangisDataSourceDTO> dtos = new ArrayList<>();

        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("listDataSources userName:" + userName);
        // 通过 datasourcemanager 获取的数据源类型和context中的数据源通过 type 和 name 比较
        // 以 exchangis 中注册了的数据源集合为准

        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        GetAllDataSourceTypesResult result;
        try {
            result = linkisDataSourceRemoteClient.getAllDataSourceTypes(GetAllDataSourceTypesAction.builder()
                    .setUser(userName)
                    .build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_TYPES_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_TYPES_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(result)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_TYPES_ERROR.getCode(), "datasource get types null or empty");
        }

        List<DataSourceType> allDataSourceType = result.getAllDataSourceType();
        if (Objects.isNull(allDataSourceType)) allDataSourceType = Collections.emptyList();

        for (DataSourceType type : allDataSourceType) {
            for (ExchangisDataSource item : all) {
                if (item.name().equalsIgnoreCase(type.getName())) {
                    ExchangisDataSourceDTO dto = new ExchangisDataSourceDTO(
                            type.getId(),
                            item.classifier(),
                            item.name()
                    );
                    dto.setDescription(item.description());
                    dto.setIcon(item.icon());
                    dto.setOption(item.option());
                    dtos.add(dto);
                }
            }
        }

        return Message.ok().data("list", dtos);
    }

    @Transactional
    public Message create(HttpServletRequest request, /*String type, */Map<String, Object> json) throws Exception {
        DataSourceCreateVO vo;
        try {
            vo = mapper.readValue(mapper.writeValueAsString(json), DataSourceCreateVO.class);
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARSE_JSON_ERROR.getCode(), e.getMessage());
        }

        String user = SecurityFilter.getLoginUsername(request);
        LOGGER.info("createDatasource userName:" + user);

        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(vo.getDataSourceTypeId());
        if (Objects.isNull(exchangisDataSource)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CONTEXT_GET_DATASOURCE_NULL.getCode(), "exchangis context get datasource null");
        }

        LinkisDataSourceRemoteClient client = exchangisDataSource.getDataSourceRemoteClient();
        Map<String, Object> connectParams = vo.getConnectParams();
        if (!Objects.isNull(connectParams)) {
            json.put("parameter", mapper.writeValueAsString(connectParams));
        }
        LOGGER.info("create datasource json as follows");
        Set<Map.Entry<String, Object>> entries = json.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            LOGGER.info("key {} : value {}", entry.getKey(), entry.getValue());
        }
//        CreateDataSourceResult result;
        String responseBody;
        try {
//            result = client.createDataSource(CreateDataSourceAction.builder()
//                    .setUser(user)
//                    .addRequestPayloads(json)
//                    .build()
//            );

            Result execute = client.execute(CreateDataSourceAction.builder()
                    .setUser(user)
                    .addRequestPayloads(json)
                    .build()
            );
            responseBody = execute.getResponseBody();
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_CREATE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_CREATE_ERROR.getCode(), e.getMessage());
            }
        }

//        if (Objects.isNull(result)) {
        if (Strings.isNullOrEmpty(responseBody)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_CREATE_ERROR.getCode(), "datasource create response null or empty");
        }

        CreateDataSourceSuccessResultDTO result = Json.fromJson(responseBody, CreateDataSourceSuccessResultDTO.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }
//        Long dataSourceId = result.getInsert_id();
        Long dataSourceId = result.getData().getId();
        UpdateDataSourceParameterResult updateDataSourceParameterResult;
        try {
            // 创建完成后发布数据源参数，形成一个版本
            updateDataSourceParameterResult = client.updateDataSourceParameter(
                    UpdateDataSourceParameterAction.builder()
                            .setUser(user)
                            .setDataSourceId(dataSourceId + "")
                            .addRequestPayloads(json)
                            .build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(updateDataSourceParameterResult)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), "datasource update params version null or empty");
        }

        if (updateDataSourceParameterResult.getStatus() != 0) {
            throw new ExchangisDataSourceException(updateDataSourceParameterResult.getStatus(), updateDataSourceParameterResult.getMessage());
        }
        return Message.ok().data("id", dataSourceId);
    }

    @Transactional
    public Message updateDataSource(HttpServletRequest request,/* String type,*/ Long id, Map<String, Object> json) throws Exception {
        DataSourceUpdateVO vo;
        try {
            vo = mapper.readValue(mapper.writeValueAsString(json), DataSourceUpdateVO.class);
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(30401, e.getMessage());
        }
        String user = SecurityFilter.getLoginUsername(request);
        LOGGER.info("updateDataSource userName:" + user);

        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(vo.getDataSourceTypeId());
        if (Objects.isNull(exchangisDataSource)) {
            throw new ExchangisDataSourceException(30401, "exchangis.datasource.null");
        }

        LinkisDataSourceRemoteClient client = exchangisDataSource.getDataSourceRemoteClient();
//        UpdateDataSourceResult updateDataSourceResult;
        String responseBody;
        try {
//            updateDataSourceResult = client.updateDataSource(UpdateDataSourceAction.builder()
//                    .setUser(user)
//                    .setDataSourceId(id+"")
//                    .addRequestPayloads(json)
//                    .build()
//            );
            Result execute = client.execute(UpdateDataSourceAction.builder()
                    .setUser(user)
                    .setDataSourceId(id + "")
                    .addRequestPayloads(json)
                    .build()
            );
            responseBody = execute.getResponseBody();
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_ERROR.getCode(), e.getMessage());
            }
        }

//        if (Objects.isNull(updateDataSourceResult)) {
        if (Strings.isNullOrEmpty(responseBody)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_ERROR.getCode(), "datasource update null or empty");
        }

        UpdateDataSourceSuccessResultDTO updateDataSourceResult = Json.fromJson(responseBody, UpdateDataSourceSuccessResultDTO.class);

        if (updateDataSourceResult.getStatus() != 0) {
            throw new ExchangisDataSourceException(updateDataSourceResult.getStatus(), updateDataSourceResult.getMessage());
        }

        UpdateDataSourceParameterResult updateDataSourceParameterResult;
        try {
            updateDataSourceParameterResult = client.updateDataSourceParameter(
                    UpdateDataSourceParameterAction.builder()
                            .setDataSourceId(id + "")
                            .setUser(user)
                            .addRequestPayloads(json)
                            .build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(updateDataSourceParameterResult)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), "datasource update params version null or empty");
        }

        if (updateDataSourceParameterResult.getStatus() != 0) {
            throw new ExchangisDataSourceException(updateDataSourceParameterResult.getStatus(), updateDataSourceParameterResult.getMessage());
        }

        return Message.ok();
    }

    @Transactional
    public Message deleteDataSource(HttpServletRequest request, /*String type,*/ Long id) throws Exception {
        LinkisDataSourceRemoteClient dataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
//        DeleteDataSourceResult result;
        String responseBody;
        try {
            String user = SecurityFilter.getLoginUsername(request);
            LOGGER.info("deleteDataSource userName:" + user);
//            result = dataSourceRemoteClient.deleteDataSource(
//                    new DeleteDataSourceAction.Builder().setUser(user).setResourceId(id+"").builder()
//            );

            Result execute = dataSourceRemoteClient.execute(
                    new DeleteDataSourceAction.Builder().setUser(user).setResourceId(id + "").builder()
            );
            responseBody = execute.getResponseBody();

        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_DELETE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_DELETE_ERROR.getCode(), e.getMessage());
            }
        }

//        if (Objects.isNull(result)) {
        if (Strings.isNullOrEmpty(responseBody)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_DELETE_ERROR.getCode(), "datasource delete null or empty");
        }

        DeleteDataSourceSuccessResultDTO result = Json.fromJson(responseBody, DeleteDataSourceSuccessResultDTO.class);

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }
//        return Message.ok().data("id", result.getRemove_id());
        return Message.ok().data("id", result.getData().getId());
    }

    public Message queryDataSourceDBs(HttpServletRequest request, String type, Long id) throws Exception {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = exchangisDataSource.getMetaDataRemoteClient();

        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("queryDataSourceDBs userName:" + userName);
        MetadataGetDatabasesResult databases;
        try {
            databases = metaDataRemoteClient.getDatabases(MetadataGetDatabasesAction.builder()
//                    .setSystem("system")
                    .setSystem(type)
                    .setDataSourceId(id)
                    .setUser(userName)
                    .build());
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_DATABASES_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_DATABASES_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(databases)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_DATABASES_ERROR.getCode(), "metadata get databases null or empty");
        }

        List<String> dbs = Optional.ofNullable(databases.getDbs()).orElse(new ArrayList<>());

        return Message.ok().data("dbs", dbs);
    }

    public Message queryDataSourceDBTables(HttpServletRequest request, String type, Long id, String dbName) throws Exception {
        String user = SecurityFilter.getLoginUsername(request);
        LOGGER.info("queryDataSourceDBTables userName:" + user);

        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        MetadataGetTablesResult tables;
        try {
            LinkisMetaDataRemoteClient metaDataRemoteClient = exchangisDataSource.getMetaDataRemoteClient();
            tables = metaDataRemoteClient.getTables(MetadataGetTablesAction.builder()
                    .setSystem(type)
                    .setDataSourceId(id)
                    .setDatabase(dbName)
                    .setUser(user)
                    .build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_TABLES_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_TABLES_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(tables)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_TABLES_ERROR.getCode(), "metadata get tables null or empty");
        }

        List<String> tbs = Optional.ofNullable(tables.getTables()).orElse(new ArrayList<>());

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

        return Message.ok().data("ui", transforms);
    }

    public Message getJobDataSourceSettingsUI(Long jobId, String jobName) throws Exception {
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

    public Message queryDataSourceDBTableFields(HttpServletRequest request, String type, Long id, String dbName, String tableName) throws Exception {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = exchangisDataSource.getMetaDataRemoteClient();

        String user = SecurityFilter.getLoginUsername(request);
        LOGGER.info("queryDataSourceDBTableFields userName:" + user);
        List<MetaColumnInfo> allColumns;
        try {
            MetadataGetColumnsResult columns = metaDataRemoteClient.getColumns(MetadataGetColumnsAction.builder()
                    .setSystem(type)
                    .setDataSourceId(id)
                    .setDatabase(dbName)
                    .setTable(tableName)
                    .setUser(user)
                    .build());

            allColumns = columns.getAllColumns();
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_COLUMNS_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_COLUMNS_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(allColumns)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_COLUMNS_ERROR.getCode(), "metadata get columns null or empty");
        }

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
        String username = SecurityFilter.getLoginUsername(request);
        LOGGER.info("queryDataSources userName:" + username);
        Integer page = Objects.isNull(vo.getPage()) ? 1 : vo.getPage();
        Integer pageSize = Objects.isNull(vo.getPageSize()) ? 20 : vo.getPageSize();

        String dataSourceName = Objects.isNull(vo.getName()) ? "" : vo.getName();
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        QueryDataSourceResult result;
        try {
            QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                    .setSystem("system")
                    .setName(dataSourceName)
                    .setIdentifies("")
                    .setCurrentPage(page)
                    .setUser(username)
                    .setPageSize(pageSize);

            Long typeId = vo.getTypeId();
            if (!Objects.isNull(typeId)) {
                builder.setTypeId(typeId);
            }
//            if (!Strings.isNullOrEmpty(dataSourceName)) {
//                builder.setName(dataSourceName);
//            }
            if (!Strings.isNullOrEmpty(vo.getTypeName())) {
                builder.setSystem(vo.getTypeName());
            }

            QueryDataSourceAction action = builder.build();
            result = linkisDataSourceRemoteClient.queryDataSource(action);
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
            }
        }
        if (Objects.isNull(result)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), "datasource query response body null or empty");
        }

        List<DataSource> allDataSource = result.getAllDataSource();

        List<DataSourceDTO> dataSources = new ArrayList<>();
        allDataSource.forEach(ds -> {
            DataSourceDTO item = new DataSourceDTO();
            item.setId(ds.getId());
            item.setCreateIdentify(ds.getCreateIdentify());
            item.setName(ds.getDataSourceName());
            item.setType(ds.getCreateSystem());
            item.setDataSourceTypeId(ds.getDataSourceTypeId());
            item.setLabels(ds.getLabels());
            item.setDesc(ds.getDataSourceDesc());
            item.setCreateUser(ds.getCreateUser());
            item.setModifyUser(ds.getModifyUser());
            item.setModifyTime(ds.getModifyTime());
            item.setVersionId(ds.getVersionId());
            item.setExpire(ds.isExpire());
            dataSources.add(item);
        });

        return Message.ok().data("list", dataSources);
    }

    public Message listAllDataSources(HttpServletRequest request, String typeName, Long typeId, Integer page, Integer pageSize) throws ExchangisDataSourceException {
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("listAllDataSources userName:" + userName);

        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                .setSystem("system")
                .setIdentifies("")
                .setUser(userName);

        if (!Strings.isNullOrEmpty(typeName)) {
            builder.setName(typeName);
        }
        if (!Objects.isNull(typeId)) {
            builder.setTypeId(typeId);
        }
        if (!Objects.isNull(page)) {
            builder.setCurrentPage(page);
        } else {
            builder.setCurrentPage(1);
        }
        if (!Objects.isNull(pageSize)) {
            builder.setPageSize(pageSize);
        } else {
            builder.setPageSize(200);
        }

        List<DataSource> allDataSource;
        try {
            QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(builder.build());
            allDataSource = result.getAllDataSource();
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
            }
        }
        List<DataSourceDTO> dataSources = new ArrayList<>();
        if (!Objects.isNull(allDataSource)) {
            allDataSource.forEach(ds -> {
                DataSourceDTO item = new DataSourceDTO();
                item.setId(ds.getId());
                item.setCreateIdentify(ds.getCreateIdentify());
                item.setName(ds.getDataSourceName());
                item.setType(ds.getDataSourceType().getName());
                item.setDataSourceTypeId(ds.getDataSourceTypeId());
                dataSources.add(item);
            });
        }
        return Message.ok().data("list", dataSources);
    }

    public Message getDataSource(HttpServletRequest request, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        try {
            String userName = SecurityFilter.getLoginUsername(request);
            LOGGER.info("getDataSource userName:" + userName);

//            GetDataSourceInfoResultDTO
            Result execute = linkisDataSourceRemoteClient.execute(
                    GetInfoByDataSourceIdAction.builder().setSystem("system").setUser(userName).setDataSourceId(id).build()
            );
            String responseBody = execute.getResponseBody();

            GetDataSourceInfoResultDTO result = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);

//            GetInfoByDataSourceIdResult result = linkisDataSourceRemoteClient.getInfoByDataSourceId(
//                    GetInfoByDataSourceIdAction.builder().setSystem("system").setUser(userName).setDataSourceId(id).build()
//            );
            if (result.getStatus() != 0) {
                throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
            }
            return Message.ok().data("info", result.getData().getInfo());
//            return Message.ok().data("info", Objects.isNull(result.getInfo()) ? null : result.getInfo());
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
            }
        }
    }

    public Message getDataSourceVersionsById(HttpServletRequest request, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("getDataSourceVersionsById userName:" + userName);
//        GetInfoByDataSourceIdResult result;
        GetDataSourceInfoResultDTO result;
        try {
            // 先根据ID获取数据源详情
//            result = linkisDataSourceRemoteClient.getInfoByDataSourceId(
//                    GetInfoByDataSourceIdAction.builder().setSystem("system").setUser(userName).setDataSourceId(id).build()
//            );

            Result execute = linkisDataSourceRemoteClient.execute(
                    GetInfoByDataSourceIdAction.builder().setSystem("system").setUser(userName).setDataSourceId(id).build()
            );
            String responseBody = execute.getResponseBody();

            result = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);

//            GetInfoByDataSourceIdResult result = linkisDataSourceRemoteClient.getInfoByDataSourceId(
//                    GetInfoByDataSourceIdAction.builder().setSystem("system").setUser(userName).setDataSourceId(id).build()
//            );
            if (result.getStatus() != 0) {
                throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
            }
//            return Message.ok().data("info", result.getData().getInfo());

        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_GET_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_GET_DATASOURCE_ERROR.getCode(), e.getMessage());
            }
        }
//        if (Objects.isNull(result)) {
//            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_GET_DATASOURCE_ERROR.getCode(), "response body null or empty");
//        }

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        GetDataSourceInfoResultDTO.DataSourceItemDTO info = result.getData().getInfo();
        Integer publishedVersionId = info.getPublishedVersionId();
//        Long publishedVersionId = null;
//        Map<String, Object> info = result.getInfo();

//        if (!Objects.isNull(info)) {
//            publishedVersionId = Long.parseLong(info.getOrDefault("publishedVersionId", "-1").toString());
//        }

        GetDataSourceVersionsResult versionsResult;
        try {
            versionsResult = linkisDataSourceRemoteClient.getDataSourceVersions(
                    new GetDataSourceVersionsAction.Builder().setUser(userName).setResourceId(id + "").build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_GET_DATASOURCE_VERSION_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_GET_DATASOURCE_VERSION_ERROR.getCode(), e.getMessage());
            }
        }
        if (Objects.isNull(versionsResult)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_GET_DATASOURCE_VERSION_ERROR.getCode(), "datasource version response body null or empty");
        }

        if (versionsResult.getStatus() != 0) {
            throw new ExchangisDataSourceException(versionsResult.getStatus(), versionsResult.getMessage());
        }


        List<Map<String, Object>> versions = versionsResult.getVersions();

        if (!Objects.isNull(versions) && !Objects.isNull(publishedVersionId)) {
            for (Map<String, Object> version : versions) {
                Object versionId = version.get("versionId");
                if (Objects.isNull(versionId)) {
                    continue;
                }
                int vid = Integer.parseInt(versionId.toString());
                if (vid == publishedVersionId) {
                    version.put("published", true);
                }
            }
        }
        return Message.ok().data("versions", versions);
    }

    public Message testConnect(HttpServletRequest request, Long id, Long version) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        String responseBody;
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("testConnect userName:" + userName);
        DataSourceTestConnectResult result;
        try {
            result = linkisDataSourceRemoteClient.getDataSourceTestConnect(
                    new DataSourceTestConnectAction.Builder().setUser(userName).setDataSourceId(id + "").setVersion(version + "").build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_TEST_CONNECTION_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_TEST_CONNECTION_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(result)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_TEST_CONNECTION_ERROR.getCode(), "datasource test connection response body null or empty");
        }

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        return Message.ok();
    }

    public Message publishDataSource(HttpServletRequest request, Long id, Long version) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();

        String responseBody;
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("publishDataSource userName:" + userName);
        PublishDataSourceVersionResult result;
        try {
            result = linkisDataSourceRemoteClient.publishDataSourceVersion(
                    new PublishDataSourceVersionAction.Builder().setUser(userName).setDataSourceId(id + "").setVersion(version + "").build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_PUBLISH_VERSION_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_PUBLISH_VERSION_ERROR.getCode(), e.getMessage());
            }
        }
        if (Objects.isNull(result)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_PUBLISH_VERSION_ERROR.getCode(), "datasource publish version response body null or empty");
        }

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        return Message.ok();
    }

    public Message getDataSourceConnectParamsById(HttpServletRequest request, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        String responseBody;
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("getDataSourceConnectParamsById userName:" + userName);
        GetConnectParamsByDataSourceIdResult result;
        try {
            result = linkisDataSourceRemoteClient.getConnectParams(
                    GetConnectParamsByDataSourceIdAction.builder().setSystem("system").setUser(userName).setDataSourceId(id).build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_PARAMS_GET_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_PARAMS_GET_ERROR.getCode(), e.getMessage());
            }
        }
        if (Objects.isNull(result)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_PARAMS_GET_ERROR.getCode(), "datasource params get response body null or empty");
        }

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        return Message.ok().data("info", Objects.isNull(result.getConnectParams()) ? null : result.getConnectParams());
    }

    @Transactional
    public Message expireDataSource(HttpServletRequest request, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();

        String responseBody;
        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("getDataSourceConnectParamsById userName:" + userName);
//        ExpireDataSourceResult result;
        try {
//            result = linkisDataSourceRemoteClient.expireDataSource(
//                    new ExpireDataSourceAction.Builder().setUser(userName).setDataSourceId(id+"").build()
//            );

            Result execute = linkisDataSourceRemoteClient.execute(
                    new ExpireDataSourceAction.Builder().setUser(userName).setDataSourceId(id + "").build()
            );
            responseBody = execute.getResponseBody();
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_EXPIRE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_EXPIRE_ERROR.getCode(), e.getMessage());
            }
        }
//        if (Objects.isNull(result)) {
        if (Strings.isNullOrEmpty(responseBody)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_EXPIRE_ERROR.getCode(), "datasource expire response body null or empty");
        }

        ExpireDataSourceSuccessResultDTO result = Json.fromJson(responseBody, ExpireDataSourceSuccessResultDTO.class);

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        return Message.ok();
    }

    public Message getDataSourceKeyDefine(HttpServletRequest request, Long dataSourceTypeId) throws ErrorException {
        if (Objects.isNull(dataSourceTypeId)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "dataSourceType id should not be null");
        }
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();

        String userName = SecurityFilter.getLoginUsername(request);
        LOGGER.info("getDataSourceKeyDefine userName:" + userName);
        GetKeyTypeDatasourceResult result;
        try {
            GetKeyTypeDatasourceAction action = new GetKeyTypeDatasourceAction.Builder().setUser(userName).setDataSourceTypeId(dataSourceTypeId).build();
            result = linkisDataSourceRemoteClient.getKeyDefinitionsByType(action);
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_KEY_DEFINES_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_KEY_DEFINES_ERROR.getCode(), e.getMessage());
            }
        }

        if (Objects.isNull(result)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_KEY_DEFINES_ERROR.getCode(), "get datasource type key defines response body null or empty");
        }

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        return Message.ok().data("list", Objects.isNull(result.getKey_define()) ? null : result.getKey_define());
    }

    public Message queryDataSourceDBTableFieldsMapping(HttpServletRequest request, FieldMappingVO vo) throws Exception {
        Message message = Message.ok();

        Message sourceMessage = this.queryDataSourceDBTableFields(request, vo.getSourceTypeId(), vo.getSourceDataSourceId(), vo.getSourceDataBase(), vo.getSourceTable());
        List<DataSourceDbTableColumnDTO> sourceFields = (List<DataSourceDbTableColumnDTO>) sourceMessage.getData().get("columns");
        message.data("sourceFields", sourceFields);

        Message sinkMessage = this.queryDataSourceDBTableFields(request, vo.getSinkTypeId(), vo.getSinkDataSourceId(), vo.getSinkDataBase(), vo.getSinkTable());
        List<DataSourceDbTableColumnDTO> sinkFields = (List<DataSourceDbTableColumnDTO>) sinkMessage.getData().get("columns");
        message.data("sinkFields", sinkFields);


        // field mapping deduction
        List<Map<String, DataSourceDbTableColumnDTO>> deductions = new ArrayList<>();
        boolean[] matchedIndex = new boolean[sinkFields.size()];

        for (DataSourceDbTableColumnDTO sourceField : sourceFields) {
            String sourceName = sourceField.getName().replaceAll("[-_]", "").toLowerCase().trim();

            for (int i = 0; i < sinkFields.size(); i++) {
                if (matchedIndex[i]) {
                    continue;
                }
                DataSourceDbTableColumnDTO sinkField = sinkFields.get(i);
                String sinkName = sinkField.getName().replaceAll("[-_]", "").toLowerCase().trim();
                if (sourceName.equals(sinkName)) {
                    Map<String, DataSourceDbTableColumnDTO> deduction = new HashMap<>();
                    deduction.put("source", sourceField);
                    deduction.put("sink", sinkField);
                    deductions.add(deduction);
                    matchedIndex[i] = true;
                    break;
                }
            }


        }

        message.data("deductions", deductions);

        return message;
    }

}
