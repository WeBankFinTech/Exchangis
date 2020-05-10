/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.common.util.page;

import java.io.Serializable;
import java.util.List;

/**
 * Created by devendeng on 2018/8/20.
 */
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
    }


    public PageList(int page, int pageSize, int totalItems) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public PageList(Paginator p) {
        this.page = p.getPage();
        this.pageSize = p.getPageSize();
        this.totalItems = p.getTotalItems();
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

        totalPages = totalItems / pageSize;
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