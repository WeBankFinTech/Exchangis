package com.webank.wedatasphere.exchangis.job.server.builder.transform.mappings;

import java.util.HashMap;
import java.util.Map;

/**
 * Hive File type for Hive version 2.x
 */
public enum HiveV2FileType {
    /**
     * TYPE:TEXT
     */
    TEXT,
    /**
     * TYPE:ORC
     */
    ORC,
    /**
     * TYPE:AVRO
     */
    AVRO,
    /**
     * TYPE:PARQUET
     */
    PARQUET,
    /**
     * TYPE:RC
     */
    RC,
    /**
     * TYPE:SEQUENCE
     */
    SEQ;

    static final Map<String, HiveV2FileType> SERDE = new HashMap<>();
    static final Map<String, HiveV2FileType> INPUT = new HashMap<>();
    static final Map<String, HiveV2FileType> OUTPUT = new HashMap<>();
    static{
        SERDE.put("org.apache.hadoop.hive.ql.io.orc.OrcSerde", ORC);
        SERDE.put("org.apache.hadoop.hive.serde2.avro.AvroSerDe", AVRO);
        SERDE.put("org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe", PARQUET);
        INPUT.put("org.apache.hadoop.mapred.TextInputFormat", TEXT);
        INPUT.put("org.apache.hadoop.hive.ql.io.orc.OrcInputFormat", ORC);
        INPUT.put("org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat", AVRO);
        INPUT.put("org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat", PARQUET);
        INPUT.put("org.apache.hadoop.hive.ql.io.RCFileInputFormat", RC);
        INPUT.put("org.apache.hadoop.mapred.SequenceFileInputFormat", SEQ);
        OUTPUT.put("org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat", TEXT);
        OUTPUT.put("org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat", ORC);
        OUTPUT.put("org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat", AVRO);
        OUTPUT.put("org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat", PARQUET);
        OUTPUT.put("org.apache.hadoop.hive.ql.io.RCFileOutputFormat", RC);
        OUTPUT.put("org.apache.hadoop.mapred.SequenceFileOutputFormat", SEQ);
    }
    HiveV2FileType(){
    }

    static HiveV2FileType serde(String serializationClz){
        return SERDE.get(serializationClz);
    }

    static HiveV2FileType input(String inputFormat){
        return INPUT.get(inputFormat);
    }

    static HiveV2FileType output(String outputFormat){
        return OUTPUT.get(outputFormat);
    }
}
