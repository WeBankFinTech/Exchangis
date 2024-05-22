package com.webank.wedatasphere.exchangis.datasource.core.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceType;

/**
 * string: HIVE.ID.DB.TABLE
 * json: {
 *  "type": "HIVE",
 *  "id": 467,
 *  "name": "HIVE-DEMO",
 *  "database": "default",
 *  "table": "demo-test"
 * }
 */
public class ExchangisJobDataSourcesContent {

    @JsonProperty("source_id")
    private String sourceId;

    /**
     * Source ds
     */
//    private ExchangisJobDataSource source = new ExchangisJobDataSource();


    @JsonProperty("sink_id")
    private String sinkId;

    /**
     * Sink ds
     */
//    private ExchangisJobDataSource sink = new ExchangisJobDataSource();

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSinkId() {
        return sinkId;
    }

    public void setSinkId(String sinkId) {
        this.sinkId = sinkId;
    }

//    public void setSource(ExchangisJobDataSource source) {
//        this.source = source;
//    }

//    public ExchangisJobDataSource getSource() {
//        return source;
//    }

//    public void setSink(ExchangisJobDataSource sink) {
//        this.sink = sink;
//    }

//    public ExchangisJobDataSource getSink() {
//        return sink;
//    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ExchangisJobDataSource {

        /**
         * Data source type
         */
        private ExchangisDataSourceType type;

        /**
         * Data source id
         */
        private String id;

        /**
         * Data source name
         */
        private String name;

        /**
         * Database field
         */
        private String database;

        /**
         * Table field
         */
        private String table;

        /**
         * Uri field
         */
        private String uri;

        public void setType(ExchangisDataSourceType type) {
            this.type = type;
        }

        public ExchangisDataSourceType getType() {
            return type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getDatabase() {
            return database;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public String getTable() {
            return table;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getUri() {
            return uri;
        }
    }
}
