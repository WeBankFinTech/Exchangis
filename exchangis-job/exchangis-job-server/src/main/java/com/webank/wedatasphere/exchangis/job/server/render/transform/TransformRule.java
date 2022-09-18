package com.webank.wedatasphere.exchangis.job.server.render.transform;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TransformRule {

    private static final Logger LOG = LoggerFactory.getLogger(TransformRule.class);
    /**
     * Hold the type class
     */
    protected static final Map<String, Class<? extends TransformRule>> typeClasses = new HashMap<>();

    public enum Types{
        DEF, MAPPING, PROCESSOR
    }

    public enum Direction{
        NONE, SOURCE, SINK
    }
    protected String direction = Direction.NONE.name();

    protected String type;

    /**
     * Engine type
     */
    protected String engineType;

    /**
     * Rule source
     */
    protected String source;

    public TransformRule(){

    }

    public TransformRule(Types type, String ruleSource){
        this.type = type.name();
        this.source = ruleSource;
    }

    @SuppressWarnings("unchecked")
    public <T extends TransformRule>T toRule(Class<T> ruleType){
        Class<?> ruleClass = typeClasses.get(this.type);
        if (Objects.isNull(ruleClass) || !ruleType.isAssignableFrom(ruleClass)){
            LOG.warn("Cannot convert to rule: [{}] with rule type: [{}]",
                    ruleType.getCanonicalName(), this.type);
        } else {
            try {
                Constructor<?> constructor = ruleClass.getConstructor(Types.class, String.class);
                return (T) constructor.newInstance(Types.valueOf(this.type), this.source);
            } catch (NoSuchMethodException e) {
                LOG.warn("Not find the suitable constructor for rule class {}", ruleClass.getSimpleName(), e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                LOG.warn("Fail to construct rule class {} with: [{}, {}]", ruleClass.getSimpleName(),
                        this.type, this.source, e);
            }
        }
        return null;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
