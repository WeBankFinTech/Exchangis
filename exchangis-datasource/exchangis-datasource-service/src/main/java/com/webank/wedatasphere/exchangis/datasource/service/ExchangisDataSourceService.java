package com.webank.wedatasphere.exchangis.datasource.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobInfo;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobInfoMapper;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.*;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
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
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisMetaDataRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.*;
import com.webank.wedatasphere.linkis.datasource.client.response.*;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSource;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;
import com.webank.wedatasphere.linkis.httpclient.request.Action;
import com.webank.wedatasphere.linkis.httpclient.response.Result;
import com.webank.wedatasphere.linkis.metadatamanager.common.Json;
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
     *
     */
    public Message listDataSources(HttpServletRequest request) {
        Collection<ExchangisDataSource> all = this.context.all();
        List<ExchangisDataSourceDTO> dtos = new ArrayList<>();

//        String userName = SecurityFilter.getLoginUsername(request);
        String userName = "hdfs";

        // 通过 datasourcemanager 获取的数据源类型和context中的数据源通过 type 和 name 比较
        // 以 exchangis 中注册了的数据源集合为准
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        GetAllDataSourceTypesResult result = linkisDataSourceRemoteClient.getAllDataSourceTypes(GetAllDataSourceTypesAction.builder()
                .setUser(userName)
                .build()
        );

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
            throw new ExchangisDataSourceException(30401, e.getMessage());
        }

        String user = "hdfs";

        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(vo.getDataSourceTypeId());
        if (Objects.isNull(exchangisDataSource)) {
            throw new ExchangisDataSourceException(30401, "exchangis.datasource.null");
        }

        LinkisDataSourceRemoteClient client = exchangisDataSource.getDataSourceRemoteClient();

        Map<String, Object> connectParams = vo.getConnectParams();
        if (!Objects.isNull(connectParams)) {
            json.put("parameter", mapper.writeValueAsString(connectParams));
        }

        Result execute = client.execute(CreateDataSourceAction.builder()
                .setUser(user)
                .addRequestPayloads(json)
                .build()
        );
        String responseBody = execute.getResponseBody();

        CreateDataSourceSuccessResultDTO rest = Json.fromJson(responseBody, CreateDataSourceSuccessResultDTO.class);

        if (rest.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, "exchangis.create.datasource.error");
        }

        Long dataSourceId = rest.getData().getId();
        // 创建完成后发布数据源参数，形成一个版本
        Result versionExec = client.execute(
                UpdateDataSourceParameterAction.builder()
                        .setDataSourceId(dataSourceId)
                        .addRequestPayloads(json)
                        .build()
        );
        String versionResponseBody = versionExec.getResponseBody();
        UpdateParamsVersionResultDTO versionRest = Json.fromJson(versionResponseBody, UpdateParamsVersionResultDTO.class);
        if (versionRest.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, versionRest.getMessage());
        }

//        Result versionExec = client.execute(
//                new PublishDataSourceVersionAction(rest.getData().getId(), 1L)
//        );
//        String versionResponseBody = versionExec.getResponseBody();


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
        String user = "hdfs";

        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(vo.getDataSourceTypeId());
        if (Objects.isNull(exchangisDataSource)) {
            throw new ExchangisDataSourceException(30401, "exchangis.datasource.null");
        }

        LinkisDataSourceRemoteClient client = exchangisDataSource.getDataSourceRemoteClient();

        Result execute = client.execute(UpdateDataSourceAction.builder()
                .setUser(user)
                .setDataSourceId(id)
                .addRequestPayloads(json)
                .build()
        );
        String responseBody = execute.getResponseBody();

        UpdateDataSourceSuccessResultDTO rest = Json.fromJson(responseBody, UpdateDataSourceSuccessResultDTO.class);

        if (rest.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, "exchangis.update.datasource.error");
        }

        Result versionExec = client.execute(
                UpdateDataSourceParameterAction.builder()
                        .setDataSourceId(id)
                        .addRequestPayloads(json)
                        .build()
        );
        String versionResponseBody = versionExec.getResponseBody();
        UpdateParamsVersionResultDTO versionRest = Json.fromJson(versionResponseBody, UpdateParamsVersionResultDTO.class);
        if (versionRest.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, versionRest.getMessage());
        }

        return Message.ok();
    }

    @Transactional
    public Message deleteDataSource(HttpServletRequest request, /*String type,*/ Long id) throws Exception {
//        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
//        LinkisDataSourceRemoteClient dataSourceRemoteClient = exchangisDataSource.getDataSourceRemoteClient();

        LinkisDataSourceRemoteClient dataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();

        Result execute = dataSourceRemoteClient.execute(
                new DeleteDataSourceAction(id + "")
        );

        String responseBody = execute.getResponseBody();

        DeleteDataSourceSuccessResultDTO rest = Json.fromJson(responseBody, DeleteDataSourceSuccessResultDTO.class);

        if (rest.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, "exchangis.delete.datasource.error");
        }
        return Message.ok().data("id", rest.getData().getId());
    }

    public Message queryDataSourceDBs(HttpServletRequest request, String type, Long id) {
        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
        LinkisMetaDataRemoteClient metaDataRemoteClient = exchangisDataSource.getMetaDataRemoteClient();

        //        String userName = SecurityFilter.getLoginUsername(request);
        String userName = "hdfs";

        MetadataGetDatabasesResult databases = metaDataRemoteClient.getDatabases(MetadataGetDatabasesAction.builder()
                .setSystem("system")
                .setDataSourceId(id)
                .setUser(userName)
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
        String username = "hdfs";
        Integer page = Objects.isNull(vo.getPage()) ? 1 : vo.getPage();
        Integer pageSize = Objects.isNull(vo.getPageSize()) ? 20 : vo.getPageSize();

        String dataSourceName = Objects.isNull(vo.getName()) ? "" : vo.getName();

        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        QueryDataSourceAction.Builder builder = QueryDataSourceAction.builder()
                .setSystem("")
                .setName(dataSourceName)
//                .setTypeId()
//                .setTypeId(typeId)
                .setIdentifies("")
                .setCurrentPage(page)
                .setUser(username)
                .setPageSize(pageSize);

        Long typeId = vo.getTypeId();
        if (!Objects.isNull(typeId)) {
            builder.setTypeId(typeId);
        }
        QueryDataSourceAction action = builder.build();
        QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(action);

//        String name = vo.getName();
//        String type = vo.getType();
//        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
//        LinkisDataSourceRemoteClient dataSourceRemoteClient = exchangisDataSource.getDataSourceRemoteClient();
//        QueryDataSourceResult result = dataSourceRemoteClient.queryDataSource(QueryDataSourceAction.builder()
//                        .setSystem("")
//                        .setName("linkis")
//                        .setTypeId(1)
//                        .setIdentifies("")
//                        .setCurrentPage(1)
//                        .setUser("hdfs")
//                        .setPageSize(20).build()
//        );

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

    public Message listAllDataSources(HttpServletRequest request) {
        //        String userName = SecurityFilter.getLoginUsername(request);
        String userName = "hdfs";

        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        QueryDataSourceResult result = linkisDataSourceRemoteClient.queryDataSource(QueryDataSourceAction.builder()
                .setSystem("")
                // TODO 传类型参数查询，分页参数
                .setName("mysql")
                .setTypeId(1)
                .setIdentifies("")
                .setCurrentPage(1)
                .setPageSize(20)
                .setUser(userName)
                .build()
        );

        List<DataSource> allDataSource = result.getAllDataSource();

        List<DataSourceDTO> dataSources = new ArrayList<>();
        allDataSource.forEach(ds -> {
            DataSourceDTO item = new DataSourceDTO();
            item.setId(ds.getId());
            item.setCreateIdentify(ds.getCreateIdentify());
            item.setName(ds.getDataSourceName());
            item.setType(ds.getDataSourceType().getName());
            item.setDataSourceTypeId(ds.getDataSourceTypeId());
            dataSources.add(item);
        });

        return Message.ok().data("list", dataSources);
    }

    public Message getDataSource(HttpServletRequest request, Long id) throws ErrorException {
//        ExchangisDataSource exchangisDataSource = context.getExchangisDataSource(type);
//        LinkisDataSourceRemoteClient dataSourceRemoteClient = exchangisDataSource.getDataSourceRemoteClient();
//
//        Result execute = dataSourceRemoteClient.execute(
//                new DeleteDataSourceAction(id + "")
//        );



        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        Result execute = linkisDataSourceRemoteClient.execute(
                new GetDataSourceAction(id + "")
        );

        String responseBody = execute.getResponseBody();
        GetDataSourceInfoResultDTO result = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, result.getMessage());
        }

        return Message.ok().data("info", Objects.isNull(result.getData()) ? null : result.getData().getInfo());

//        GetDataSourceSuccessResultDTO rest = Json.fromJson(responseBody, GetDataSourceSuccessResultDTO.class);
//
//        if (rest.getStatus() != 0) {
//            throw new ExchangisDataSourceException(23001, "exchangis.get.datasource.error");
//        }
//        return Message.ok().data("dataSource", rest.getData().getId());
    }

    public Message getDataSourceVersionsById(HttpServletRequest request, Long id) throws ErrorException  {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();

        // 先根据ID获取数据源详情
        Result execute = linkisDataSourceRemoteClient.execute(
                new GetDataSourceAction(id + "")
        );

        String responseBody = execute.getResponseBody();
        GetDataSourceInfoResultDTO getResult = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);
        if (getResult.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, getResult.getMessage());
        }
        Long publishedVersionId = null;
        GetDataSourceInfoResultDTO.DataSourceInfoDTO data = getResult.getData();
        if (!Objects.isNull(data)) {
            GetDataSourceInfoResultDTO.DataSourceItemDTO info = data.getInfo();
            if (!Objects.isNull(info)) {
                publishedVersionId = info.getPublishedVersionId();
            }
        }

        Result versionExecute = linkisDataSourceRemoteClient.execute(
                new GetDataSourceVersionsAction(id + "")
        );

        String versionResponseBody = versionExecute.getResponseBody();

        GetDataSourceVersionsResultDTO result = Json.fromJson(versionResponseBody, GetDataSourceVersionsResultDTO.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, result.getMessage());
        }

        GetDataSourceVersionsResultDTO.VersionDataDTO versionData = result.getData();
        if (!Objects.isNull(versionData)) {
            List<GetDataSourceVersionsResultDTO.VersionDTO> versions = versionData.getVersions();
            if (!Objects.isNull(versions)) {
                for (GetDataSourceVersionsResultDTO.VersionDTO version : versions) {
                    if (Objects.equals(version.getVersionId(), publishedVersionId)) {
                        version.setPublished(true);
                    }
                }
            }
        }


        return Message.ok().data("versions", result.getData().getVersions());
    }

    public Message testConnect(HttpServletRequest request, Long id, Long version) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        Result execute = linkisDataSourceRemoteClient.execute(
                new DataSourceTestConnectAction(id, version)
        );

        String responseBody = execute.getResponseBody();

        DataSourceTestConnectResultDTO result = Json.fromJson(responseBody, DataSourceTestConnectResultDTO.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, result.getMessage());
        }

        return Message.ok();
    }

    public Message publishDataSource(HttpServletRequest request, Long id, Long version) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        Result execute = linkisDataSourceRemoteClient.execute(
                new PublishDataSourceVersionAction(id, version)
        );

        String responseBody = execute.getResponseBody();

        ResultDTO result = Json.fromJson(responseBody, ResultDTO.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, result.getMessage());
        }

        return Message.ok();
    }

    public Message getDataSourceConnectParamsById(HttpServletRequest request, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        Result execute = linkisDataSourceRemoteClient.execute(
                new GetDataSourceConnectParamsAction(id + "")
        );

        String responseBody = execute.getResponseBody();
        GetDataSourceConnectParamsResultDTO result = Json.fromJson(responseBody, GetDataSourceConnectParamsResultDTO.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, result.getMessage());
        }

        return Message.ok().data("info", Objects.isNull(result.getData()) ? null : result.getData());
    }

    @Transactional
    public Message expireDataSource(HttpServletRequest request, Long id) throws ErrorException {
        LinkisDataSourceRemoteClient linkisDataSourceRemoteClient = ExchangisLinkisRemoteClient.getLinkisDataSourceRemoteClient();
        Result execute = linkisDataSourceRemoteClient.execute(
                new ExpireDataSourceAction(id)
        );

        String responseBody = execute.getResponseBody();

        ExpireDataSourceSuccessResultDTO result = Json.fromJson(responseBody, ExpireDataSourceSuccessResultDTO.class);
        if (result.getStatus() != 0) {
            throw new ExchangisDataSourceException(23001, result.getMessage());
        }

        return Message.ok();
    }
}
