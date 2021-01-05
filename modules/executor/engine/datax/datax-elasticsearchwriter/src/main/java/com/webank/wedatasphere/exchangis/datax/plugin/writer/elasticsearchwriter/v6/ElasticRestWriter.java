package com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6;

import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.BasicDataReceiver;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.plugin.TaskPluginCollector;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.datax.core.statistics.plugin.task.util.DirtyRecord;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.exchangis.datax.common.CryptoUtils;
import com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6.column.ElasticColumn;
import com.webank.wedatasphere.exchangis.datax.plugin.writer.elasticsearchwriter.v6.column.ElasticFieldDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        private String userName;
        private String password;

        @Override
        public void prepare() {
            String indexName = this.jobConf.getNecessaryValue(ElasticKey.INDEX_NAME, ElasticWriterErrorCode.REQUIRE_VALUE);
            String indexType = this.jobConf.getString(ElasticKey.INDEX_TYPE, "_doc");
            log.info(String.format("index:[%s], type:[%s]", indexName, indexType));

            //检查索引，创建索引，可以根据实际情况修改
            ElasticRestHighClient restClient;
            Map<String, Object> clientConfig = jobConf.getMap(ElasticKey.CLIENT_CONFIG);
            if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)){
                restClient = ElasticRestHighClient.custom(endPoints, userName,
                        password, clientConfig);
            }else{
                restClient = ElasticRestHighClient.custom(endPoints, clientConfig);
            }
            String columnNameSeparator = this.jobConf.getString(ElasticKey.COLUMN_NAME_SEPARATOR, ElasticColumn.DEFAULT_NAME_SPLIT);
            List<Object> rawColumnList = jobConf
                    .getList(ElasticKey.PROPS_COLUMN);
            List<ElasticColumn> resolvedColumnList = new ArrayList<>();
            Map<Object, Object> props = resolveColumn(restClient, indexName, indexType,
                    rawColumnList, resolvedColumnList, columnNameSeparator);
            this.jobConf.set(ElasticKey.PROPS_COLUMN, resolvedColumnList);
            //clean up
            if(jobConf.getBool(ElasticKey.CLEANUP, false) &&
                    restClient.existIndices(indexName)){
                if(!restClient.deleteIndices(indexName)){
                    throw DataXException.asDataXException(ElasticWriterErrorCode.DELETE_INDEX_ERROR, "cannot delete index:[" + indexName +"]");
                }
            }

            //if the index is not existed, create it
            restClient.createIndex(indexName, indexType, jobConf.getMap(ElasticKey.SETTINGS),
                    props);
            restClient.close();
        }

        private Map<Object, Object> resolveColumn(ElasticRestHighClient client,
                                                  String index, String type ,
                                                  List<Object> rawColumnList, List<ElasticColumn> outputColumn,
                                                  String columnNameSeparator){
            Map<Object, Object> properties;
            if(null != rawColumnList && !rawColumnList.isEmpty()) {
                //allow to custom the fields of properties
                properties = new HashMap<>(rawColumnList.size());
                rawColumnList.forEach(columnRaw -> {
                    String raw = columnRaw.toString();
                    ElasticColumn column = JSONObject
                            .parseObject(raw, ElasticColumn.class);
                    if (StringUtils.isNotBlank(column.getName()) && StringUtils.isNotBlank(column.getType())) {
                        outputColumn.add(column);
                        if (!column.getName().equals(DEFAULT_ID) && ElasticFieldDataType.valueOf(column.getType().toUpperCase())
                                != ElasticFieldDataType.ALIAS) {
                            Map property = JSONObject.parseObject(raw, Map.class);
                            property.remove(ElasticKey.PROPS_COLUMN_NAME);
                            properties.put(column.getName(), property);
                        }
                    }
                });
            }else{
                if(!client.existIndices(index)){
                    throw DataXException.asDataXException(ElasticWriterErrorCode.INDEX_NOT_EXIST,
                            "cannot get columns from index:[" + index +"]");
                }
                //get properties from index existed
                properties = client.getProps(index, type);
                resolveColumn(outputColumn, null, properties, columnNameSeparator);
                //Reverse outputColumn
                Collections.reverse(outputColumn);
            }
            return properties;
        }

        private void resolveColumn(List<ElasticColumn> outputColumn, ElasticColumn column,
                                   Map<Object, Object> propsMap, String columnNameSeparator){
            propsMap.forEach((key, value) ->{
                if(value instanceof Map){
                    Map metaMap = (Map)value;
                    if(null != metaMap.get(ElasticKey.PROPS_COLUMN_TYPE)){
                        ElasticColumn levelColumn = new ElasticColumn();
                        if(null != column) {
                            levelColumn.setName(column.getName() + columnNameSeparator + key);
                        }else{
                            levelColumn.setName(String.valueOf(key));
                        }
                        levelColumn.setType(String.valueOf(metaMap.get(ElasticKey.PROPS_COLUMN_TYPE)));
                        if(null != metaMap.get(ElasticKey.PROPS_COLUMN_TIMEZONE)){
                            levelColumn.setTimezone(String.valueOf(metaMap.get(ElasticKey.PROPS_COLUMN_TIMEZONE)));
                        }
                        if(null != metaMap.get(ElasticKey.PROPS_COLUMN_FORMAT)){
                            levelColumn.setFormat(String.valueOf(metaMap.get(ElasticKey.PROPS_COLUMN_FORMAT)));
                        }
                        outputColumn.add(levelColumn);
                    }else if(null != metaMap.get(ElasticRestHighClient.FIELD_PROPS)
                            && metaMap.get(ElasticRestHighClient.FIELD_PROPS) instanceof Map){
                        ElasticColumn levelColumn = column;
                        if(null == levelColumn){
                            levelColumn = new ElasticColumn();
                            levelColumn.setName(String.valueOf(key));
                        }else{
                            levelColumn.setName(levelColumn.getName() + columnNameSeparator + key);
                        }
                        resolveColumn(outputColumn, levelColumn, (Map)metaMap.get(ElasticRestHighClient.FIELD_PROPS),
                                columnNameSeparator);
                    }
                }
            });
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

            this.userName = this.jobConf.getString(ElasticKey.USERNAME, "");
            this.password = this.jobConf.getString(ElasticKey.PASSWORD, "");
            if(StringUtils.isNotBlank(this.password)){
                try {
                    this.password = (String) CryptoUtils.string2Object(this.password);
                } catch (Exception e) {
                    throw DataXException.asDataXException(ElasticWriterErrorCode.CONFIG_ERROR, "decrypt password failed");
                }
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

        private ElasticRestHighClient restHighClient;
        private BulkProcessor bulkProcessor;
        private volatile boolean bulkError;

        private static final ObjectMapper mapper = new ObjectMapper();

        @Override
        public void startWrite(BasicDataReceiver<Object> receiver, Class<?> type) {
            if(type.equals(DocWriteRequest.class)){
                logger.info("Begin to write record to ElasticSearch");
                long count = 0;
                DocWriteRequest request = null;
                while(null != (request = (DocWriteRequest) receiver.getFromReader())){
                    request.index(indexName);
                    request.type(typeName);
                    if(bulkError){
                        throw DataXException.asDataXException(ElasticWriterErrorCode.BULK_REQ_ERROR, "");
                    }
                    this.bulkProcessor.add(request);
                    count += 1;
                }
                this.bulkProcessor.close();
                getTaskPluginCollector().collectMessage(ElasticWriter.Job.WRITE_SIZE, String.valueOf(count));
                logger.info("End to write record to ElasticSearch");
            }else{
                super.startWrite(receiver, type);
            }
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

            //创建索引映射
            int bulkPerTask = this.taskConf.getInt(ElasticKey.BULK_PER_TASK, 1);
            String userName = this.taskConf.getString(ElasticKey.USERNAME, "");
            String password = this.taskConf.getString(ElasticKey.PASSWORD, "");
            if(StringUtils.isNotBlank(password)){
                try {
                    password = (String) CryptoUtils.string2Object(password);
                } catch (Exception e) {
                    throw DataXException.asDataXException(ElasticWriterErrorCode.CONFIG_ERROR, "decrypt password failed");
                }
            }

            if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)){
                restHighClient = ElasticRestHighClient.custom(endPoints, userName,
                        password, this.taskConf.getMap(ElasticKey.CLIENT_CONFIG));
            }else{
                restHighClient = ElasticRestHighClient.custom(endPoints, this.taskConf.getMap(ElasticKey.CLIENT_CONFIG));
            }
            this.bulkProcessor = restHighClient.createBulk(buildListener(getTaskPluginCollector()), batchSize, bulkPerTask);

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
            if(null != restHighClient){
                restHighClient.close();
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

        private BulkProcessor.Listener buildListener(final TaskPluginCollector pluginCollector){
            return new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long l, BulkRequest bulkRequest) {
                    bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
                    logger.trace("do_bulk: " + bulkRequest.getDescription());
                }

                @Override
                public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                    BulkItemResponse[] response = bulkResponse.getItems();
                    for (BulkItemResponse itemResponse : response) {
                        if (itemResponse.isFailed()) {
                            List<String> message = new ArrayList<>();
                            message.add(String.valueOf(itemResponse.getFailure().getStatus().getStatus()));
                            message.add(itemResponse.getId());
                            message.add(itemResponse.getFailureMessage());
                            pluginCollector.collectDirtyRecord(new DirtyRecord(), null, JSON.toJSONString(message));
                        }
                    }
                }

                @Override
                public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                    //Ignore interrupted error
                    if(!(throwable instanceof  InterruptedException)){
                        logger.error(throwable.getMessage(), throwable);
                    }
                    bulkError = true;
                }
            };
        }

    }
}
