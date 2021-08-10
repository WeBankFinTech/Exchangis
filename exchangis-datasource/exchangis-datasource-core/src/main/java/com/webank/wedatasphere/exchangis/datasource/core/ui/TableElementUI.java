package com.webank.wedatasphere.exchangis.datasource.core.ui;


import java.util.ArrayList;
import java.util.List;

public class TableElementUI {

    private List<TableRowElementUI> rows = new ArrayList<>();

    public TableElementUI addRow(TableRowElementUI row) {
        this.rows.add(row);
        return this;
    }

    public List<TableRowElementUI> getRows() {
        return rows;
    }

    public void setRows(List<TableRowElementUI> rows) {
        this.rows = rows;
    }
}
