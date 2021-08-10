package com.webank.wedatasphere.exchangis.datasource.core.ui;

import java.util.ArrayList;
import java.util.List;

public class TableRowElementUI {
    private Integer sort;
    private List<TableCellElementUI> cells = new ArrayList<>();

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<TableCellElementUI> getCells() {
        return cells;
    }

    public void setCells(List<TableCellElementUI> cells) {
        this.cells = cells;
    }

    public TableRowElementUI addCell(TableCellElementUI cell) {
        this.cells.add(cell);
        return this;
    }

    public Integer getSort() {
        return sort;
    }
}
