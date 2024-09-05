package com.webank.wedatasphere.exchangis.datasource.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.common.UserUtils;
import com.webank.wedatasphere.exchangis.common.config.GlobalConfiguration;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.dao.mapper.ExchangisJobParamConfigMapper;
import com.webank.wedatasphere.exchangis.datasource.core.ExchangisDataSourceDefinition;
import com.webank.wedatasphere.exchangis.datasource.core.context.ExchangisDataSourceContext;
import com.webank.wedatasphere.exchangis.datasource.core.ui.*;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.DefaultDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceDetail;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobDataSourcesContent;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.job.domain.content.ExchangisJobTransformsContent;
import com.webank.wedatasphere.exchangis.job.domain.ExchangisJobEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractDataSourceService extends AbstractLinkisDataSourceService implements DataSourceService {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ExchangisDataSourceContext context;
    protected final ExchangisJobParamConfigMapper exchangisJobParamConfigMapper;

    private final static Logger LOG = LoggerFactory.getLogger(AbstractDataSourceService.class);


    public AbstractDataSourceService(ExchangisDataSourceContext context, ExchangisJobParamConfigMapper exchangisJobParamConfigMapper) {
        this.context = context;
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
    }

    private ExchangisDataSourceIdsUI buildDataSourceIdsUI(ExchangisJobInfoContent content) {
        return this.buildDataSourceIdsUI(null, content);
    }

    /**
     * Build data source ui for 'ids'
     * @param request client request
     * @param content content
     * @return ui entity
     */
    private ExchangisDataSourceIdsUI buildDataSourceIdsUI(HttpServletRequest request, ExchangisJobInfoContent content) {
        String requestUser = UserUtils.getLoginUser(request);
        ExchangisJobDataSourcesContent dataSources = content.getDataSources();
        if (Objects.isNull(dataSources)) {
            return null;
        }
        String sourceId = dataSources.getSourceId();
        String sinkId = dataSources.getSinkId();
        ExchangisDataSourceIdUI sourceUi = parseDataSourceIdUi(requestUser, sourceId,
                dataSources.getSource());
        ExchangisDataSourceIdUI sinkUi = parseDataSourceIdUi(requestUser, sinkId,
                dataSources.getSink());
        ExchangisDataSourceIdsUI ids = new ExchangisDataSourceIdsUI();
        ids.setSource(sourceUi);
        ids.setSink(sinkUi);
        return ids;
    }

    protected ExchangisDataSourceParamsUI buildDataSourceParamsUI(ExchangisDataSourceIdsUI dataSourceIdsUi, ExchangisJobInfoContent content) {
        ExchangisJobParamsContent params = content.getParams();
        List<ExchangisJobParamConfig> sourceParamConfigs = Collections.emptyList();
        List<ExchangisJobParamConfig> sinkParamConfigs = Collections.emptyList();
        if (null != dataSourceIdsUi) {
            ExchangisDataSourceIdUI source = dataSourceIdsUi.getSource();
            if (null != source) {
                String type = source.getType();
                ExchangisDataSourceDefinition exchangisSourceDataSource = this.context.getExchangisDsDefinition(type);
                if (null != exchangisSourceDataSource) {
                    sourceParamConfigs = exchangisSourceDataSource.getDataSourceParamConfigs().stream().filter(
                            i -> i.getConfigDirection().equals(content.getEngine() + "-SOURCE") || "SOURCE".equalsIgnoreCase(i.getConfigDirection())).collect(Collectors.toList());
                }
            }

            ExchangisDataSourceIdUI sink = dataSourceIdsUi.getSink();
            if (null != sink) {
                String type = sink.getType();
                ExchangisDataSourceDefinition exchangisSinkDataSource = this.context.getExchangisDsDefinition(type);
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
        ExchangisDataSourceParamsUI paramsUI = buildDataSourceParamsUI(dataSourceIdsUI, content);

        // ----------- 构建 dataSourceTransformsUI
        ExchangisJobTransformsContent transforms = content.getTransforms();

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
                    if (Objects.nonNull(value)) {
                        if (!(value instanceof String) || StringUtils.isNotBlank(String.valueOf(value))) {
                            String json = Json.toJson(value, null);
                            if (StringUtils.isNotBlank(json)) {
                                mapElement = Json.fromJson(json,
                                        Map.class, String.class, Object.class);
                            }
                        }
                    }
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

    /**
     * Parse data source id ui
     * @param jobDataSource data source
     * @return ui
     */
    private ExchangisDataSourceIdUI parseDataSourceIdUi(String requestUser, String idValue,
                                                        ExchangisJobDataSourcesContent.ExchangisJobDataSource jobDataSource){
        ExchangisDataSourceIdUI ui = new ExchangisDataSourceIdUI(jobDataSource);
        if (StringUtils.isNotBlank(idValue)) {
            String[] split = idValue.trim().split("\\.");
            ui.setType(split[0]);
            ui.setId(split[1]);
            ui.setDb(split[2]);
            ui.setTable(split[3]);
        }
        if (StringUtils.isBlank(ui.getDs())){
            String operator = StringUtils.isNotBlank(ui.getCreator())? ui.getCreator() :
                    GlobalConfiguration.getAdminUser();
            if (StringUtils.isBlank(operator)){
                operator = requestUser;
            }
            String finalOperator = operator;
            Optional.ofNullable(this.context.getExchangisDsDefinition(ui.getType())).ifPresent(o -> {
                try {
                    ExchangisDataSourceDetail dataSourceInfo = getDataSource(finalOperator, Long.parseLong(ui.getId()));
                    if (Objects.nonNull(dataSourceInfo)) {
                        String name = dataSourceInfo.getDataSourceName();
                        ui.setDs(name);
                        ui.setName(name);
                        ui.setCreator(dataSourceInfo.getCreateUser());
                    }
                } catch (ErrorException e) {
                    // Ignore
                }
            });
        }
        return ui;
    }
}
