package com.webank.wedatasphere.exchangis.common.pager;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageList<E> implements Serializable {

    private static final long serialVersionUID = 1412759446332294208L;

    /**
     * Page size
     */
    private int pageSize;
    /**
     * Page number
     */
    private int page;
    /**
     * Total of items
     */
    private int totalItems;

    private int totalPages;

    private int code = 0;
    private String message;

    private List<E> data;

    public PageList() {
        this.data = new ArrayList<>();
    }


    public PageList(int page, int pageSize, int totalItems) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.data = new ArrayList<>();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
        if (totalItems <= 0) {
            totalPages = 0;
        }
        if (pageSize <= 0) {
            totalPages = 0;
        }
        if (pageSize > 0) {
            totalPages = totalItems / pageSize;
        }
        if (totalItems % pageSize > 0) {
            totalPages++;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
