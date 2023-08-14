package com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping;

import com.webank.wedatasphere.exchangis.datasource.core.domain.MetaColumn;
import com.webank.wedatasphere.exchangis.datasource.core.exception.ExchangisDataSourceException;
import com.webank.wedatasphere.exchangis.datasource.core.service.MetadataInfoService;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobException;
import com.webank.wedatasphere.exchangis.job.exception.ExchangisJobExceptionCode;
import com.webank.wedatasphere.exchangis.job.server.mapper.JobTransformRuleDao;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRule;
import com.webank.wedatasphere.exchangis.job.server.render.transform.Transformer;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformRequestVo;
import com.webank.wedatasphere.exchangis.job.server.render.transform.TransformSettings;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.FieldColumn;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldAllMatchIgnoreCaseStrategy;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldAllMatchStrategy;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldColumnMatch;
import com.webank.wedatasphere.exchangis.job.server.render.transform.field.mapping.match.FieldMatchStrategy;
import com.webank.wedatasphere.exchangis.job.server.utils.SpringContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Transform of field mapping
 */

public class FieldMappingTransformer implements Transformer {

    /**
     * Metadata info service
     */
    private MetadataInfoService metadataInfoService;

    /**
     * Rules fusion
     */
    private final FieldMappingRulesFusion rulesFusion;

    /**
     * Rule dao
     */
    private final JobTransformRuleDao transformRuleDao;

    public FieldMappingTransformer(FieldMappingRulesFusion rulesFusion, JobTransformRuleDao transformRuleDao){
        this.rulesFusion = rulesFusion;
        this.transformRuleDao = transformRuleDao;
    }

    @Override
    public String name() {
        return TransformRule.Types.MAPPING.name();
    }

    @Override
    public TransformSettings getSettings(TransformRequestVo requestVo) {
        // Get the mapping rules
        FieldMappingRule sourceRule = getFieldMappingRule(requestVo.getSourceTypeId(), requestVo.getEngine(), TransformRule.Direction.SOURCE.name());
        if (Objects.isNull(sourceRule)){
            sourceRule = new FieldMappingRuleEntity(TransformRule.Types.MAPPING, null);
        }
        sourceRule.setDirection(TransformRule.Direction.SOURCE.name());
        FieldMappingRule sinkRule = getFieldMappingRule(requestVo.getSinkTypeId(), requestVo.getEngine(), TransformRule.Direction.SINK.name());
        if (Objects.isNull(sinkRule)){
            sinkRule = new FieldMappingRuleEntity(TransformRule.Types.MAPPING, null);
            sinkRule.setFieldMatchStrategyName(FieldAllMatchIgnoreCaseStrategy.ALL_MATCH_IGNORE_CASE);
        }
        sinkRule.setDirection(TransformRule.Direction.SINK.name());
        FieldMappingRule fusedRule = this.rulesFusion.fuse(sourceRule, sinkRule);
        return getFieldMappingSettings(fusedRule, requestVo);
    }

    /**
     * Get field mapping settings
     * @param rule rule
     * @param requestVo request Vo
     * @return field mapping settings
     */
    private FieldMappingSettings getFieldMappingSettings(FieldMappingRule rule, TransformRequestVo requestVo) {
        FieldMappingSettings settings = new FieldMappingSettings();
        settings.setAddEnable(rule.isFieldAddEnable());
        settings.setTransformEnable(rule.isFieldTransformEnable());
        // Get raw meta columns
        List<FieldColumn> sourceColumns = new ArrayList<>();
        try {
            List<MetaColumn> metaColumns = getOrLoadMetadataInfoService().
                    getColumns(requestVo.getOperator(), requestVo.getSourceDataSourceId(),
                            requestVo.getSourceDataBase(), requestVo.getSourceTable());
            boolean editable = rule.getFieldEditEnableRuleItem().getOrDefault(TransformRule.Direction.SOURCE.name(), true);
            for (int i = 0; i < metaColumns.size(); i++) {
                MetaColumn metaColumn = metaColumns.get(i);
                sourceColumns.add(new FieldColumnWrapper(metaColumn.getName(), metaColumn.getType(), i, editable));
            }
        } catch (ExchangisDataSourceException e) {
            throw new ExchangisJobException.Runtime(ExchangisJobExceptionCode.RENDER_TRANSFORM_ERROR.getCode(), "Fail to get source meta columns in generating field mapping settings", e);
        }
        settings.setSourceFields(sourceColumns);
        List<FieldColumn> sinkColumns = new ArrayList<>();
        try {
            List<MetaColumn> metaColumns = getOrLoadMetadataInfoService().
                    getColumns(requestVo.getOperator(), requestVo.getSinkDataSourceId(),
                            requestVo.getSinkDataBase(), requestVo.getSinkTable());
            boolean editable = rule.getFieldEditEnableRuleItem().getOrDefault(TransformRule.Direction.SINK.name(), true);
            for (int i = 0; i < metaColumns.size(); i++) {
                MetaColumn metaColumn = metaColumns.get(i);
                sinkColumns.add(new FieldColumnWrapper(metaColumn.getName(), metaColumn.getType(), i, editable));
            }
        } catch (ExchangisDataSourceException e) {
            throw new ExchangisJobException.Runtime(ExchangisJobExceptionCode.RENDER_TRANSFORM_ERROR.getCode(), "Fail to get sink meta columns in generating field mapping settings", e);
        }
        settings.setSinkFields(sinkColumns);
        FieldMatchStrategy matchStrategy = rule.getFieldMatchStrategy();
        if (Objects.isNull(matchStrategy)) {
            // Just use the all match strategy
            matchStrategy = this.rulesFusion.getFieldMatchStrategyFactory().getOrCreateStrategy(FieldAllMatchStrategy.ALL_MATCH);
        }
        boolean positive = TransformRule.Direction.SOURCE.name().equals(rule.getDirection());
        List<FieldColumn> dependColumns = positive ? sourceColumns : sinkColumns;
        List<FieldColumn> searchColumns = positive ? sinkColumns : sourceColumns;
        List<FieldColumnMatch> fieldColumnMatches = matchStrategy.match(dependColumns, searchColumns, rule.isFieldUnMatchIgnore());
        // Covert to field mappings
        if (positive) {
            fieldColumnMatches.forEach(fieldColumnMatch -> settings.getDeductions()
                    .add(new FieldMappingColumn(fieldColumnMatch.getLeftMatch(), fieldColumnMatch.getRightMatch(), rule.isFieldDeleteEnable())));
        } else{
            fieldColumnMatches.forEach(fieldColumnMatch -> settings.getDeductions()
                    .add(new FieldMappingColumn(fieldColumnMatch.getRightMatch(), fieldColumnMatch.getLeftMatch(), rule.isFieldDeleteEnable())));
        }
        return settings;
    }
    /**
     * Get field mapping rule
     * @param dataSourceType data source type
     * @param engine engine type
     * @param direction direction
     * @return rule
     */
    private FieldMappingRule getFieldMappingRule(String dataSourceType, String engine, String direction){
        AtomicReference<TransformRule> fieldMappingRule = new AtomicReference<>();
        AtomicInteger maxFraction = new AtomicInteger(0);
        this.transformRuleDao.getTransformRules(TransformRule.Types.MAPPING.name(), dataSourceType).forEach(rule -> {
                    int fraction = rule.matchInFraction(dataSourceType, engine, direction);
                    if (fraction > maxFraction.get()){
                        fieldMappingRule.set(rule);
                        maxFraction.set(fraction);
                    }
                });
        return Objects.nonNull(fieldMappingRule.get())? fieldMappingRule.get().toRule(FieldMappingRuleEntity.class) : null;
    }

    private MetadataInfoService getOrLoadMetadataInfoService(){
        if (Objects.isNull(this.metadataInfoService)){
            this.metadataInfoService = SpringContextHolder.getBean(MetadataInfoService.class);
        }
        return this.metadataInfoService;
    }
    public static class FieldColumnWrapper extends FieldColumn{
        /**
         * Edit enable switch
         */
        private boolean fieldEditable;

        public FieldColumnWrapper(){

        }

        public FieldColumnWrapper(String name, String type, int fieldIndex, boolean fieldEditable){
            super(name, type, fieldIndex);
            this.fieldEditable = fieldEditable;
        }

        public boolean isFieldEditable() {
            return fieldEditable;
        }

        public void setFieldEditable(boolean fieldEditable) {
            this.fieldEditable = fieldEditable;
        }
    }

}
