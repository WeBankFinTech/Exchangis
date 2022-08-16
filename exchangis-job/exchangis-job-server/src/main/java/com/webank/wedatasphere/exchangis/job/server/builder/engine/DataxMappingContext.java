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
         * Index name
         */
        private Integer index;

        public Column(){

        }

        public Column(String name, String type, Integer index){

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
            private int columnIndex;

            /**
             * Params
             */
            private List<String> paras = new ArrayList<>();

            public Parameter(){

            }

            public Parameter(List<String> paras){
                this.paras = paras;
            }
            public int getColumnIndex() {
                return columnIndex;
            }

            public void setColumnIndex(int columnIndex) {
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
