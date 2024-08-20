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
import com.webank.wedatasphere.exchangis.datasource.utils.RSAUtil;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ElementUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.ExchangisDataSourceParamsUI;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobTransformsContent;
import com.webank.wedatasphere.exchangis.datasource.remote.*;
import com.webank.wedatasphere.exchangis.datasource.linkis.ExchangisLinkisRemoteClient;
import com.webank.wedatasphere.exchangis.datasource.linkis.request.ParamsTestConnectAction;
import com.webank.wedatasphere.exchangis.datasource.linkis.response.ParamsTestConnectResult;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceCreateVO;
import com.webank.wedatasphere.exchangis.datasource.vo.DataSourceQueryVO;
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
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import org.apache.linkis.datasource.client.request.*;
import org.apache.linkis.datasource.client.response.*;
import org.apache.linkis.datasource.client.response.DataSourceTestConnectResult;
import org.apache.linkis.datasource.client.response.GetDataSourceVersionsResult;
import org.apache.linkis.datasource.client.response.MetadataGetColumnsResult;
import org.apache.linkis.datasourcemanager.common.domain.DataSource;
import org.apache.linkis.datasourcemanager.common.domain.DataSourceType;
import org.apache.linkis.datasourcemanager.common.util.json.Json;
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
import java.util.stream.Collectors;

import static com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceExceptionCode.*;

@Service
public class ExchangisDataSourceService extends AbstractDataSourceService
        implements DataSourceUIGetter, DataSourceService{

    private final EngineSettingsDao settingsDao;

    private static final Logger LOG = LoggerFactory.getLogger(ExchangisDataSourceService.class);


    @Autowired
    public ExchangisDataSourceService(ExchangisDataSourceContext context,
                                      ExchangisJobParamConfigMapper exchangisJobParamConfigMapper, EngineSettingsDao settingsDao) {
        super(context, exchangisJobParamConfigMapper);
        this.settingsDao = settingsDao;
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Resource
    private ExchangisJobOpenService jobOpenService;

    @Resource
    private ProjectOpenService projectOpenService;

    @Autowired
    private ExchangisJobDsBindMapper exchangisJobDsBindMapper;
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

    // 根据数据源类型获取参数
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
    public Message listDataSources(HttpServletRequest request, String engineType, String direct, String sourceType) throws Exception {
        Collection<com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition> all = this.context.all();
        List<ExchangisRemoteDataSourceDefinition> dtos = new ArrayList<>();

        List<EngineSettings> settingsList = this.settingsDao.getSettings();
        List<EngineSettings> engineSettings = new ArrayList<>();


        if (StringUtils.isEmpty(engineType)) {
            engineSettings = settingsList;
        } else {
            EngineSettings engineSetting = new EngineSettings();
            for (int i = 0; i < settingsList.size(); i++) {
                if (StringUtils.equals(settingsList.get(i).getName(), engineType.toLowerCase())) {
                    engineSetting = settingsList.get(i);
                    break;
                }
            }
            engineSettings.add(engineSetting);
        }

        Set<String> directType = new HashSet<>();
        for (EngineSettings engineSetting: engineSettings) {
            for (int i = 0; i < engineSetting.getDirectionRules().size(); i++) {
                engineSetting.getDirectionRules().stream().forEach(item -> {
                    String source = item.getSource();
                    String sink = item.getSink();
                    if (StringUtils.isEmpty(direct)) {
                        directType.add(source);
                        directType.add(sink);
                    } else if (StringUtils.equals(direct, "source")) {
                        directType.add(source);
                    } else {
                        if ((StringUtils.isBlank(sourceType) ||
                                (StringUtils.isNoneBlank(sourceType) && StringUtils.equals(source, sourceType.toLowerCase())))) {
                            directType.add(sink);
                        }
                    }
                });
            }
        }

        String userName = UserUtils.getLoginUser(request);
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
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_TYPES_ERROR.getCode(), e.getMessage());
        }

        if (Objects.isNull(result)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_GET_TYPES_ERROR.getCode(), "datasource get types null or empty");
        }

        List<DataSourceType> allDataSourceType = new ArrayList<>();
        List<DataSourceType> dataSourceTypes = result.getAllDataSourceType();
        for (DataSourceType dataSourceType : dataSourceTypes) {
            if (directType.contains(dataSourceType.getName())) {
                allDataSourceType.add(dataSourceType);
            }
        }

        for (DataSourceType type : allDataSourceType) {
            for (com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition item : all) {
                if (item.name().equalsIgnoreCase(type.getName())) {
                    ExchangisRemoteDataSourceDefinition dto = new ExchangisRemoteDataSourceDefinition(
                            type.getId(),
                            type.getClassifier(),
                            item.name(),
                            item.structClassifier()
                    );
                    // use linkis datasource table field to fill the dto bean
                    dto.setIcon(type.getIcon());
                    dto.setDescription(type.getDescription());
                    dto.setOption(type.getOption());
                    dtos.add(dto);
                }
            }
        }

        return Message.ok().data("list", dtos);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Message create(String username, /*String type, */DataSourceCreateVO vo) throws ErrorException {
        //DataSourceCreateVO vo;
        Map<String, Object> json;
        try {
            json = mapper.readValue(mapper.writeValueAsString(vo), Map.class);
            json.put("labels",json.get("label"));
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARSE_JSON_ERROR.getCode(), e.getMessage());
        }
        String comment = vo.getComment();
        String createSystem = vo.getCreateSystem();
        if (Objects.isNull(comment)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "parameter comment should not be null");
        }

        if (Strings.isNullOrEmpty(createSystem)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "parameter createSystem should not be empty");
        }

        com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition dsType = context.getExchangisDsDefinition(vo.getDataSourceTypeId());
        if (Objects.isNull(dsType)) {
            throw new ExchangisDataSourceException(CONTEXT_GET_DATASOURCE_NULL.getCode(), "exchangis context get datasource null");
        }
        LinkisDataSourceRemoteClient client = dsType.getDataSourceRemoteClient();
        String responseBody;
        try {
            Result execute = client.execute(CreateDataSourceAction.builder()
                    .setUser(username)
                    .addRequestPayloads(json)
                    .build()
            );
            responseBody = execute.getResponseBody();
        } catch (Exception e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_CREATE_ERROR.getCode(), e.getMessage());
        }

        if (Strings.isNullOrEmpty(responseBody)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_CREATE_ERROR.getCode(), "datasource create response null or empty");
        }

        CreateDataSourceSuccessResult result = Json.fromJson(responseBody, CreateDataSourceSuccessResult.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }
        Long dataSourceId = result.getData().getId();
        UpdateDataSourceParameterResult updateDataSourceParameterResult;
        try {
            // 创建完成后发布数据源参数，形成一个版本
            updateDataSourceParameterResult = client.updateDataSourceParameter(
                    UpdateDataSourceParameterAction.builder()
                            .setUser(username)
                            .setDataSourceId(Long.parseLong(dataSourceId + ""))
                            .addRequestPayloads(json)
                            .build()
            );
        } catch (Exception e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), e.getMessage());
        }

        if (Objects.isNull(updateDataSourceParameterResult)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), "datasource update params version null or empty");
        }

        if (updateDataSourceParameterResult.getStatus() != 0) {
            throw new ExchangisDataSourceException(updateDataSourceParameterResult.getStatus(), updateDataSourceParameterResult.getMessage());
        }
        Message message = Message.ok();
        updateDataSourceParameterResult.getData().forEach(message::data);
        return message.data("id", dataSourceId);
    }

    @Transactional
    public Message updateDataSource(HttpServletRequest request, Long id, DataSourceCreateVO vo) throws Exception {

        Map<String, Object> json;
        try {
            json = mapper.readValue(mapper.writeValueAsString(vo), Map.class);
            json.put("labels",json.get("label"));
        } catch (JsonProcessingException e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARSE_JSON_ERROR.getCode(), e.getMessage());
        }
        String createSystem = vo.getCreateSystem();
        if (Strings.isNullOrEmpty(createSystem)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.PARAMETER_INVALID.getCode(), "parameter createSystem should not be empty");
        }
        String user = UserUtils.getLoginUser(request);
        com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition dsType = context.getExchangisDsDefinition(vo.getDataSourceTypeId());
        if (Objects.isNull(dsType)) {
            throw new ExchangisDataSourceException(30401, "exchangis.datasource.null");
        }

        LinkisDataSourceRemoteClient client = dsType.getDataSourceRemoteClient();
        String responseBody;
        try {
            Result execute = client.execute(UpdateDataSourceAction.builder()
                    .setUser(user)
                    .setDataSourceId(Long.parseLong(id + ""))
                    .addRequestPayloads(json)
                    .build()
            );
            responseBody = execute.getResponseBody();
        } catch (Exception e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_ERROR.getCode(), e.getMessage());
        }

        if (Strings.isNullOrEmpty(responseBody)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_ERROR.getCode(), "datasource update null or empty");
        }

        UpdateDataSourceSuccessResult updateDataSourceResult = Json.fromJson(responseBody, UpdateDataSourceSuccessResult.class);

        if (updateDataSourceResult.getStatus() != 0) {
            throw new ExchangisDataSourceException(updateDataSourceResult.getStatus(), updateDataSourceResult.getMessage());
        }

        UpdateDataSourceParameterResult updateDataSourceParameterResult;
        try {
            updateDataSourceParameterResult = client.updateDataSourceParameter(
                    UpdateDataSourceParameterAction.builder()
                            .setDataSourceId(Long.parseLong(id + ""))
                            .setUser(user)
                            .addRequestPayloads(json)
                            .build()
            );
        } catch (Exception e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_UPDATE_PARAMS_VERSION_ERROR.getCode(), e.getMessage());
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

        QueryWrapper<ExchangisJobDsBind> condition = new QueryWrapper<>();
        condition.eq("source_ds_id", id).or().eq("sink_ds_id", id);
        Long inUseCount = this.exchangisJobDsBindMapper.selectCount(condition);
        if (inUseCount > 0) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_DELETE_ERROR.getCode(), "目前存在引用依赖");
        }

        LinkisDataSourceRemoteClient dataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        //        DeleteDataSourceResult result;

        String responseBody;
        try {
            String user = UserUtils.getLoginUser(request);
            Result execute = dataSourceRemoteClient.execute(
                    new DeleteDataSourceAction.Builder().setUser(user).setDataSourceId(Long.parseLong(id + "")).builder()
            );
            responseBody = execute.getResponseBody();

        } catch (Exception e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_DELETE_ERROR.getCode(), e.getMessage());
        }

//        if (Objects.isNull(result)) {
        if (Strings.isNullOrEmpty(responseBody)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_DATASOURCE_DELETE_ERROR.getCode(), "datasource delete null or empty");
        }

        DeleteDataSourceSuccessResult result = Json.fromJson(responseBody, DeleteDataSourceSuccessResult.class);

        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(result.getStatus(), result.getMessage());
        }
//        return Message.ok().data("id", result.getRemove_id());
        return Message.ok().data("id", result.getData().getId());
    }

    /**
     * Query database from data source
     * @param username username
     * @param type type
     * @param id id
     * @return message
     * @throws Exception e
     */
    public Message queryDataSourceDBs(String username, String type, Long id) throws Exception {
        com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition definition = context.getExchangisDsDefinition(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = definition.getMetaDataRemoteClient();
        MetadataGetDatabasesResult databases;
        try {
            databases = metaDataRemoteClient.getDatabases(MetadataGetDatabasesAction.builder()
                    .setSystem("exchangis")
                    .setSystem(type)
                    .setDataSourceId(id)
                    .setUser(username)
                    .build());
        } catch (Exception e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_DATABASES_ERROR.getCode(), e.getMessage());
        }

        if (Objects.isNull(databases)) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_DATABASES_ERROR.getCode(), "metadata get databases null or empty");
        }

        List<String> dbs = Optional.ofNullable(databases.getDbs()).orElse(new ArrayList<>());
        return Message.ok().data("dbs", dbs);
    }

    /**
     * Query table in database from data source
     * @param username username
     * @param type type
     * @param id id
     * @param dbName database name
     * @return message
     * @throws Exception e
     */
    public Message queryDataSourceDBTables(String username, String type, Long id, String dbName) throws Exception {
        com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition definition = context.getExchangisDsDefinition(type);
        MetadataGetTablesResult tables;
        try {
            LinkisMetaDataRemoteClient metaDataRemoteClient = definition.getMetaDataRemoteClient();
            tables = metaDataRemoteClient.getTables(MetadataGetTablesAction.builder()
                    .setSystem(type)
                    .setDataSourceId(id)
                    .setDatabase(dbName)
                    .setUser(username)
                    .build()
            );
        } catch (Exception e) {
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_METADATA_GET_TABLES_ERROR.getCode(), e.getMessage());
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
        List<ExchangisDataSourceParamsUI> uis = new ArrayList<>();
        for (ExchangisJobInfoContent cnt : jobInfoContents) {
            uis.add(this.buildDataSourceParamsUI(null, cnt));
        }

        return Message.ok().data("ui", uis);
    }

    public Message getJobDataSourceTransformsUI(Long jobId) {
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

        String jobContent = job.getJobContent();
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

        List<ExchangisJobInfoContent> contents = JobUtils.parseJobContent(job.getJobContent());

        for (ExchangisJobInfoContent content : contents) {
            if (content.getSubJobName().equalsIgnoreCase(jobName)) {
                List<ElementUI<?>> uis = this.buildJobSettingsUI(job.getEngineType(), content);
                return Message.ok().data("uis", uis);
            }
        }

        return Message.ok().data("ui", Collections.emptyList());

    }

    /**
     * Query table fields
     * @param username username
     * @param type type
     * @param id id
     * @param dbName database name
     * @param tableName table name
     * @return message
     * @throws Exception e
     */
    public Message queryDataSourceDBTableFields(String username, String type, Long id, String dbName, String tableName) throws Exception {
        com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition definition = context.getExchangisDsDefinition(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = definition.getMetaDataRemoteClient();
        List<MetaColumnInfo> allColumns;
        try {
            MetadataGetColumnsResult columns = metaDataRemoteClient.getColumns(MetadataGetColumnsAction.builder()
                    .setSystem(type)
                    .setDataSourceId(id)
                    .setDatabase(dbName)
                    .setTable(tableName)
                    .setUser(username)
                    .build());
            allColumns = columns.getAllColumns();
        } catch (Exception e) {
            throw new ExchangisDataSourceException(CLIENT_METADATA_GET_COLUMNS_ERROR.getCode(), e.getMessage());
        }

        if (Objects.isNull(allColumns)) {
            throw new ExchangisDataSourceException(CLIENT_METADATA_GET_COLUMNS_ERROR.getCode(), "metadata get columns null or empty");
        }

        List<DataSourceDbTableColumn> list = new ArrayList<>();
        allColumns.forEach(col -> {
            DataSourceDbTableColumn item = new DataSourceDbTableColumn();
            item.setName(col.getName());
            item.setType(col.getType());
            list.add(item);
        });

        return Message.ok().data("columns", list);
    }

    /**
     * Query data sources
     * @param request request
     * @param vo view object
     * @return response message
     * @throws Exception e
     */
    public Message queryDataSources(HttpServletRequest request, DataSourceQueryVO vo) throws Exception {
        String username = StringUtils.isNoneBlank(vo.getCreateUser()) ?
                vo.getCreateUser() : UserUtils.getLoginUser(request);
        int page = Objects.isNull(vo.getPage()) ? 1 : vo.getPage();
        int pageSize = Objects.isNull(vo.getPageSize()) ? 100 : vo.getPageSize();
        String dataSourceName = Objects.isNull(vo.getName()) ? "" : vo.getName().replace("_", "\\_");
        Long typeId = vo.getTypeId();
        int total = 0;
        // If to fetch from remote server
        boolean toRemote = true;
        Map<String, ExchangisRemoteDataSource> dsQueryMap = new LinkedHashMap<>();
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
                    Optional.ofNullable(toExchangisDataSources(refProjectId, dsRelations.getList()))
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
            // Request linkis server to get data sources
            LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
            QueryDataSourceResult result;
            try {
                QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                        .setSystem("exchangis")
                        .setName(dataSourceName)
                        .setIdentifies("")
                        .setCurrentPage(page)
                        .setUser(username)
                        .setPageSize(pageSize);
                if (!Objects.isNull(typeId)) {
                    builder.setTypeId(typeId);
                }
                if (!Strings.isNullOrEmpty(vo.getTypeName())) {
                    builder.setSystem(vo.getTypeName());
                }
                QueryDataSourceAction action = builder.build();
                result = linkisDataSourceRemoteClient.queryDataSource(action);
                total += result.getTotalPage();
                List<DataSource> dataSources = result.getAllDataSource();
                int addSize = Math.min(pageSize - dsQueryMap.size(), dataSources.size());
                if (addSize > 0){
                    Optional.ofNullable(toExchangisDataSources(dataSources.subList(0, addSize)))
                            .ifPresent(list -> {
                                for(int i = 0; i < addSize; i++) {
                                    ExchangisRemoteDataSource item = list.get(i);
                                    if (!dsQueryMap.containsKey(item.getName())) {
                                        dsQueryMap.put(item.getName(), item);
                                    }
                                }
                            });
                }
            } catch (Exception e) {
                if (e instanceof ErrorException) {
                    ErrorException ee = (ErrorException) e;
                    throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage(), ee.getIp(), ee.getPort(), ee.getServiceKind());
                } else {
                    throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
                }
            }
        }
        Message message = Message.ok();
        message.data("list", dsQueryMap.values());
        message.data("total", total);
        return message;
    }

    public Message listAllDataSources(HttpServletRequest request, String typeName, Long typeId, Integer page, Integer pageSize) throws ExchangisDataSourceException {
        String userName = UserUtils.getLoginUser(request);
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                .setSystem("exchangis")
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
            throw new ExchangisDataSourceException(ExchangisDataSourceExceptionCode.CLIENT_QUERY_DATASOURCE_ERROR.getCode(), e.getMessage());
        }
        List<ExchangisRemoteDataSource> dataSources = new ArrayList<>();
        if (!Objects.isNull(allDataSource)) {
            dataSources = toExchangisDataSources(allDataSource);
        }
        return Message.ok().data("list", dataSources);
    }

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

        GetDataSourceInfoResult.DataSourceItemDTO info = result.getData().getInfo();
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
    public Message testConnectByVo(HttpServletRequest request, DataSourceCreateVO vo) throws ErrorException {
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
                this.checkDataXDSSupportDegree(sourceDsType, sinkDsType);
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

    private void checkDataXDSSupportDegree(String sourceDsType, String sinkDsType) throws ExchangisDataSourceException {

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
            DataSourceCreateVO createDs = new DataSourceCreateVO();
            GetDataSourceInfoResult.DataSourceItemDTO modelDs = dsResult.getData().getInfo();
            createDs.setDataSourceName(newName);
            createDs.setDataSourceTypeId(modelDs.getDataSourceTypeId());
            createDs.setDataSourceDesc(modelDs.getDataSourceDesc());
            createDs.setCreateSystem(modelDs.getCreateSystem());
            createDs.setCreateUser(operator);
            createDs.setLabel(modelDs.getLabel());
            createDs.setConnectParams(modelDs.getConnectParams());
            createDs.setComment("init");
            Message resultMsg = create(operator, createDs);
            Object version = resultMsg.getData().get("version");
            Object id = resultMsg.getData().get("id");
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
                    String user = "SYSTEM";
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
     * Convert project data source relations to exchangis data sources
     * @param dsRelations relation
     * @return list
     */
    private List<ExchangisRemoteDataSource> toExchangisDataSources(Long projectId, List<ExchangisProjectDsRelation> dsRelations){
        return dsRelations.stream().map(ds -> {
            ExchangisRemoteDataSource item = new ExchangisRemoteDataSource();
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
    private List<ExchangisRemoteDataSource> toExchangisDataSources(List<DataSource> dataSources){
        return dataSources.stream().map(ds -> {
            ExchangisRemoteDataSource item = new ExchangisRemoteDataSource();
            item.setId(ds.getId());
            item.setCreateIdentify(ds.getCreateIdentify());
            item.setName(ds.getDataSourceName());
            item.setType(ds.getCreateSystem());
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
