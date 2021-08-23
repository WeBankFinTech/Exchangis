package com.webank.wedatasphere.exchangis.datasource.core.ui.viewer.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.webank.wedatasphere.exchangis.dao.domain.ExchangisJobParamConfig;
import com.webank.wedatasphere.exchangis.datasource.core.ui.*;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobDataSourcesContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobParamsContent;
import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobTransformsItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ExchangisDataSourceUIViewBuilder {
    private static final ObjectMapper mapper = new ObjectMapper();

    private ExchangisDataSourceUIViewBuilder() {}

    public static ExchangisDataSourceIdsUI getDataSourceIdsUI(ExchangisJobDataSourcesContent dataSources) {
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
            source.setDb(split[2]);
            source.setTable(split[3]);

            ids.setSource(source);
        }

        if (!Strings.isNullOrEmpty(sinkId)) {
            String[] split = sinkId.trim().split("\\.");
            ExchangisDataSourceIdUI sink = new ExchangisDataSourceIdUI();
            sink.setType(split[0]);
            sink.setId(split[1]);
            sink.setDb(split[2]);
            sink.setTable(split[3]);

            ids.setSink(sink);
        }
        return ids;
    }

    public static List<ElementUI> getDataSourceParamsUI(List<ExchangisJobParamConfig> paramConfigs) {
        List<ElementUI> uis = new ArrayList<>();
        if (!Objects.isNull(paramConfigs) && !paramConfigs.isEmpty()) {
            for (ExchangisJobParamConfig cfg : paramConfigs) {
                ElementUI ui = fillElementUIValue(cfg, "");
                uis.add(ui);
            }
        }
        return uis;
    }

    public static List<ElementUI> getJobDataSourceParamsUI(List<ExchangisJobParamConfig> paramConfigs, List<ExchangisJobParamsContent.ExchangisJobParamsItem> paramsList) {
        List<ElementUI> uis = new ArrayList<>();
        if (!Objects.isNull(paramConfigs) && !paramConfigs.isEmpty()) {
            for (ExchangisJobParamConfig cfg : paramConfigs) {
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

    public static ExchangisDataSourceTransformsUI getDataSourceTransFormsUI(List<ExchangisJobTransformsItem> items) {
        TableElementUI sourceTable = new TableElementUI();
        TableElementUI sinkTable = new TableElementUI();

        if (Objects.isNull(items)) {
            ExchangisDataSourceTransformsUI ui = new ExchangisDataSourceTransformsUI();
            ui.setSource(sourceTable);
            ui.setSink(sinkTable);
            return ui;
        }
        TableRowElementUI sourceRow;
        TableRowElementUI sinkRow;
        int rowIdx = 1;
        for (ExchangisJobTransformsItem item : items) {
            // -------------- source table
            sourceRow = new TableRowElementUI();
            sourceRow.setSort(rowIdx);

            TableCellElementUI sourceFieldNameCell = new TableCellElementUI();
            sourceFieldNameCell.setField("sourceFieldName");
            sourceFieldNameCell.setSort(1);
            sourceFieldNameCell.setValue(item.getSourceFieldName());

            TableCellElementUI sourceFieldTypeCell = new TableCellElementUI();
            sourceFieldTypeCell.setField("sourceFieldType");
            sourceFieldTypeCell.setSort(2);
            sourceFieldTypeCell.setValue(item.getSourceFieldType());

            sourceRow.addCell(sourceFieldNameCell);
            sourceRow.addCell(sourceFieldTypeCell);

            sourceTable.addRow(sourceRow);

            // --------------- sink table
            sinkRow = new TableRowElementUI();
            sinkRow.setSort(rowIdx);

            TableCellElementUI sinkFieldNameCell = new TableCellElementUI();
            sinkFieldNameCell.setField("sinkFieldName");
            sinkFieldNameCell.setSort(1);
            sinkFieldNameCell.setValue(item.getSinkFieldName());

            TableCellElementUI sinkFieldTypeCell = new TableCellElementUI();
            sinkFieldTypeCell.setField("sinkFieldType");
            sinkFieldTypeCell.setSort(2);
            sinkFieldTypeCell.setValue(item.getSinkFieldType());

            sinkRow.addCell(sinkFieldNameCell);
            sinkRow.addCell(sinkFieldTypeCell);

            sinkTable.addRow(sinkRow);

            rowIdx++;
        }

        ExchangisDataSourceTransformsUI ui = new ExchangisDataSourceTransformsUI();
        ui.setSource(sourceTable);
        ui.setSink(sinkTable);
        return ui;
    }

    private static ExchangisJobParamsContent.ExchangisJobParamsItem getJobParamsItem(String configKey, List<ExchangisJobParamsContent.ExchangisJobParamsItem> sources) {
        for (ExchangisJobParamsContent.ExchangisJobParamsItem item : sources) {
            if (item.getConfigKey().equalsIgnoreCase(configKey)) {
                return item;
            }
        }
        return null;
    }

    public static ElementUI fillElementUIValue(ExchangisJobParamConfig config, String value) {
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


    private static OptionElementUI fillOptionElementUIValue(ExchangisJobParamConfig config, String value) {
//        String valueType = config.getValueType();
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

    private static InputElementUI fillInputElementUIValue(ExchangisJobParamConfig config, String value) {
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
