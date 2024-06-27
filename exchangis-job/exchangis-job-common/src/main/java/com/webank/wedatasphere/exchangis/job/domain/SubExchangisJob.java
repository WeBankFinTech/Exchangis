package com.webank.wedatasphere.exchangis.job.domain;

import com.webank.wedatasphere.exchangis.job.domain.params.JobParam;
import com.webank.wedatasphere.exchangis.job.domain.params.JobParamSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * For each sub job entity,
 * will have parameter set which divided into different realm
 */
public class SubExchangisJob extends GenericExchangisJob {


    protected String sourceType;

    protected String sinkType;

    public static final String REALM_JOB_SETTINGS = "job.realm.settings";

    public static final String REALM_JOB_DATA_SOURCE = "job.realm.data-source";

    public static final String REALM_JOB_CONTENT_SOURCE = "job.realm.content.source";

    public static final String REALM_JOB_CONTENT_SINK = "job.realm.content.sink";

//    public static final String REALM_JOB_COLUMN_MAPPING = "job.realm.column-mappings";

    /**
     * Realm params set
     */
    private final Map<String, JobParamSet> realmParamSet = new ConcurrentHashMap<>();

    /**
     * Source columns
     */
    private final List<ColumnDefine> sourceColumns = new ArrayList<>();

    /**
     * Sink columns
     */
    private final List<ColumnDefine> sinkColumns = new ArrayList<>();

    /**
     * Functions
     */
    private final List<ColumnFunction> columnFunctions = new ArrayList<>();

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSinkType() {
        return sinkType;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }

    /**
     * Add
     * @param realm realm info
     * @param paramSet param set
     */
    public void addRealmParams(String realm, JobParamSet paramSet){
        realmParamSet.put(realm, paramSet);
    }

    /**
     * Get
     * @param realm realm info
     * @return param set
     */
    public JobParamSet getRealmParams(String realm){
        return  realmParamSet.get(realm);
    }

    /**
     * Get and convert to map
     * @param realm realm info
     * @return map
     */
    public Map<String, Object> getParamsToMap(String realm, boolean isTemp){
        JobParamSet jobParamSet = getRealmParams(realm);
        return jobParamSet.toList(isTemp).stream().collect(
                Collectors.toMap(JobParam::getStrKey, JobParam::getValue));
    }

    /**
     * Get all and convert to map
     * @return map
     */
    public Map<String, Object> getParamsToMap(){
        return realmParamSet.values().stream().flatMap(realmParam -> realmParam.toList().stream())
                .collect(Collectors.toMap(JobParam::getStrKey, JobParam::getValue));
    }

    public Map<String, Object> getParamsToMap(boolean isTemp){
        return realmParamSet.values().stream().flatMap(realmParam -> realmParam.toList(isTemp).stream())
                .collect(Collectors.toMap(JobParam::getStrKey, JobParam::getValue, (left, right) -> right));
    }

    public List<ColumnDefine> getSourceColumns() {
        return sourceColumns;
    }

    public List<ColumnDefine> getSinkColumns() {
        return sinkColumns;
    }

    public List<ColumnFunction> getColumnFunctions() {
        return columnFunctions;
    }

    /**
     * Column definition
     */
    public static class ColumnDefine{

        /**
         * Column name
         */
        private String name;

        /**
         * Column type
         */
        private String type;

        /**
         * Raw column type
         */
        private String rawType;

        /**
         * Column index
         */
        private Integer index;

        public ColumnDefine(){

        }

        public ColumnDefine(String name, String type){
            this.name = name;
            this.type = type;
        }

        public ColumnDefine(String name, String type, Integer index){
            this.name = name;
            this.type = type;
            this.index = index;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getRawType() {
            return rawType;
        }

        public void setRawType(String rawType) {
            this.rawType = rawType;
        }
    }

    /**
     * Column define with precision and scale
     */
    public static class DecimalColumnDefine extends ColumnDefine{

        private static final int DEFAULT_PRECISION = 38;

        private static final int DEFAULT_SCALE = 18;

        /**
         * Precision
         */
        private int precision = DEFAULT_PRECISION;

        /**
         * Scale
         */
        private int scale = DEFAULT_SCALE;

        public DecimalColumnDefine(){

        }

        public DecimalColumnDefine(String name, String type, Integer index, int precision, int scale){
            super(name, type, index);
            this.precision = precision;
            this.scale = scale;
        }

        public int getPrecision() {
            return precision;
        }

        public void setPrecision(int precision) {
            this.precision = precision;
        }

        public int getScale() {
            return scale;
        }

        public void setScale(int scale) {
            this.scale = scale;
        }
    }
    /**
     * Column function
     */
    public static class ColumnFunction{

        private Integer index;
        /**
         * Function name
         */
        private String name;

        /**
         * Function params
         */
        private List<String> params = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }
}
