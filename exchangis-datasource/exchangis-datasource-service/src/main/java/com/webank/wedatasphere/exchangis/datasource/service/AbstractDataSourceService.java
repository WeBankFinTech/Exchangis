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
import com.webank.wedatasphere.exchangis.datasource.core.ui.*;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.DefaultDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.ExchangisDataSourceUIViewer;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobDataSourcesContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobInfoContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsContent;
import com.webank.wedatasphere.exchangis.datasource.dto.GetDataSourceInfoResultDTO;
import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient;
import com.webank.wedatasphere.linkis.datasource.client.request.GetInfoByDataSourceIdAction;
import com.webank.wedatasphere.linkis.httpclient.response.Result;
import com.webank.wedatasphere.linkis.metadatamanager.common.Json;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class AbstractDataSourceService {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ExchangisDataSourceContext context;
    protected final ExchangisJobParamConfigMapper exchangisJobParamConfigMapper;
    protected final ExchangisJobInfoMapper exchangisJobInfoMapper;

    public AbstractDataSourceService(ExchangisDataSourceContext context, ExchangisJobParamConfigMapper exchangisJobParamConfigMapper, ExchangisJobInfoMapper exchangisJobInfoMapper) {
        this.context = context;
        this.exchangisJobParamConfigMapper = exchangisJobParamConfigMapper;
        this.exchangisJobInfoMapper = exchangisJobInfoMapper;
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
        String loginUser = Optional.ofNullable(request).isPresent() ? SecurityFilter.getLoginUsername(request) : null;
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
                    GetDataSourceInfoResultDTO dsInfo = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);
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
                    GetDataSourceInfoResultDTO dsInfo = Json.fromJson(responseBody, GetDataSourceInfoResultDTO.class);
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
                    sourceParamConfigs = exchangisSourceDataSource.getDataSourceParamConfigs().stream().filter(i -> i.getConfigDirection().equals(content.getEngine() + "-SOURCE")).collect(Collectors.toList());
                }
            }

            ExchangisDataSourceIdUI sink = dataSourceIdsUI.getSink();
            if (null != sink) {
                String type = sink.getType();
                ExchangisDataSource exchangisSinkDataSource = this.context.getExchangisDataSource(type);
                if (null != exchangisSinkDataSource) {
                    sinkParamConfigs = exchangisSinkDataSource.getDataSourceParamConfigs().stream().filter(i -> i.getConfigDirection().equals(content.getEngine() + "-SINK")).collect(Collectors.toList());
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

        List<ElementUI> jobDataSourceParamsUI1 = buildDataSourceParamsFilledValueUI(sourceParamConfigs, sourceParamsItems);
        List<ElementUI> jobDataSourceParamsUI2 = buildDataSourceParamsFilledValueUI(sinkParamConfigs, sinkParamsItems);
        ExchangisDataSourceParamsUI paramsUI = new ExchangisDataSourceParamsUI();
        paramsUI.setSources(jobDataSourceParamsUI1);
        paramsUI.setSinks(jobDataSourceParamsUI2);
        return paramsUI;
    }

    protected ExchangisDataSourceUIViewer buildAllUI(HttpServletRequest request, ExchangisJobInfo job, ExchangisJobInfoContent content) {
        // ----------- 构建 dataSourceIdsUI
        ExchangisDataSourceIdsUI dataSourceIdsUI = buildDataSourceIdsUI(request, content);

        // ----------- 构建 dataSourceParamsUI
        ExchangisDataSourceParamsUI paramsUI = buildDataSourceParamsUI(content);

        // ----------- 构建 dataSourceTransformsUI
        ExchangisJobTransformsContent transforms = content.getTransforms();
//        ExchangisDataSourceTransformsUI dataSourceTransFormsUI = ExchangisDataSourceUIViewBuilder.getDataSourceTransFormsUI(transforms);

        List<ElementUI> jobDataSourceSettingsUI = this.buildJobSettingsUI(job.getEngineType(), content);

        return new DefaultDataSourceUIViewer(content.getSubJobName(), dataSourceIdsUI, paramsUI, transforms, jobDataSourceSettingsUI);
    }


    protected List<ElementUI> buildJobSettingsUI(String jobEngineType) {
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

    protected List<ElementUI> buildJobSettingsUI(String jobEngineType, ExchangisJobInfoContent content) {
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

    protected List<ElementUI> buildDataSourceParamsUI(List<ExchangisJobParamConfig> paramConfigs) {
        List<ElementUI> uis = new ArrayList<>();
        if (!Objects.isNull(paramConfigs) && !paramConfigs.isEmpty()) {
            for (ExchangisJobParamConfig cfg : paramConfigs) {
                ElementUI ui = fillElementUIValue(cfg, "");
                uis.add(ui);
            }
        }
        return uis;
    }

    protected List<ElementUI> buildDataSourceParamsFilledValueUI(List<ExchangisJobParamConfig> paramConfigs, List<ExchangisJobParamsContent.ExchangisJobParamsItem> paramsList) {
        List<ElementUI> uis = new ArrayList<>();
        if (!Objects.isNull(paramConfigs) && !paramConfigs.isEmpty()) {
            for (ExchangisJobParamConfig cfg : paramConfigs) {
                if (Objects.isNull(paramsList) || paramsList.isEmpty()) {
                    uis.add(fillElementUIValue(cfg, ""));
                    continue;
                }
                ExchangisJobParamsContent.ExchangisJobParamsItem selectedParamItem = getJobParamsItem(cfg.getConfigKey(), paramsList);
                if (Objects.isNull(selectedParamItem)) {
                    ElementUI ui = fillElementUIValue(cfg, "");
                    uis.add(ui);
                } else {
                    ElementUI ui = fillElementUIValue(cfg, selectedParamItem.getConfigValue());
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

    private ElementUI fillElementUIValue(ExchangisJobParamConfig config, String value) {
        String uiType = config.getUiType();
        switch (uiType) {
            case ElementUI.OPTION:
                return fillOptionElementUIValue(config, value);
            case ElementUI.INPUT:
                return fillInputElementUIValue(config, value);
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
        ui.setKey(config.getConfigKey());
        ui.setField(config.getUiField());
        ui.setLabel(config.getUiLabel());
        ui.setValues(values);
        ui.setValue(value);
        ui.setSort(config.getSort());
        ui.setRequired(config.getRequired());
        ui.setUnit(config.getUnit());
        return ui;
    }

    private InputElementUI fillInputElementUIValue(ExchangisJobParamConfig config, String value) {
        InputElementUI ui = new InputElementUI();
        ui.setKey(config.getConfigKey());
        ui.setField(config.getUiField());
        ui.setLabel(config.getUiLabel());
        ui.setValue(value);
        ui.setSort(config.getSort());
        ui.setRequired(config.getRequired());
        ui.setUnit(config.getUnit());
        return ui;
    }
}
