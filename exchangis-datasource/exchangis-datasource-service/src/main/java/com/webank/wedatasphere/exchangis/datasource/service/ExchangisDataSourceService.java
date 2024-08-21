package com.webank.wedatasphere.exchangis.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.common.pager.PageResult;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobDsBind;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobDsBindMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.GetDataSourceInfoByIdAndVersionIdAction;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceDetail;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceItem;
import com.webank.wedatasphere.exchangis.datasource.domain.ExchangisDataSourceTypeDefinition;
import com.webank.wedatasphere.exchangis.datasource.utils.RSAUtil;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.remote.*;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.request.ParamsTestConnectAction;
import com.webank.wedatasphere.exchangis.datasource.linkis.response.ParamsTestConnectResult;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVo;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVo;
import com.webank.wedatasphere.exchangis.engine.dao.EngineSettingsDao;
import com.webank.wedatasphere.exchangis.engine.domain.EngineSettings;
import com.webank.wedatasphere.exchangis.job.api.ExchangisJobOpenService;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.utils.JobUtils;
import com.webank.wedatasphere.exchangis.project.entity.domain.OperationType;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProject;
import com.webank.wedatasphere.exchangis.project.entity.entity.ExchangisProjectDsRelation;
import com.webank.wedatasphere.exchangis.project.entity.vo.ExchangisProjectInfo;
import com.webank.wedatasphere.exchangis.project.entity.vo.ProjectDsQueryVo;
import com.webank.wedatasphere.exchangis.project.provider.service.ProjectOpenService;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.datasource.client.AbstractRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import org.apache.linkis.datasource.client.request.*;
import org.apache.linkis.datasource.client.response.*;
import org.apache.linkis.datasource.client.response.DataSourceTestConnectResult;
import org.apache.linkis.datasource.client.response.GetDataSourceVersionsResult;
import org.apache.linkis.datasource.client.response.MetadataGetColumnsResult;
import org.apache.linkis.datasourcemanager.common.domain.DataSource;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceType;
import org.apache.linkis.datasourcemanager.common.exception.JsonErrorException;
import org.apache.linkis.datasourcemanager.common.util.json.Json;
import org.apache.linkis.httpclient.dws.response.DWSResult;
import org.apache.linkis.httpclient.request.Action;
import org.apache.linkis.httpclient.response.Result;
import org.apache.linkis.metadata.query.common.domain.MetaColumnInfo;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode.*;

@Service
public class ExchangisDataSourceService extends AbstractDataSourceService
        implements DataSourceUIGetter, DataSourceService{

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceService.class);

    /**
     * Engine settings
     */
    private final EngineSettingsDao settingsDao;

    /**
     * Open service
     */
    @Resource
    private ExchangisJobOpenService jobOpenService;

    /**
     * Project open service
     */
    @Resource
    private ProjectOpenService projectOpenService;

    /**
     * Job and data source
     */
    @Autowired
    private ExchangisJobDsBindMapper exchangisJobDsBindMapper;

    @Autowired
    public ExchangisDataSourceService(ExchangisDataSourceContext context,
                                      ExchangisJobParamConfigMapper exchangisJobParamConfigMapper, EngineSettingsDao settingsDao) {
        super(context, exchangisJobParamConfigMapper);
        this.settingsDao = settingsDao;
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public List<ExchangisDataSourceUIViewer> getJobDataSourceUIs(HttpServletRequest request, Long jobId) {
        if (Objects.isNull(jobId)) {
            return null;
        }
        ExchangisJobEntity job;
        try {
            job = this.jobOpenService.getJobById(jobId, false);
        } catch (ExchangisJobException e) {
            throw new ExchangisDataSourceException
                    .Runtime(CONTEXT_GET_DATASOURCE_NULL.getCode(), "Fail to get job entity (获得任务信息失败)", e);
        }
        if (Objects.isNull(job)) {
            return null;
        }

        List<ExchangisJobInfoContent> jobInfoContents = JobUtils.parseJobContent(job.getJobContent());
        List<ExchangisDataSourceUIViewer> uis = new ArrayList<>();
        for (ExchangisJobInfoContent cnt : jobInfoContents) {
            cnt.setEngine(job.getEngineType());
            ExchangisDataSourceUIViewer viewer = buildAllUI(request, job, cnt);
            uis.add(viewer);
        }

        return uis;
    }

    /**
     * Generate data source ui
     */
    @Override
    public List<ElementUI<?>> getDataSourceParamsUI(String dsType, String engineAndDirection) {

        com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition exchangisDataSource = this.context.getExchangisDsDefinition(dsType);
        List<ExchangisJobParamConfig> paramConfigs = exchangisDataSource.getDataSourceParamConfigs();
        List<ExchangisJobParamConfig> filteredConfigs = new ArrayList<>();
        String[] engineDirect = engineAndDirection.split("-");
        String direction = engineDirect[1];
        for (ExchangisJobParamConfig paramConfig : paramConfigs) {
            //skip the
            Optional.ofNullable(paramConfig.getConfigDirection()).ifPresent(configDirection -> {
                if (configDirection.equalsIgnoreCase(engineAndDirection) || configDirection.equalsIgnoreCase(direction)){
                    filteredConfigs.add(paramConfig);
                }
            });
        }
        return this.buildDataSourceParamsUI(filteredConfigs);
    }

    @Override
    public List<ElementUI<?>> getJobEngineSettingsUI(String engineType) {
        return this.buildJobSettingsUI(engineType);
    }

    /**
     * 根据 LocalExchangisDataSourceLoader 加载到的本地的数据源与 Linkis 支持的数据源
     * 做比较，筛选出可以给前端展示的数据源类型
     */
    @Override
    public List<ExchangisDataSourceTypeDefinition> listDataSourceTypes(String operator,
                                                                       String engineType, String direct, String sourceType)
            throws ExchangisDataSourceException{
        List<ExchangisDataSourceTypeDefinition> typeDefinitions = new ArrayList<>();
        // Load engine settings
        List<EngineSettings> settingsList = this.settingsDao.getSettings();
        List<EngineSettings> engineSettings = new ArrayList<>();
        if (StringUtils.isEmpty(engineType)) {
            engineSettings = settingsList;
        } else {
            EngineSettings engineSetting = new EngineSettings();
            for (EngineSettings settings : settingsList) {
                if (StringUtils.equals(settings.getName(), engineType.toLowerCase())) {
                    engineSetting = settings;
                    break;
                }
            }
            engineSettings.add(engineSetting);
        }

        Set<String> directType = new HashSet<>();
        for (EngineSettings engineSetting: engineSettings) {
            for (int i = 0; i < engineSetting.getDirectionRules().size(); i++) {
                engineSetting.getDirectionRules().forEach(item -> {
                    String source = item.getSource();
                    String sink = item.getSink();
                    if (StringUtils.isEmpty(direct)) {
                        directType.add(source);
                        directType.add(sink);
                    } else if (StringUtils.equals(direct, "source")) {
                        directType.add(source);
                    } else {
                        // Sink types filter
                        if ((StringUtils.isBlank(sourceType) ||
                                (StringUtils.isNoneBlank(sourceType) && StringUtils.equals(source, sourceType.toLowerCase())))) {
                            directType.add(sink);
                        }
                    }
                });
            }
        }
        // Send to get data source types
        LinkisDataSourceRemoteClient client = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        GetAllDataSourceTypesResult result = rpcSend(client, () -> GetAllDataSourceTypesAction.builder()
                .setUser(operator)
                .build(),
                LinkisDataSourceRemoteClient::getAllDataSourceTypes,CLIENT_DATASOURCE_GET_TYPES_ERROR.getCode(),
                "datasource get types null or empty");
        List<DataSourceType> dataSourceTypes = result.getAllDataSourceType();
        for (DataSourceType type : dataSourceTypes) {
            String typeName = type.getName();
            if (directType.contains(typeName)) {
                ExchangisDataSourceDefinition definition = this.context.getExchangisDsDefinition(typeName);
                ExchangisDataSourceTypeDefinition typeDef = new ExchangisDataSourceTypeDefinition(
                        type.getId(),
                        type.getClassifier(),
                        definition.name(),
                        definition.structClassifier()
                );
                // use linkis datasource table field to fill the definition bean
                typeDef.setIcon(type.getIcon());
                typeDef.setDescription(type.getDescription());
                typeDef.setOption(type.getOption());
                typeDefinitions.add(typeDef);
            }
        }
        return typeDefinitions;
    }

    /**
     * Create data source
     * @param operator operator operatorr
     * @param vo create vo
     * @return data source id
     * @throws ExchangisDataSourceException e
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> create(String operator, DataSourceCreateVo vo) throws ExchangisDataSourceException {
        // TODO merge parameter from data source model
        Map<String, Object> payLoads;
        try {
            payLoads = Json.fromJson(Json.toJson(vo, null), Map.class);
            payLoads.put("labels", payLoads.get("label"));
        } catch (JsonErrorException e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARSE_JSON_ERROR.getCode(),
                    e.getMessage());
        }
        ExchangisDataSourceDefinition dsType = context.getExchangisDsDefinition(vo.getDataSourceTypeId());
        if (Objects.isNull(dsType)) {
            throw new ExchangisDataSourceException(CONTEXT_GET_DATASOURCE_NULL.getCode(), "exchangis context get datasource null");
        }
        LinkisDataSourceRemoteClient client = dsType.getDataSourceRemoteClient();
        // Send to create data source
        CreateDataSourceResult createResult = rpcSend(client, () -> CreateDataSourceAction.builder()
                .setUser(operator)
                .addRequestPayloads(payLoads)
                .build(),
                LinkisDataSourceRemoteClient::createDataSource, CLIENT_DATASOURCE_CREATE_ERROR.getCode(),
                "datasource create response null or empty");
        // Get data source id
        Long dataSourceId = createResult.getInsertId();
        // Send to create version
        UpdateDataSourceParameterResult versionResult = rpcSend(client, () -> UpdateDataSourceParameterAction.builder()
                .setUser(operator)
                .setDataSourceId(Long.parseLong(dataSourceId + ""))
                .addRequestPayloads(payLoads)
                .build(),
                LinkisDataSourceRemoteClient::updateDataSourceParameter, CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(),
                "datasource update params version null or empty");
        // TODO build the relation between model and data source version
        Map<String, Object> versionParams = versionResult.getData();
        versionParams.put("id", dataSourceId);
        return versionParams;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> update(String operator, Long id, DataSourceCreateVo vo) throws ExchangisDataSourceException {
        Map<String, Object> payLoads;
        try {
            payLoads = Json.fromJson(Json.toJson(vo, null), Map.class);
            payLoads.put("labels", payLoads.get("label"));
        } catch (JsonErrorException e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARSE_JSON_ERROR.getCode(),
                    e.getMessage());
        }
        ExchangisDataSourceDefinition dsType = context.getExchangisDsDefinition(vo.getDataSourceTypeId());
        if (Objects.isNull(dsType)) {
            throw new ExchangisDataSourceException(CONTEXT_GET_DATASOURCE_NULL.getCode(), "exchangis context get datasource null");
        }
        LinkisDataSourceRemoteClient client = dsType.getDataSourceRemoteClient();
        // TODO First to get data source data
        // Send to update data source
        rpcSend(client, () -> UpdateDataSourceAction.builder()
                .setUser(operator)
                .setDataSourceId(Long.parseLong(id + ""))
                .addRequestPayloads(payLoads)
                .build(),
                AbstractRemoteClient::execute, CLIENT_DATASOURCE_UPDATE_ERROR.getCode(),
                "datasource update null or empty");
        // Send to create version
        rpcSend(client, () -> UpdateDataSourceParameterAction.builder()
                .setDataSourceId(Long.parseLong(id + ""))
                .setUser(operator)
                .addRequestPayloads(payLoads)
                .build(),
                LinkisDataSourceRemoteClient :: updateDataSourceParameter,
                CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(),
                "datasource update params version null or empty");
        // TODO build the relation between model and data source version
        return new HashMap<>();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long delete(String operator,  Long id) throws ExchangisDataSourceException {
        QueryWrapper<ExchangisJobDsBind> condition = new QueryWrapper<>();
        condition.eq("source_ds_id", id).or().eq("sink_ds_id", id);
        Long inUseCount = this.exchangisJobDsBindMapper.selectCount(condition);
        if (inUseCount > 0) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_DELETE_ERROR.getCode(), "目前存在引用依赖");
        }
        // TODO delete the relation between model and data source version
        LinkisDataSourceRemoteClient client = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        DeleteDataSourceResult result = rpcSend(client, () -> DeleteDataSourceAction.builder()
                .setUser(operator).setDataSourceId(Long.parseLong(id + "")).builder(),
                LinkisDataSourceRemoteClient::deleteDataSource, CLIENT_DATASOURCE_DELETE_ERROR.getCode(),
                "datasource delete null or empty");
        return result.getRemoveId();
    }

    /**
     * Query database from data source
     * @param operator username
     * @param type type
     * @param id id
     * @return message
     * @throws ExchangisDataSourceException e
     */
    @Override
    public List<String> getDatabases(String operator, String type, Long id) throws ExchangisDataSourceException {
        ExchangisDataSourceDefinition definition = context.getExchangisDsDefinition(type);
        MetadataGetDatabasesResult result = rpcSend(definition.getMetaDataRemoteClient(), () -> MetadataGetDatabasesAction.builder()
                .setSystem("exchangis")
                .setSystem(type)
                .setDataSourceId(id)
                .setUser(operator)
                .build(),
                LinkisMetaDataRemoteClient::getDatabases, CLIENT_METADATA_GET_DATABASES_ERROR.getCode(),
                "metadata get databases null or empty");
        return Optional.ofNullable(result.getDbs()).orElse(new ArrayList<>());
    }

    /**
     * Query table in database from data source
     * @param  operator operator
     * @param type type
     * @param id id
     * @param database database name
     * @return message
     * @throws ExchangisDataSourceException e
     */
    @Override
    public List<String> getTables(String operator, String type, Long id, String database) throws ExchangisDataSourceException {
        ExchangisDataSourceDefinition definition = context.getExchangisDsDefinition(type);
        MetadataGetTablesResult tablesResult = rpcSend(definition.getMetaDataRemoteClient(), () -> MetadataGetTablesAction.builder()
                .setSystem(type)
                .setDataSourceId(id)
                .setDatabase(database)
                .setUser(operator)
                .build(),
                LinkisMetaDataRemoteClient::getTables, CLIENT_METADATA_GET_TABLES_ERROR.getCode(),
                "metadata get tables null or empty");
        return Optional.ofNullable(tablesResult.getTables()).orElse(new ArrayList<>());
    }

    /**
     * Query table fields (columns)
     * @param operator username
     * @param type type
     * @param id id
     * @param database database name
     * @param table table name
     * @return message
     * @throws ExchangisDataSourceException e
     */
    @Override
    public List<DataSourceDbTableColumn> getTableFields(String operator,
                                                        String type, Long id, String database, String table) throws ExchangisDataSourceException {
        ExchangisDataSourceDefinition definition = context.getExchangisDsDefinition(type);
        MetadataGetColumnsResult columnsResult = rpcSend(definition.getMetaDataRemoteClient(), () -> MetadataGetColumnsAction.builder()
                .setSystem(type)
                .setDataSourceId(id)
                .setDatabase(database)
                .setTable(table)
                .setUser(operator)
                .build(),
                LinkisMetaDataRemoteClient::getColumns, CLIENT_METADATA_GET_COLUMNS_ERROR.getCode(),
                "metadata get columns null or empty");
        List<MetaColumnInfo> columns = columnsResult.getAllColumns();
        return Optional.ofNullable(columns).orElse(new ArrayList<>()).stream().map(column -> {
            DataSourceDbTableColumn item = new DataSourceDbTableColumn();
            item.setName(column.getName());
            item.setType(column.getType());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * Query data sources
     * @param operator operator
     * @param vo vo object
     * @return response message
     * @throws Exception e
     */
    @Override
    public PageResult<ExchangisDataSourceItem> queryDataSources(String operator,
                                                                DataSourceQueryVo vo) throws ExchangisDataSourceException {
        String username = StringUtils.isNoneBlank(vo.getCreateUser()) ?
                vo.getCreateUser() : operator;
        int page = Objects.isNull(vo.getPage()) ? 1 : vo.getPage();
        int pageSize = Objects.isNull(vo.getPageSize()) ? 100 : vo.getPageSize();
        String dataSourceName = Objects.isNull(vo.getName()) ? "" : vo.getName().replace("_", "\\_");
        Long typeId = vo.getTypeId();
        int total = 0;
        // If to fetch from remote server
        boolean toRemote = true;
        Map<String, ExchangisDataSourceItem> dsQueryMap = new LinkedHashMap<>();
        Long refProjectId = vo.getProjectId();
        if (Objects.nonNull(refProjectId)){
            // Try to get data sources from project relation
            ExchangisProjectInfo project = projectOpenService.getProject(refProjectId);
            if (Objects.nonNull(project)){
                toRemote = !ExchangisProject.Domain.DSS.name()
                        .equalsIgnoreCase(project.getDomain());
                if (projectOpenService.hasAuthority(username, project, OperationType.PROJECT_QUERY)){
                    // Build project data source query
                    ProjectDsQueryVo dsQueryVo = new ProjectDsQueryVo();
                    dsQueryVo.setProjectId(refProjectId);
                    dsQueryVo.setName(dataSourceName);
                    if (Objects.nonNull(typeId)){
                        // Try to find the type string
                        com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition definition =
                                this.context.getExchangisDsDefinition(typeId);
                        if (Objects.nonNull(definition)){
                            dsQueryVo.setType(definition.name());
                        }
                    }
                    PageResult<ExchangisProjectDsRelation> dsRelations = projectOpenService.queryDsRelation(dsQueryVo);
                    total += dsRelations.getTotal();
                    Optional.ofNullable(toExchangisDataSourceItems(refProjectId, dsRelations.getList()))
                            .ifPresent(list -> {
                                list.forEach(item -> {
                                    if (!dsQueryMap.containsKey(item.getName())) {
                                        dsQueryMap.put(item.getName(), item);
                                    }
                                });
                            });
                }
            }
        }
        if (toRemote) {
            // Recalculate the page number and page size
            if (dsQueryMap.size() >= pageSize){
                page = 1;
            } else if (total > 0){
                int totalPages = (total + pageSize - 1) / pageSize;
                if (page <= totalPages){
                    page = 1;
                } else {
                    // TODO has a problem that produces duplicate page data
                    page = page - totalPages;
                }
            }
            // Send to get data sources from linkis
            int finalPage = page;
            QueryDataSourceResult result = rpcSend(ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient(), () -> {
                QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                        .setSystem("exchangis")
                        .setName(dataSourceName)
                        .setIdentifies("")
                        .setCurrentPage(finalPage)
                        .setUser(username)
                        .setPageSize(pageSize);
                if (!Objects.isNull(typeId)) {
                    builder.setTypeId(typeId);
                }
                if (!Strings.isNullOrEmpty(vo.getTypeName())) {
                    builder.setSystem(vo.getTypeName());
                }
                return builder.build();
            }, LinkisDataSourceRemoteClient::queryDataSource, CLIENT_QUERY_DATASOURCE_ERROR.getCode(),
                    "");
            total += result.getTotalPage();
            List<DataSource> dataSources = result.getAllDataSource();
            int addSize = Math.min(pageSize - dsQueryMap.size(), dataSources.size());
            if (addSize > 0){
                Optional.ofNullable(toExchangisDataSourceItems(dataSources.subList(0, addSize)))
                        .ifPresent(list -> {
                            for(int i = 0; i < addSize; i++) {
                                ExchangisDataSourceItem item = list.get(i);
                                if (!dsQueryMap.containsKey(item.getName())) {
                                    dsQueryMap.put(item.getName(), item);
                                }
                            }
                        });
            }
        }
        PageResult<ExchangisDataSourceItem> pageResult = new PageResult<>();
        pageResult.setList(new ArrayList<>(dsQueryMap.values()));
        pageResult.setTotal((long) total);
        return pageResult;
    }

    /**
     * List data sources
     * @param operator operator
     * @param typeName type name
     * @param typeId type id
     * @param page page num
     * @param pageSize page size
     * @return data sources
     * @throws ExchangisDataSourceException
     */
    public List<ExchangisDataSourceItem> listDataSources(String operator,
                                                         String typeName, Long typeId, Integer page, Integer pageSize) throws ExchangisDataSourceException {
        QueryDataSourceResult result = rpcSend(ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient(), () -> {
            QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                    .setSystem("exchangis")
                    .setIdentifies("")
                    .setUser(operator);
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
            return builder.build();
        }, LinkisDataSourceRemoteClient::queryDataSource, CLIENT_QUERY_DATASOURCE_ERROR.getCode(),
                "");
        List<ExchangisDataSourceItem> dataSources = new ArrayList<>();
        if (!Objects.isNull(result.getAllDataSource())) {
            dataSources = toExchangisDataSourceItems(result.getAllDataSource());
        }
        return dataSources;
    }

    @Override
    public ExchangisDataSourceDetail getDataSource(String operator, Long id, String versionId) {
        return null;
    }

    @Deprecated
    public Message getDataSource(HttpServletRequest request, Long id, String versionId) throws ErrorException {
        String userName = UserUtils.getLoginUser(request);
        GetDataSourceInfoResult result;
        if (Strings.isNullOrEmpty(versionId)) {
            result = getDataSource(userName, id);
        } else {
            result = getDataSourceByIdAndVersionId(userName, id, versionId);
        }
        return Message.ok().data("info", result.getData().getInfo());
    }

    public GetDataSourceInfoResult getDataSourceByIdAndVersionId(String userName, Long id, String versionId) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        try {
            Result execute = linkisDataSourceRemoteClient.execute(
                    GetDataSourceInfoByIdAndVersionIdAction.builder().setSystem("exchangis").setUser(userName).setDataSourceId(id).setVersionId(versionId).build()
            );
            String responseBody = execute.getResponseBody();
            GetDataSourceInfoResult result = Json.fromJson(responseBody, GetDataSourceInfoResult.class);
            if (result.getStatus() != 0) {
                throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
            }
            return result;
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
            }
        }

    }

    /**
     * Get data source by name
     * @param userName username
     * @param dsName data source name
     * @return dto
     * @throws ErrorException e
     */
    public GetDataSourceInfoResult getDataSource(String userName, String dsName) throws ErrorException{
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        try {
            Result execute = linkisDataSourceRemoteClient
                    .execute(GetInfoByDataSourceNameAction.builder()
                            .setSystem("exchangis").setUser(userName).setDataSourceName(dsName)
                            .build());
            String responseBody = execute.getResponseBody();
            GetDataSourceInfoResult result = Json.fromJson(responseBody, GetDataSourceInfoResult.class);
            if (result.getStatus() != 0) {
                throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
            }
            return result;
        } catch (Exception e){
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
            }
        }
    }

    @Override
    public GetDataSourceInfoResult getDataSource(String userName, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        try {
            Result execute = linkisDataSourceRemoteClient.execute(
                    GetInfoByDataSourceIdAction.builder().setSystem("exchangis").setUser(userName).setDataSourceId(id).build()
            );
            String responseBody = execute.getResponseBody();
            GetDataSourceInfoResult result = Json.fromJson(responseBody, GetDataSourceInfoResult.class);
            if (result.getStatus() != 0) {
                throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
            }
            return result;
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
        String userName = UserUtils.getLoginUser(request);
        GetDataSourceInfoResult result;
        try {
            Result execute = linkisDataSourceRemoteClient.execute(
                    GetInfoByDataSourceIdAction.builder().setSystem("exchangis").setUser(userName).setDataSourceId(id).build()
            );
            String responseBody = execute.getResponseBody();
            result = Json.fromJson(responseBody, GetDataSourceInfoResult.class);
            if (result.getStatus() != 0) {
                throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
            }
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(CLIENT_GET_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(CLIENT_GET_DATASOURCE_ERROR.getCode(), e.getMessage());
            }
        }

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        GetDataSourceInfoResult.DataSourceDetail info = result.getData().getInfo();
        Integer publishedVersionId = info.getPublishedVersionId();
        GetDataSourceVersionsResult versionsResult;
        try {
            versionsResult = linkisDataSourceRemoteClient.getDataSourceVersions(
                    new GetDataSourceVersionsAction.Builder().setUser(userName).setDataSourceId(Long.parseLong(id + "")).build()
            );
        } catch (Exception e) {
            if (e instanceof ErrorException) {
                ErrorException ee = (ErrorException) e;
                throw new ExchangisDataSourceException(CLIENT_GET_DATASOURCE_VERSION_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
            } else {
                throw new ExchangisDataSourceException(CLIENT_GET_DATASOURCE_VERSION_ERROR.getCode(), e.getMessage());
            }
        }
        if (Objects.isNull(versionsResult)) {
            throw new ExchangisDataSourceException(CLIENT_GET_DATASOURCE_VERSION_ERROR.getCode(), "datasource version response body null or empty");
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

        versions.sort((o1, o2) -> {
            Object vid1 = o1.get("versionId");
            Object vid2 = o2.get("versionId");
            int a1 = 0, a2 = 0;
            if (Objects.nonNull(vid1)) {
                a1 = Integer.parseInt(vid1.toString());
            }
            if (Objects.nonNull(vid2)) {
                a2 = Integer.parseInt(vid2.toString());
            }
            return a2 - a1;
        });
        return Message.ok().data("versions", versions);
    }

    public Message testConnect(HttpServletRequest request, Long id, Long version) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        String userName = UserUtils.getLoginUser(request);
        DataSourceTestConnectResult result;
        try {
            result = linkisDataSourceRemoteClient.getDataSourceTestConnect(
                    new DataSourceTestConnectAction.Builder().setUser(userName).setDataSourceId(Long.parseLong(id + "")).setVersion(version + "").build()
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

    @SuppressWarnings("unchecked")
    public Message testConnectByVo(HttpServletRequest request, DataSourceCreateVo vo) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        String userName = UserUtils.getLoginUser(request);
        Map<String, Object> json;
        try {
            json = mapper.readValue(mapper.writeValueAsString(vo), Map.class);
            json.put("labels",json.get("label"));
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARSE_JSON_ERROR.getCode(), e.getMessage());
        }
        ParamsTestConnectResult result;
        try {
            result = (ParamsTestConnectResult) linkisDataSourceRemoteClient.execute(
                    new ParamsTestConnectAction(json, userName)
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

    /**
     * Publish data source
     * @param username username
     * @param id data source id
     * @param version version
     * @return message
     * @throws ErrorException e
     */
    public Message publishDataSource(String username, Long id, Long version) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        PublishDataSourceVersionResult result;
        try {
            result = linkisDataSourceRemoteClient.publishDataSourceVersion(
                    new PublishDataSourceVersionAction.Builder().setUser(username).setDataSourceId(Long.parseLong(id + "")).setVersion(Long.parseLong(version + "")).build()
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
        String userName = UserUtils.getLoginUser(request);
        GetConnectParamsByDataSourceIdResult result =  getDataSourceConnectParamsById(userName, id);
        return Message.ok().data("info", Objects.isNull(result.getConnectParams()) ? null : result.getConnectParams());
    }

    @Transactional
    public Message expireDataSource(HttpServletRequest request, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();

        String responseBody;
        String userName = UserUtils.getLoginUser(request);
        try {
            Result execute = linkisDataSourceRemoteClient.execute(
                    new ExpireDataSourceAction.Builder().setUser(userName).setDataSourceId(Long.parseLong(id + "")).build()
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

        ExpireDataSourceSuccessResult result = Json.fromJson(responseBody, ExpireDataSourceSuccessResult.class);

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }

        return Message.ok();
    }

    public GetConnectParamsByDataSourceIdResult getDataSourceConnectParamsById(String userName, Long id) throws ErrorException{
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        GetConnectParamsByDataSourceIdResult result;
        try {
            result = linkisDataSourceRemoteClient.getConnectParams(
                    GetConnectParamsByDataSourceIdAction.builder().setSystem("exchangis").setUser(userName).setDataSourceId(id).build()
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
        return result;
    }
    public Message getDataSourceKeyDefine(HttpServletRequest request, Long dataSourceTypeId) throws ErrorException {
        if (Objects.isNull(dataSourceTypeId)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "dataSourceType id should not be null");
        }
        Message message = Message.ok();
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();

        String userName = UserUtils.getLoginUser(request);
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

        message.data("list", Objects.isNull(result.getKeyDefine()) ? null : result.getKeyDefine());
        return message;
    }

    public void checkDSSupportDegree(String engine, String sourceDsType, String sinkDsType) throws ExchangisDataSourceException {
        switch (engine) {
            case "SQOOP":
                this.checkSqoopDSSupportDegree(sourceDsType, sinkDsType);
                break;
            case "DATAX":
                break;
            default:
                throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.UNSUPPORTEd_ENGINE.getCode(), "不支持的引擎");
        }
    }

    private void checkSqoopDSSupportDegree(String sourceDsType, String sinkDsType) throws ExchangisDataSourceException {
        if (!("HIVE".equals(sourceDsType) || "HIVE".equals(sinkDsType))) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.DS_MAPPING_MUST_CONTAIN_HIVE.getCode(), "SQOOP引擎输入/输出数据源必须包含HIVE");
        }
        if (sourceDsType.equals(sinkDsType)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.DS_TYPE_MUST_DIFFERENT.getCode(), "SQOOP引擎读写类型不可相同");
        }
    }

    public Message encryptConnectInfo(String encryStr) throws Exception {
        if (Objects.isNull(encryStr)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "dataSourceType connect parameter show not be null");
        }
        String publicKeyStr = RSAUtil.PUBLIC_KEY_STR.getValue();
        PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
        //用公钥加密
        byte[] publicEncrypt = RSAUtil.publicEncrypt(encryStr.getBytes(), publicKey);
        //加密后的内容Base64编码
        String byte2Base64 = RSAUtil.byte2Base64(publicEncrypt);
        Message message = new Message();
        message.data("encryStr", byte2Base64);
        return message;
    }

    public Message decryptConnectInfo(String sinkStr) throws Exception {
        if (Objects.isNull(sinkStr)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "dataSourceType connect parameter show not be null");
        }

        String privateKeyStr = RSAUtil.PRIVATE_KEY_STR.getValue();
        PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
        //加密后的内容Base64解码
        byte[] base642Byte = RSAUtil.base642Byte(sinkStr);
        //用私钥解密
        byte[] privateDecrypt = RSAUtil.privateDecrypt(base642Byte, privateKey);
        String decryptStr = new String(privateDecrypt);
        Message message = new Message();
        message.data("decryptStr", decryptStr);
        return message;
    }

    @Override
    public void copyDataSource(String operator,
                               String sourceName, String newName) throws ErrorException{
        // Try to get data source with new name
        GetDataSourceInfoResult dsResult;
        try {
            dsResult = getDataSource(operator, newName);
            if (Objects.nonNull(dsResult)){
                // data source already exists
                return;
            }
        } catch (Exception e){
            // Ignore the exception
        }
        // Use admin user to get model data source
        String admin = GlobalConfiguration.getAdminUser();
        dsResult = getDataSource(StringUtils.isNotBlank(admin) ? admin : operator, sourceName);
        if (Objects.nonNull(dsResult)){
            DataSourceCreateVo createDs = new DataSourceCreateVo();
            GetDataSourceInfoResult.DataSourceDetail modelDs = dsResult.getData().getInfo();
            createDs.setDataSourceName(newName);
            createDs.setDataSourceTypeId(modelDs.getDataSourceTypeId());
            createDs.setDataSourceDesc(modelDs.getDataSourceDesc());
            createDs.setCreateSystem(modelDs.getCreateSystem());
            createDs.setCreateUser(operator);
            createDs.setLabel(modelDs.getLabel());
            createDs.setConnectParams(modelDs.getConnectParams());
            createDs.setComment("init");
            Map<String, Object> versionParams = create(operator, createDs);
            Object version = versionParams.get("version");
            Object id = versionParams.get("id");
            if (Objects.nonNull(version) && Objects.nonNull(id)){
                publishDataSource(operator,
                        Long.parseLong(String.valueOf(id)), Long.parseLong(String.valueOf(version)));
            }
        }
    }

    @Override
    public void recycleDataSource(String userName, String handover) throws ExchangisDataSourceException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                .setSystem("Exchangis")
                .setIdentifies("")
                .setUser(userName);
        List<DataSource> allDataSource = null;
        try {
            QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(builder.build());
            allDataSource = result.getAllDataSource();
            if (Objects.nonNull(allDataSource)) {
                for (int i = 0; i < allDataSource.size(); i++) {
                    DataSource dataSource = allDataSource.get(i);
                    Map<String, Object> json;
                    try {
                        dataSource.setCreateUser(handover);
                        json = mapper.readValue(mapper.writeValueAsString(dataSource), Map.class);
                        json.put("labels",json.get("label"));
                    } catch (JsonProcessingException e) {
                        throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARSE_JSON_ERROR.getCode(), e.getMessage());
                    }
                    json = null;
                    String user = GlobalConfiguration.getAdminUser();
                    com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition dsType = context.getExchangisDsDefinition(dataSource.getDataSourceTypeId());

                    LinkisDataSourceRemoteClient client = dsType.getDataSourceRemoteClient();
                    String responseBody;
                    Result execute = client.execute(UpdateDataSourceAction.builder()
                            .setUser(user)
                            .setDataSourceId(dataSource.getId())
                            .addRequestPayloads(json)
                            .build()
                    );
                    responseBody = execute.getResponseBody();
                    if (Strings.isNullOrEmpty(responseBody)) {
                        throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_ERROR.getCode(), "datasource update null or empty");
                    }

                    UpdateDataSourceSuccessResult updateDataSourceResult = Json.fromJson(responseBody, UpdateDataSourceSuccessResult.class);

                    if (updateDataSourceResult.getStatus() != 0) {
                        throw new ExchangisDataSourceException(updateDataSourceResult.getStatus(), updateDataSourceResult.getMessage());
                    }
                }
            }
        } catch (ExchangisDataSourceException e) {
            LOG.error("Failed to recycle datasource, cause by : " + e);
        } catch (Exception e){
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
        }
    }

    /**
     * Rpc send wrapper
     * @param client rpc client
     * @param decorator decorator for action
     * @param executor executor for client
     * @param <T> action type
     * @param <R> result type
     * @return result
     * @throws ExchangisDataSourceException e
     */
    private <T extends Action, R extends Result, C extends AbstractRemoteClient>R rpcSend(
            C client,
            Supplier<T> decorator, BiFunction<C, T, R> executor,
            int errorCode, String nonErrorMessage) throws ExchangisDataSourceException{
        T action = decorator.get();
        R result;
        try {
            result = executor.apply(client, action);
        } catch (Exception e){
            throw new ExchangisDataSourceException(errorCode, e.getMessage());
        }
        if (Objects.isNull(result) || StringUtils.isBlank(result.getResponseBody())){
            throw new ExchangisDataSourceException(errorCode, nonErrorMessage);
        }
        if (result instanceof DWSResult) {
            DWSResult dwsResult = (DWSResult) result;
            if (dwsResult.getStatus() != 0){
                throw new ExchangisDataSourceException(dwsResult.getStatus(), dwsResult.getMessage());
            }
        }
        return result;
    }
    /**
     * Convert project data source relations to exchangis data sources
     * @param dsRelations relation
     * @return list
     */
    private List<ExchangisDataSourceItem> toExchangisDataSourceItems(Long projectId, List<ExchangisProjectDsRelation> dsRelations){
        return dsRelations.stream().map(ds -> {
            ExchangisDataSourceItem item = new ExchangisDataSourceItem();
            item.setId(ds.getDsId());
            item.setName(ds.getDsName());
            item.setType(ds.getDsType());
            item.setCreateUser(ds.getDsCreator());
            return item;
        }).collect(Collectors.toList());
    }
    /**
     * Convert linkis data sources to exchangis data sources
     * @param dataSources  linkis data sources
     * @return list
     */
    private List<ExchangisDataSourceItem> toExchangisDataSourceItems(List<DataSource> dataSources){
        return dataSources.stream().map(ds -> {
            ExchangisDataSourceItem item = new ExchangisDataSourceItem();
            item.setId(ds.getId());
            item.setCreateIdentify(ds.getCreateIdentify());
            item.setName(ds.getDataSourceName());
            Optional.ofNullable(ds.getDataSourceType()).ifPresent(type -> {
                item.setType(type.getName());
            });
            item.setCreateSystem(ds.getCreateSystem());
            item.setDataSourceTypeId(ds.getDataSourceTypeId());
            item.setLabels(ds.getLabels());
            item.setLabel(ds.getLabels());
            item.setDesc(ds.getDataSourceDesc());
            item.setCreateUser(ds.getCreateUser());
            item.setModifyUser(ds.getModifyUser());
            item.setModifyTime(ds.getModifyTime());
            item.setVersionId(ds.getVersionId());
            item.setExpire(ds.isExpire());
            return item;
        }).collect(Collectors.toList());
    }


}
