package com.webank.wedatasphere.exchangis.engine.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Engine settings
 */
public class EngineSettings {

    private static final Logger LOG = LoggerFactory.getLogger(EngineSettings.class);
    /**
     * ID
     */
    private String id;

    /**
     * Engine name: engine_name
     */
    private String name;

    /**
     * Description: engine_desc
     */
    private String description;

    /**
     * Settings: engine_settings_value
     */
    @JsonIgnoreProperties
    private String settings;
    /**
     * Direction: engine_direction => hdfs->local,mysql->hdfs,mysql->hdfs
     */
    @JsonIgnoreProperties
    private String direction;

    /**
     * Resource loader class: res_loader_class
     */
    private String resourceLoaderClass;

    /**
     * Resource uploader class: res_uploader_class
     */
    private String resourceUploaderClass;

    /**
     * Direct rules
     */
    private final List<Direction> directionRules = new ArrayList<>();

    /**
     * Setting map
     */
    private final Map<String, Object> settingsMap = new HashMap<>();


    public List<Direction> getDirectionRules(){
        if (directionRules.isEmpty() && StringUtils.isNotBlank(direction)){
            synchronized (directionRules) {
                if (directionRules.isEmpty()) {
                    String[] directs = direction.split(",");
                    for (String direct : directs) {
                        String[] parts = direct.trim().split("->");
                        if (parts.length == 2) {
                            directionRules.add(new Direction(parts[0].trim(), parts[1].trim()));
                        }
                    }
                }
            }
        }
        return directionRules;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getSettingsMap(){
        if (settingsMap.isEmpty() && StringUtils.isNotBlank(settings)){
            synchronized (settingsMap){
                if (settingsMap.isEmpty()){
                    try {
                        settingsMap.putAll(JsonUtils.jackson().reader().readValue(settings, Map.class));
                    }catch(Exception e){
                        // Ignore
                        LOG.warn("Fail to load engine settings properties", e);
                    }
                }
            }
        }
        return settingsMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getResourceLoaderClass() {
        return resourceLoaderClass;
    }

    public void setResourceLoaderClass(String resourceLoaderClass) {
        this.resourceLoaderClass = resourceLoaderClass;
    }

    public String getResourceUploaderClass() {
        return resourceUploaderClass;
    }

    public void setResourceUploaderClass(String resourceUploaderClass) {
        this.resourceUploaderClass = resourceUploaderClass;
    }


    public static class Direction{
        /**
         * Source type
         */
        private final String source;

        /**
         * Sink type
         */
        private final String sink;

        public Direction(String source, String sink){
            this.source = source;
            this.sink = sink;
        }

        public String getSource() {
            return source;
        }

        public String getSink() {
            return sink;
        }
    }
}
