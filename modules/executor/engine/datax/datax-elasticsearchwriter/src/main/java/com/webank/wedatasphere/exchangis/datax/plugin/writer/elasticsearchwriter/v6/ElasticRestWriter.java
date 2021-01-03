package com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6;

import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.BasicDataReceiver;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6.column.ElasticColumn;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6.ElasticRestWriter.Job.DEFAULT_ENDPOINT_SPLIT;
import static com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6.ElasticRestWriter.Job.WRITE_SIZE;

/**
 * @Classname ElasticRestWriter
 * @Description TODO
 * @Date 2021/1/3 11:21
 * @Created by limeng
 */
public class ElasticRestWriter  extends Writer {
    public static class Job extends Writer.Job{
        private static final Logger log = LoggerFactory.getLogger(Job.class);

        private static final String DEFAULT_ID = "_id";
        static final String WRITE_SIZE = "WRITE_SIZE";

        static final String DEFAULT_ENDPOINT_SPLIT = ",";

        private Configuration jobConf = null;
        private String[] endPoints;

        @Override
        public void prepare() {
            String indexName = this.jobConf.getNecessaryValue(ElasticKey.INDEX_NAME, ElasticWriterErrorCode.REQUIRE_VALUE);
            String indexType = this.jobConf.getString(ElasticKey.INDEX_TYPE, "_doc");
            log.info(String.format("index:[%s], type:[%s]", indexName, indexType));
        }

        @Override
        public List<Configuration> split(int mandatoryNumber) {
            List<Configuration> configurations = new ArrayList<>();
            for( int i = 0; i < mandatoryNumber; i++){
                configurations.add(this.jobConf.clone());
            }
            return configurations;
        }

        @Override
        public void init() {
            this.jobConf = super.getPluginJobConf();
            this.validateParams();
        }

        @Override
        public void destroy() {

        }

        private void validateParams(){
            String endPoints = this.jobConf.getString(ElasticKey.ENDPOINTS);
            if(StringUtils.isBlank(endPoints)){
                throw DataXException.asDataXException(ElasticWriterErrorCode.REQUIRE_VALUE, "'endPoints(elasticUrls)' is necessary");
            }

            this.endPoints = endPoints.split(DEFAULT_ENDPOINT_SPLIT);
            this.jobConf.getNecessaryValue(ElasticKey.INDEX_NAME, ElasticWriterErrorCode.REQUIRE_VALUE);
        }
    }

    public static class Task extends Writer.Task{
        private static final Logger logger = LoggerFactory.getLogger(Task.class);

        private Configuration taskConf;
        private String indexName;
        private String typeName;
        private String columnNameSeparator = ElasticColumn.DEFAULT_NAME_SPLIT;
        private List<ElasticColumn> columns;
        private RestClient restClient;

        private ArrayList<String> batch;

        private Integer batchSize;
        private long currentBatchSizeBytes;
        private Long maxBatchSizeBytes = 5L * 1024L * 1024L;
        private int backendVersion;

        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public void startWrite(BasicDataReceiver<Object> receiver, Class<?> type) {
            logger.info("Begin to BasicDataReceiver write record to ElasticSearch");
            super.startWrite(receiver, type);
            logger.info("End to BasicDataReceiver write record to ElasticSearch");
            throw DataXException.asDataXException(ElasticWriterErrorCode.FEATURES_ERROR, "功能不支持");
        }
        @Override
        public void startWrite(RecordReceiver lineReceiver) {
            logger.info("Begin to rest write record to ElasticSearch");
            Record record = null;
            long count = 0;
            while(null != (record = lineReceiver.getFromReader())){
                String document = ElasticColumn.recordToString(record, columns, columnNameSeparator);
                String documentMetadata = "{}";
                batch.add(String.format("{ \"index\" : %s }%n%s%n", documentMetadata, document));

                currentBatchSizeBytes += document.getBytes(StandardCharsets.UTF_8).length;

                if(batch.size() >= batchSize  || currentBatchSizeBytes >= maxBatchSizeBytes){
                    flushBatch();
                }
                count += 1;
            }
            flushBatch();
            getTaskPluginCollector().collectMessage(WRITE_SIZE, String.valueOf(count));
            logger.info("End to rest write record to ElasticSearch");
        }

        private void flushBatch()  {
            if(batch.isEmpty()){
                return;
            }

            StringBuilder bulkRequest = new StringBuilder();
            for(String json:batch){
                bulkRequest.append(json);
            }
            batch.clear();
            currentBatchSizeBytes = 0;
            Response response;
            HttpEntity responseEntity;
            String endPoint = String.format(
                    "/%s/%s/_bulk",
                    indexName,
                    typeName);


            HttpEntity requestBody =
                    new NStringEntity(bulkRequest.toString(), ContentType.APPLICATION_JSON);

            try {
                response = restClient.performRequest(ElasticRestClient.getRequest("POST", endPoint, requestBody));
                responseEntity = new BufferedHttpEntity(response.getEntity());
            }catch (IOException e){
                throw DataXException.asDataXException(ElasticWriterErrorCode.BAD_CONNECT, e);
            }

            try {
                checkForErrors(responseEntity, backendVersion);
            }catch (IOException e){
                throw DataXException.asDataXException(ElasticWriterErrorCode.BULK_REQ_ERROR, e);
            }

        }

        @Override
        public void init() {
            this.taskConf = super.getPluginJobConf();
            indexName = this.taskConf.getString(ElasticKey.INDEX_NAME);
            typeName = this.taskConf.getString(ElasticKey.INDEX_TYPE, ElasticRestHighClient.MAPPING_TYPE_DEFAULT);
            columnNameSeparator = this.taskConf.getString(ElasticKey.COLUMN_NAME_SEPARATOR, ElasticColumn.DEFAULT_NAME_SPLIT);
            batchSize = this.taskConf.getInt(ElasticKey.BULK_ACTIONS, 10000);
            batch = new ArrayList<>();


            columns = JSON.parseObject(this.taskConf.getString(ElasticKey.PROPS_COLUMN), new TypeReference<List<ElasticColumn>>(){
            });


            String[] endPoints = this.taskConf.getString(ElasticKey.ENDPOINTS).split(DEFAULT_ENDPOINT_SPLIT);
            restClient = ElasticRestClient.createClient(endPoints);

            backendVersion = getBackendVersion();
        }

        @Override
        public void destroy() {
            if(null != restClient){
                try {
                    restClient.close();
                } catch (IOException e) {
                    throw DataXException.asDataXException(ElasticWriterErrorCode.CLOSE_EXCEPTION, e);
                }
            }
        }

        private void checkForErrors(HttpEntity responseEntity, int backendVersion) throws IOException {
            JsonNode searchResult = parseResponse(responseEntity);
            boolean errors = searchResult.path("errors").asBoolean();
            if (errors) {
                StringBuilder errorMessages =
                        new StringBuilder("Error writing to Elasticsearch, some elements could not be inserted:");
                JsonNode items = searchResult.path("items");
                //some items present in bulk might have errors, concatenate error messages
                for (JsonNode item : items) {

                    String errorRootName = "";
                    if (backendVersion == 2) {
                        errorRootName = "create";
                    } else if (backendVersion == 5 || backendVersion == 6) {
                        errorRootName = "index";
                    }
                    JsonNode errorRoot = item.path(errorRootName);
                    JsonNode error = errorRoot.get("error");
                    if (error != null) {
                        String type = error.path("type").asText();
                        String reason = error.path("reason").asText();
                        String docId = errorRoot.path("_id").asText();
                        errorMessages.append(String.format("%nDocument id %s: %s (%s)", docId, reason, type));
                        JsonNode causedBy = error.get("caused_by");
                        if (causedBy != null) {
                            String cbReason = causedBy.path("reason").asText();
                            String cbType = causedBy.path("type").asText();
                            errorMessages.append(String.format("%nCaused by: %s (%s)", cbReason, cbType));
                        }
                    }
                }

                throw new IOException(errorMessages.toString());
            }
        }

        private int getBackendVersion(){
            try{
                Response response = restClient.performRequest(ElasticRestClient.getRequest("GET", ""));
                JsonNode jsonNode = parseResponse(response.getEntity());
                int backendVersion =
                        Integer.parseInt(jsonNode.path("version").path("number").asText().substring(0, 1));
                checkArgument(
                        (backendVersion == 2 || backendVersion == 5 || backendVersion == 6 || backendVersion==7),
                        "The Elasticsearch version to connect to is %s.x. "
                                + "This version of the ElasticsearchIO is only compatible with "
                                + "Elasticsearch v6.x, v5.x and v2.x",
                        backendVersion);
                return backendVersion;

            } catch (IOException e) {
                throw DataXException.asDataXException(ElasticWriterErrorCode.ES_VERSION, e);
            }
        }

        private static  JsonNode parseResponse(HttpEntity responseEntity) throws IOException {
            return mapper.readValue(responseEntity.getContent(), JsonNode.class);
        }

    }
}
