package com.framework.common;

import com.alibaba.fastjson.JSON;
import com.framework.util.StringUtil;
import lombok.ToString;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * ObjectParam 前台参数封装类
 *
 * @author D.Yeung
 * @since 2018.05.01
 */
@ToString
public class ObjectParam {

    /**
     * 前台参数
     */
    private Map<String, Object> params = new HashMap<>(1 << 4);

    /**
     * 参数原始JSON
     */
    private String paramsStr;

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 页面显示记录数
     */
    private Integer rows;

    /**
     * 排序字段
     */
    private String sort;

    /**
     * 排序方式
     */
    private String order;

    public Integer getPage() {
        return page;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(String params) {
        this.setParamsStr(params);
        if (null != params && !"".equals(params)) {
            Map<String, Object> paramMap = JSON.parseObject(params);
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                this.params.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setParamsStr(String paramsStr) {
        this.paramsStr = paramsStr;
    }

    public void setPage(Integer page) {
        if (null != page) {
            this.params.put("page", page);
        }
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        if (null != rows) {
            this.params.put("rows", rows);
        }
        this.rows = rows;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        if (null != sort && !"".equals(sort)) {
            this.params.put("sort", sort);
        }
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        if (null != order && !"".equals(order)) {
            this.params.put("order", order);
        }
        this.order = order;
    }

    /**
     * 追加session信息
     *
     * @param session javax.servlet.http.HttpSession
     */
    public void appendSessionInfo(HttpSession session) {
        if (StringUtil.isNull(this.params.get("userId"))) {
            this.params.put("userId", session.getAttribute("userId"));
        }
    }
}