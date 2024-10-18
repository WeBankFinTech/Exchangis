package com.webank.wedatasphere.exchangis.job.domain.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.exchangis.common.domain.ExchangisDataSource;
import com.webank.wedatasphere.exchangis.common.enums.ExchangisDataSourceType;
import org.apache.commons.lang3.StringUtils;

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
         * Model id
         */
        private Long modelId;

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

        /**
         * Model id
         * @return model id
         */
        public Long getModelId() {
            return modelId;
        }

        public void setModelId(Long modelId) {
            this.modelId = modelId;
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

        public boolean matchIdentify(String sourceSinkId) {
            if (StringUtils.isNotBlank(sourceSinkId)) {
                return (StringUtils.isNotBlank(this.uri) && this.uri.contains(sourceSinkId)) ||
                        this.db.contains(sourceSinkId) || this.table.contains(sourceSinkId);
            }
            return false;
        }
    }

    public boolean matchIdentify(String dataSrcType, String dataDestType, String sourceSinkId) {
        String sourceId = this.getSourceId();
        ExchangisJobDataSourcesContent.ExchangisJobDataSource source = this.getSource();
        String sinkId = this.getSinkId();
        ExchangisJobDataSourcesContent.ExchangisJobDataSource sink = this.getSink();

        if (StringUtils.isNotBlank(dataSrcType)) {
            if (containsOrMatchesType(sourceId, source, dataSrcType)) {
                return true;
            }
        }
        if (StringUtils.isNotBlank(dataDestType)) {
            if (containsOrMatchesType(sinkId, sink, dataDestType)) {
                return true;
            }
        }
        if (StringUtils.isNotBlank(sourceSinkId)) {
            if (StringUtils.isNotBlank(sourceId) && (sourceId.contains(sourceSinkId) || sinkId.contains(sourceSinkId))) {
                return true;
            } else if (StringUtils.isBlank(sourceId) && (source.matchIdentify(sourceSinkId) || sink.matchIdentify(sourceSinkId))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsOrMatchesType(String id, ExchangisJobDataSourcesContent.ExchangisJobDataSource dataSource, String type) {
        return StringUtils.isNotBlank(id) && id.contains(type) || StringUtils.equals(type, dataSource.getType());
    }
}
