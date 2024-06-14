package com.webank.wedatasphere.exchangis.datasource.core.ui;

import com.webank.wedatasphere.exchangis.datasource.core.vo.ExchangisJobDataSourcesContent;

/**
 * 任务数据源UI对象
 */
public class ExchangisDataSourceIdUI {

    private String type;

    private String id;

    private String name;

    /**
     * Equals to name
     */
    private String ds;

    /**
     * Data source creator
     */
    private String creator;

    private String db;

    private String table;

    public ExchangisDataSourceIdUI(){

    }

    public ExchangisDataSourceIdUI(ExchangisJobDataSourcesContent.ExchangisJobDataSource dataSource){
        this.type = dataSource.getType();
        this.id = dataSource.getId() + "";
        this.name = dataSource.getName();
        this.ds = this.name;
        this.db = dataSource.getDb();
        this.table = dataSource.getTable();
        this.creator = dataSource.getCreator();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
