package com.webank.wedatasphere.exchangis.job.server.render.transform;


import com.webank.wedatasphere.exchangis.datasource.core.utils.Json;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class TransformRule {

    private static final Logger LOG = LoggerFactory.getLogger(TransformRule.class);
    /**
     * Hold the type class
     */
    protected static final Map<String, Class<? extends TransformRule>> typeClasses = new HashMap<>();

    public enum Types{
        DEF(TransformTypes.NONE), MAPPING(TransformTypes.MAPPING), PROCESSOR(TransformTypes.PROCESSOR);
        final TransformTypes type;
        Types(TransformTypes type){
            this.type = type;
        }

        public TransformTypes getType() {
            return type;
        }
    }

    public enum Direction{
        NONE, SOURCE, SINK
    }

    /**
     * Rule id
     */
    private Long id;

    /**
     * Rule name
     */
    private String ruleName;

    /**
     * Rule type
     */
    protected String ruleType;

    /**
     * Data source type
     */
    protected String dataSourceType;

    /**
     * Engine type
     */
    protected String engineType;

    protected String direction = Direction.NONE.name();
    /**
     * Rule source
     */
    protected String ruleSource;

    protected Date createTime;

    public TransformRule(){

    }

    public TransformRule(Types type, String ruleSource){
        this.ruleType = type.name();
        this.ruleSource = ruleSource;
    }

    @SuppressWarnings("unchecked")
    public <T extends TransformRule>T toRule(Class<T> ruleType){
        Class<?> ruleClass = typeClasses.get(this.ruleType);
        if (Objects.isNull(ruleClass) || !ruleType.isAssignableFrom(ruleClass)){
            LOG.warn("Cannot convert to rule: [{}] with rule type: [{}]",
                    ruleType.getCanonicalName(), this.ruleType);
        } else {
            try {
                Constructor<?> constructor = ruleClass.getConstructor(Types.class, String.class);
                return (T) constructor.newInstance(Types.valueOf(this.ruleType), this.ruleSource);
            } catch (NoSuchMethodException e) {
                LOG.warn("Not find the suitable constructor for rule class {}", ruleClass.getSimpleName(), e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                LOG.warn("Fail to construct rule class {} with: [{}, {}]", ruleClass.getSimpleName(),
                        this.ruleType, this.ruleSource, e);
            }
        }
        return null;
    }

    /**
     * Convert rule source to map
     * @return map
     */
    public Map<String, Object> getRuleSourceAsMap(){
        Map<String, Object> mapResult = null;
        if (StringUtils.isNotBlank(this.ruleSource)){
            mapResult = Json.fromJson(this.ruleSource, Map.class);
        }
        return Objects.nonNull(mapResult)? mapResult : Collections.emptyMap();
    }


    public int matchInFraction(String dataSourceType, String engineType, String direction){
        int fraction = Objects.nonNull(dataSourceType) && dataSourceType.equals(this.dataSourceType) ? 1 : 0;
        if (fraction > 0){
            if (Objects.nonNull(this.engineType)){
                if (this.engineType.equals(engineType)) {
                    fraction += 2;
                } else {
                    fraction = 0;
                }
            }
        }
        if (fraction > 0){
            if (Objects.nonNull(this.direction) && !this.direction.equals(Direction.NONE.name())){
                if (this.direction.equals(direction)) {
                    fraction ++;
                } else {
                    fraction = 0;
                }
            }
        }
        return fraction;
    }
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getRuleSource() {
        return ruleSource;
    }

    public void setRuleSource(String ruleSource) {
        this.ruleSource = ruleSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
