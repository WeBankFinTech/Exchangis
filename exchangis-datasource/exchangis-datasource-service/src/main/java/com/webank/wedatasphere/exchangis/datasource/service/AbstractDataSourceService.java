package com.webank.wedatasphere.exchangis.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.ui.*;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.DefaultDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobDataSourcesContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsContent;
import com.webank.wedatasphere.exchangis.datasource.dto.GetDataSourceInfoResultDTO;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import org.apache.linkis.datasource.client.request.GetInfoByDataSourceIdAction;
import org.apache.linkis.httpclient.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class AbstractDataSourceService {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ExchangisDataSourceContext context;
    protected final ExchangisJobParamConfigMapper exchangisJobParamConfigMapper;

    private final static Logger LOG = LoggerFactory.getLogger(AbstractDataSourceService.class);


    public AbstractDataSourceService(ExchangisDataSourceContext context, ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.context = context;
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }

    protected List<ExchangisJobInfoContent> parseJobContent(String content) {
        List<ExchangisJobInfoContent> jobInfoContents;
        if (Strings.isNullOrEmpty(content)) {
            jobInfoContents = new ArrayList<>();
        } else {
            try {
                jobInfoContents = this.mapper.readValue(content, new TypeReference<List<ExchangisJobInfoContent>>() {
                });
            } catch (JsonProcessingException e) {
                jobInfoContents = new ArrayList<>();
            }
        }
        return jobInfoContents;
    }

    private ExchangisDataSourceIdsUI buildDataSourceIdsUI(ExchangisJobInfoContent content) {
        return this.buildDataSourceIdsUI(null, content);
    }

    private ExchangisDataSourceIdsUI buildDataSourceIdsUI(HttpServletRequest request, ExchangisJobInfoContent content) {
        String loginUser = Optional.ofNullable(request).isPresent() ? UserUtils.getLoginUser(request) : null;
        ExchangisJobDataSourcesContent dataSources = content.getDataSources();
        if (Objects.isNull(dataSources)) {
            return null;
        }

        String sourceId = dataSources.getSourceId();
        String sinkId = dataSources.getSinkId();

        if (Strings.isNullOrEmpty(sourceId) && Strings.isNullOrEmpty(sinkId)) {
            return null;
        }

        ExchangisDataSourceIdsUI ids = new ExchangisDataSourceIdsUI();
        if (!Strings.isNullOrEmpty(sourceId)) {
            String[] split = sourceId.trim().split("\\.");
            ExchangisDataSourceIdUI source = new ExchangisDataSourceIdUI();
            source.setType(split[0]);
            source.setId(split[1]);
            Optional.ofNullable(loginUser).ifPresent(u -> {
                Optional.ofNullable(this.context.getExchangisDataSource(split[0])).ifPresent(o -> {
                    LinkisDataSourceRemoteClient dsClient = o.getDataSourceRemoteClient();
                    GetInfoByDataSourceIdAction action = GetInfoByDataSourceIdAction.builder()
                            .setDataSourceId(Long.parseLong(split[1]))
                            .setUser(u)
                            .setSystem(split[0])
                            .build();

                    Result execute = dsClient.execute(action);
                    String responseBody = execute.getResponseBody();
                    GetDataSourceInfoResultDTO dsInfo = null;
                    dsInfo = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);
                    assert dsInfo != null;
                    source.setDs(dsInfo.getData().getInfo().getDataSourceName());
                });
            });
            source.setDb(split[2]);
            source.setTable(split[3]);

            ids.setSource(source);
        }

        if (!Strings.isNullOrEmpty(sinkId)) {
            String[] split = sinkId.trim().split("\\.");
            ExchangisDataSourceIdUI sink = new ExchangisDataSourceIdUI();
            sink.setType(split[0]);
            sink.setId(split[1]);
            Optional.ofNullable(loginUser).ifPresent(u -> {
                Optional.ofNullable(this.context.getExchangisDataSource(split[0])).ifPresent(o -> {
                    LinkisDataSourceRemoteClient dsClient = o.getDataSourceRemoteClient();
                    GetInfoByDataSourceIdAction action = GetInfoByDataSourceIdAction.builder()
                            .setDataSourceId(Long.parseLong(split[1]))
                            .setUser(u)
                            .setSystem(split[0])
                            .build();
                    Result execute = dsClient.execute(action);
                    String responseBody = execute.getResponseBody();
                    GetDataSourceInfoResultDTO dsInfo = null;
                    dsInfo = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);
                    assert dsInfo != null;
                    sink.setDs(dsInfo.getData().getInfo().getDataSourceName());
                });
            });

            sink.setDb(split[2]);
            sink.setTable(split[3]);

            ids.setSink(sink);
        }
        return ids;
    }

    protected ExchangisDataSourceParamsUI buildDataSourceParamsUI(ExchangisJobInfoContent content) {
        ExchangisDataSourceIdsUI dataSourceIdsUI = buildDataSourceIdsUI(content);

        ExchangisJobParamsContent params = content.getParams();
        List<ExchangisJobParamConfig> sourceParamConfigs = Collections.emptyList();
        List<ExchangisJobParamConfig> sinkParamConfigs = Collections.emptyList();
        if (null != dataSourceIdsUI) {
            ExchangisDataSourceIdUI source = dataSourceIdsUI.getSource();
            if (null != source) {
                String type = source.getType();
                ExchangisDataSource exchangisSourceDataSource = this.context.getExchangisDataSource(type);
                if (null != exchangisSourceDataSource) {
                    sourceParamConfigs = exchangisSourceDataSource.getDataSourceParamConfigs().stream().filter(
                            i -> i.getConfigDirection().equals(content.getEngine() + "-SOURCE") || "SOURCE".equalsIgnoreCase(i.getConfigDirection())).collect(Collectors.toList());
                }
            }

            ExchangisDataSourceIdUI sink = dataSourceIdsUI.getSink();
            if (null != sink) {
                String type = sink.getType();
                ExchangisDataSource exchangisSinkDataSource = this.context.getExchangisDataSource(type);
                if (null != exchangisSinkDataSource) {
                    sinkParamConfigs = exchangisSinkDataSource.getDataSourceParamConfigs().stream().filter(i ->
                            i.getConfigDirection().equals(content.getEngine() + "-SINK") || "SINK".equalsIgnoreCase(i.getConfigDirection())).collect(Collectors.toList());
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

        List<ElementUI<?>> jobDataSourceParamsUI1 = buildDataSourceParamsFilledValueUI(sourceParamConfigs, sourceParamsItems);
        List<ElementUI<?>> jobDataSourceParamsUI2 = buildDataSourceParamsFilledValueUI(sinkParamConfigs, sinkParamsItems);
        ExchangisDataSourceParamsUI paramsUI = new ExchangisDataSourceParamsUI();
        paramsUI.setSources(jobDataSourceParamsUI1);
        paramsUI.setSinks(jobDataSourceParamsUI2);
        return paramsUI;
    }

    protected ExchangisDataSourceUIViewer buildAllUI(HttpServletRequest request, ExchangisJobEntity job, ExchangisJobInfoContent content) {
        // ----------- 构建 dataSourceIdsUI
        ExchangisDataSourceIdsUI dataSourceIdsUI = buildDataSourceIdsUI(request, content);

        // ----------- 构建 dataSourceParamsUI
        ExchangisDataSourceParamsUI paramsUI = buildDataSourceParamsUI(content);

        // ----------- 构建 dataSourceTransformsUI
        ExchangisJobTransformsContent transforms = content.getTransforms();
        transforms.setAddEnable(!("HIVE".equals(dataSourceIdsUI.getSource().getType()) || "HIVE".equals(dataSourceIdsUI.getSink().getType())));

//        ExchangisDataSourceTransformsUI dataSourceTransFormsUI = ExchangisDataSourceUIViewBuilder.getDataSourceTransFormsUI(transforms);

        List<ElementUI<?>> jobDataSourceSettingsUI = this.buildJobSettingsUI(job.getEngineType(), content);

        return new DefaultDataSourceUIViewer(content.getSubJobName(), dataSourceIdsUI, paramsUI, transforms, jobDataSourceSettingsUI);
    }


    protected List<ElementUI<?>> buildJobSettingsUI(String jobEngineType) {
        if (Strings.isNullOrEmpty(jobEngineType)) {
            return Collections.emptyList();
        }
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", jobEngineType);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        List<ExchangisJobParamConfig> settingParamConfigs = exchangisJobParamConfigMapper.selectList(queryWrapper);
        return buildDataSourceParamsFilledValueUI(settingParamConfigs, null);
    }

    protected List<ElementUI<?>> buildJobSettingsUI(String jobEngineType, ExchangisJobInfoContent content) {
        if (Strings.isNullOrEmpty(jobEngineType)) {
            return Collections.emptyList();
        }
        List<ExchangisJobParamsContent.ExchangisJobParamsItem> settings = content.getSettings();
        QueryWrapper<ExchangisJobParamConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", jobEngineType);
        queryWrapper.eq("is_hidden", 0);
        queryWrapper.eq("status", 1);
        List<ExchangisJobParamConfig> settingParamConfigs = exchangisJobParamConfigMapper.selectList(queryWrapper);
        return buildDataSourceParamsFilledValueUI(settingParamConfigs, settings);
    }

    protected List<ElementUI<?>> buildDataSourceParamsUI(List<ExchangisJobParamConfig> paramConfigs) {
        List<ElementUI<?>> uis = new ArrayList<>();
        if (!Objects.isNull(paramConfigs) && !paramConfigs.isEmpty()) {
            for (ExchangisJobParamConfig cfg : paramConfigs) {
                ElementUI<?> ui = fillElementUIValue(cfg, "");
                uis.add(ui);
            }
        }
        return uis;
    }

    protected List<ElementUI<?>> buildDataSourceParamsFilledValueUI(List<ExchangisJobParamConfig> paramConfigs, List<ExchangisJobParamsContent.ExchangisJobParamsItem> paramsList) {
        List<ElementUI<?>> uis = new ArrayList<>();
        if (!Objects.isNull(paramConfigs) && !paramConfigs.isEmpty()) {
            for (ExchangisJobParamConfig cfg : paramConfigs) {
                if (Objects.isNull(paramsList) || paramsList.isEmpty()) {
                    uis.add(fillElementUIValue(cfg, ""));
                    continue;
                }
                ExchangisJobParamsContent.ExchangisJobParamsItem selectedParamItem = getJobParamsItem(cfg.getConfigKey(), paramsList);
                if (Objects.isNull(selectedParamItem)) {
                    ElementUI<?> ui = fillElementUIValue(cfg, "");
                    uis.add(ui);
                } else {
                    ElementUI<?> ui = fillElementUIValue(cfg, selectedParamItem.getConfigValue());
                    uis.add(ui);
                }
            }
        }
        return uis;
    }

    private ExchangisJobParamsContent.ExchangisJobParamsItem getJobParamsItem(String configKey, List<ExchangisJobParamsContent.ExchangisJobParamsItem> sources) {
        for (ExchangisJobParamsContent.ExchangisJobParamsItem item : sources) {
            if (item.getConfigKey().equalsIgnoreCase(configKey)) {
                return item;
            }
        }
        return null;
    }

    private ElementUI<?> fillElementUIValue(ExchangisJobParamConfig config, Object value) {
        String uiType = config.getUiType();
        ElementUI.Type uiTypeEnum;
        try {
            uiTypeEnum = StringUtils.isNotBlank(uiType)?
                    ElementUI.Type.valueOf(uiType.toUpperCase(Locale.ROOT)) : ElementUI.Type.NONE;
        }catch (Exception e){
            uiTypeEnum = ElementUI.Type.NONE;
        }
        switch (uiTypeEnum) {
            case OPTION:
                return fillOptionElementUIValue(config, String.valueOf(value));
            case INPUT:
                return fillInputElementUIValue(config, String.valueOf(value));
            case MAP:
                Map<String, Object> mapElement = null;
                try {
                    mapElement = Json.fromJson(Json.toJson(value, null),
                            Map.class, String.class, Object.class);
                } catch (Exception e) {
                    LOG.info("Exception happened while parse json"+ "Config value: " + value + "message: " + e.getMessage(), e);
                }
                return fillMapElementUIValue(config, mapElement);
            default:
                return null;
        }
    }


    private OptionElementUI fillOptionElementUIValue(ExchangisJobParamConfig config, String value) {
        String valueRange = config.getValueRange();
        List values = Collections.emptyList();
        try {
            values = mapper.readValue(valueRange, List.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        OptionElementUI ui = new OptionElementUI();
        ui.setId(config.getId());
        ui.setKey(config.getConfigKey());
        ui.setField(config.getUiField());
        ui.setLabel(config.getUiLabel());
        ui.setValues(values);
        ui.setValue(value);
        ui.setDefaultValue(config.getDefaultValue());
        ui.setSort(config.getSort());
        ui.setRequired(config.getRequired());
        ui.setUnit(config.getUnit());
        ui.setRefId(config.getRefId());
        return ui;
    }

    private InputElementUI fillInputElementUIValue(ExchangisJobParamConfig config, String value) {
        InputElementUI ui = new InputElementUI();
        ui.setId(config.getId());
        ui.setKey(config.getConfigKey());
        ui.setField(config.getUiField());
        ui.setLabel(config.getUiLabel());
        ui.setValue(value);
        ui.setDefaultValue(config.getDefaultValue());
        ui.setSort(config.getSort());
        ui.setRequired(config.getRequired());
        ui.setUnit(config.getUnit());
        ui.setSource(config.getSource());
        ui.setValidateType(config.getValidateType());
        ui.setValidateRange(config.getValidateRange());
        ui.setValidateMsg(config.getValidateMsg());
        ui.setRefId(config.getRefId());
        return ui;
    }

    private MapElementUI fillMapElementUIValue(ExchangisJobParamConfig config, Map<String, Object> value) {
        MapElementUI ui = new MapElementUI();
        ui.setId(config.getId());
        ui.setKey(config.getConfigKey());
        ui.setField(config.getUiField());
        ui.setLabel(config.getUiLabel());
        ui.setValue(value);
        //ui.setDefaultValue(config.getDefaultValue());
        ui.setSort(config.getSort());
        ui.setRequired(config.getRequired());
        ui.setUnit(config.getUnit());
        ui.setSource(config.getSource());
        ui.setValidateType(config.getValidateType());
        ui.setValidateRange(config.getValidateRange());
        ui.setValidateMsg(config.getValidateMsg());
        ui.setRefId(config.getRefId());
        return ui;
    }

}
