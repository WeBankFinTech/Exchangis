package com.webank.wedatasphere.exchangis.job.server.builder.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Datax mapping context
 */
public class DataxMappingContext {

    /**
     * Source columns
     */
    private List<Column> sourceColumns = new ArrayList<>();

    /**
     * Sink columns
     */
    private List<Column> sinkColumns = new ArrayList<>();

    /**
     * Transform
     */
    private List<Transformer> transformers = new ArrayList<>();

    public List<Column> getSourceColumns() {
        return sourceColumns;
    }

    public void setSourceColumns(List<Column> sourceColumns) {
        this.sourceColumns = sourceColumns;
    }

    public List<Column> getSinkColumns() {
        return sinkColumns;
    }

    public void setSinkColumns(List<Column> sinkColumns) {
        this.sinkColumns = sinkColumns;
    }

    public List<Transformer> getTransformers() {
        return transformers;
    }

    public void setTransformers(List<Transformer> transformers) {
        this.transformers = transformers;
    }

    /**
     * Column entity
     */
    public static class Column{
        /**
         * Colum name
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
         * Index name
         */
        private String index;

        public Column(){

        }

        public Column(String name, String type, String index){
            this(name, type, null, index);
        }

        public Column(String name, String type, String rawType, String index){
            this.name = name;
            this.type = type;
            this.rawType = rawType;
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

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
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
     * Transformer
     */
    public static class Transformer {

        /**
         * Parameter context
         */
        private Parameter parameter = new Parameter();
        /**
         * Name
         */
        private String name;

        public Transformer(){

        }

        public Transformer(String name, Parameter parameter){
            this.name = name;
            this.parameter = parameter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Parameter getParameter() {
            return parameter;
        }

        public void setParameter(Parameter parameter) {
            this.parameter = parameter;
        }

        /**
         * Parameter
         */
        public static class Parameter {
            /**
             * Index
             */
            private String columnIndex;

            /**
             * Params
             */
            private List<String> paras = new ArrayList<>();

            public Parameter(){

            }

            public Parameter(List<String> paras){
                this.paras = paras;
            }

            public String getColumnIndex() {
                return columnIndex;
            }

            public void setColumnIndex(String columnIndex) {
                this.columnIndex = columnIndex;
            }

            public List<String> getParas() {
                return paras;
            }

            public void setParas(List<String> paras) {
                this.paras = paras;
            }
        }
    }
}
