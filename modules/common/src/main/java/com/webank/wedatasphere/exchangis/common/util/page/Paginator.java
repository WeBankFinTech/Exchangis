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

/**
 * Created by devendeng on 2018/8/20.
 */
public class Paginator implements java.io.Serializable {
    private static final long serialVersionUID = 6089482156906595931L;

    private static final int DEFAULT_SLIDERS_COUNT = 7;

    /**
     * Page size
     */
    private int pageSize;
    /**
     * Page num
     */
    private int page;
    /**
     * Total of items
     */
    private int totalItems;

    public Paginator(int page, int pageSize, int totalItems) {
        super();
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.page = computePageNo(page);
    }

    /**
     * Get current page
     */
    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * Total items
     *
     * @return number
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * If is the first page
     * @return page
     */
    public boolean isFirstPage() {
        return page <= 1;
    }

    /**
     * If is the last page
     * @return
     */
    public boolean isLastPage() {
        return page >= getTotalPages();
    }

    public int getPrePage() {
        if (isHasPrePage()) {
            return page - 1;
        } else {
            return page;
        }
    }

    public int getNextPage() {
        if (isHasNextPage()) {
            return page + 1;
        } else {
            return page;
        }
    }

    /**
     * If the page is disabled
     * @param page Page num
     * @return boolean
     */
    public boolean isDisabledPage(int page) {
        return ((page < 1) || (page > getTotalPages()) || (page == this.page));
    }

    /**
     * If it has previous page
     * @return
     */
    public boolean isHasPrePage() {
        return (page - 1 >= 1);
    }

    /**
     * If it has next page
     *
     * @return
     */
    public boolean isHasNextPage() {
        return (page + 1 <= getTotalPages());
    }

    /**
     * Start line
     **/
    public int getStartRow() {
        if(getPageSize() <= 0 || totalItems <= 0){
            return 0;
        }
        return page > 0 ? (page - 1) * getPageSize() + 1 : 0;
    }

    /**
     * End line
     **/
    public int getEndRow() {
        return page > 0 ? Math.min(pageSize * page, getTotalItems()) : 0;
    }

    /**
     * offset
     **/
    public int getOffset() {
        return page > 0 ? (page - 1) * getPageSize() : 0;
    }

    /**
     * limit
     **/
    public int getLimit() {
        if (page > 0) {
            return Math.min(pageSize * page, getTotalItems()) - (pageSize * (page - 1));
        } else {
            return 0;
        }
    }

    /**
     * Get total
     *
     * @return
     */
    public int getTotalPages() {
        if (totalItems <= 0) {
            return 0;
        }
        if (pageSize <= 0) {
            return 0;
        }

        int count = totalItems / pageSize;
        if (totalItems % pageSize > 0) {
            count++;
        }
        return count;
    }

    protected int computePageNo(int page) {
        return computePageNumber(page, pageSize, totalItems);
    }

    /**
     * Get slider
     *
     * @return
     */
    public Integer[] getSlider() {
        return slider(DEFAULT_SLIDERS_COUNT);
    }

    /**
     *
     * @return
     */
    public Integer[] slider(int slidersCount) {
        return generateLinkPageNumbers(getPage(), (int) getTotalPages(), slidersCount);
    }

    private static int computeLastPageNumber(int totalItems, int pageSize) {
        if(pageSize <= 0){
            return 1;
        }
        int result = (int) (totalItems % pageSize == 0 ?
                totalItems / pageSize
                : totalItems / pageSize + 1);
        if(result <= 1) {
            result = 1;
        }
        return result;
    }

    private static int computePageNumber(int page, int pageSize, int totalItems) {
        if (page <= 1) {
            return 1;
        }
        if (Integer.MAX_VALUE == page
                || page > computeLastPageNumber(totalItems, pageSize)) { //last page
            return computeLastPageNumber(totalItems, pageSize);
        }
        return page;
    }

    private static Integer[] generateLinkPageNumbers(int currentPageNumber, int lastPageNumber, int count) {
        int avg = count / 2;

        int startPageNumber = currentPageNumber - avg;
        if (startPageNumber <= 0) {
            startPageNumber = 1;
        }

        int endPageNumber = startPageNumber + count - 1;
        if (endPageNumber > lastPageNumber) {
            endPageNumber = lastPageNumber;
        }

        if (endPageNumber - startPageNumber < count) {
            startPageNumber = endPageNumber - count;
            if (startPageNumber <= 0) {
                startPageNumber = 1;
            }
        }

        java.util.List<Integer> result = new java.util.ArrayList<Integer>();
        for (int i = startPageNumber; i <= endPageNumber; i++) {
            result.add(new Integer(i));
        }
        return result.toArray(new Integer[result.size()]);
    }

    @Override
    public String toString() {
        return "page:" + page + " pageSize:" + pageSize + " totalItems:" + totalItems;
    }

}