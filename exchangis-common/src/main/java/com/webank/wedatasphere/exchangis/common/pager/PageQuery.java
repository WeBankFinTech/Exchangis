package com.webank.wedatasphere.exchangis.common.pager;

import java.util.Objects;

/**
 * Query Vo
 */
public class PageQuery {

    protected Integer current = 1;

    protected Integer size = 10;

    protected Integer page;

    protected Integer pageSize;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPage() {
        return Objects.nonNull(page) ? page : current;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return Objects.nonNull(pageSize) ? pageSize : size;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
