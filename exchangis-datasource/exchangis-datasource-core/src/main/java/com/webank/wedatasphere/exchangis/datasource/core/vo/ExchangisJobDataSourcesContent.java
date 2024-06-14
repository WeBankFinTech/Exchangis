package com.webank.wedatasphere.exchangis.datasource.core.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.exchangis.common.domain.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.datasource.core.domain.ExchangisDataSourceType;

import java.util.Objects;

/**
 * string: HIVE.ID.DB.TABLE
 * json: {
 *  "type": "HIVE",
 *  "id": 467,
 *  "name": "HIVE-DEMO",
 *  "creator": "hadoop",
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
    @JsonProperty("source")
    private ExchangisJobDataSource source = new ExchangisJobDataSource();

    @JsonProperty("sink_id")
    private String sinkId;

    /**
     * Sink ds
     */
    @JsonProperty("sink")
    private ExchangisJobDataSource sink = new ExchangisJobDataSource();

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

    public void setSource(ExchangisJobDataSource source) {
        this.source = source;
    }

    public ExchangisJobDataSource getSource() {
        return source;
    }

    public void setSink(ExchangisJobDataSource sink) {
        this.sink = sink;
    }

    public ExchangisJobDataSource getSink() {
        return sink;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ExchangisJobDataSource implements ExchangisDataSource {

        /**
         * Data source type
         */
        private ExchangisDataSourceType type;

        /**
         * Data source id
         */
        private Long id;

        /**
         * Data source name
         */
        private String name;

        /**
         * Data source creator
         */
        private String creator;

        /**
         * Database field
         */
        private String db;

        /**
         * Table field
         */
        private String table;

        /**
         * Uri field
         */
        private String uri;


        @Override
        public void setType(String type) {
            this.type = ExchangisDataSourceType.valueOf(type);
        }


        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public String getType() {
            return Objects.nonNull(type) ? type.name() : null;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String getCreator() {
            return this.creator;
        }

        @Override
        public void setCreator(String creator) {
            this.creator = creator;
        }

        @Override
        public String getDesc() {
            return null;
        }

        @Override
        public void setDesc(String desc) {

        }


        public void setDb(String db) {
            this.db = db;
        }

        public String getDb() {
            return db;
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
