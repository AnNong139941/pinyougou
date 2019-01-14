package com.inso.core.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分装分页结果信息 总数以及结果集
 * 序列化的场景:
 *      1.网络传输
 *      2.orm框架的缓存
 * 实现序列化的好处:
 *      1.灾备
 *      2.数据共享(序列化后二进制的数据可以在任何平台使用)
 */
public class PageResult implements Serializable {
    private Long total;  //页面传递过来的总条数
    private List rows;   //传递给页面的结果集

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
