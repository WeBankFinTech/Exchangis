package com.webank.wedatasphere.exchangis.common.pager;

import com.github.pagehelper.PageInfo;
import org.apache.linkis.server.Message;

import java.util.List;

/**
 * Page result
 * @param <T>
 */
public class PageResult<T>{
    /**
     * Total
     */
    private Long total;

    /**
     * List
     */
    private List<T> list;

    public PageResult(){

    }

    public PageResult(PageInfo<T> pageInfo){
        this.total = pageInfo.getTotal();
        this.list = pageInfo.getList();
    }
    /**
     * To Message(in linkis-common)
     * @return message
     */
    public Message toMessage(String info){
        Message message = Message.ok(info);
        message.data("total", total);
        message.data("list", list);
        return message;
    }

    public Message toMessage(){
        return toMessage("");
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
