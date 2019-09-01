package com.framework.common;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ResultVO 后台处理信息封装类
 *
 * @author D.Yeung
 * @since 2018.05.01
 */
@ToString
public class ResultVO {

    /**
     * 状态码
     */
    private Integer status = 200;

    /**
     * 处理结果
     */
    private Boolean success = true;

    /**
     * 处理结果信息
     */
    private String message;

    /**
     * 总记录数
     */
    private Long total = 0L;

    /**
     * 页面容量
     */
    private Integer pageSize;

    /**
     * 后台返回的主键
     */
    private String id;

    /**
     * 后台返回的对象
     */
    private Object obj;

    /**
     * 后台返回的列表对象
     */
    private List<?> rows = new ArrayList<>();

    /**
     * 后台返回的列表对象和普通对象
     */
    private Map<String, ?> map;

    /**
     * 参数列表
     */
    private List<?> columns;

    /**
     * 后台返回的页脚列表对象
     */
    private List<?> footer;

    public int getStatus() {
        return status;
    }

    public ResultVO setStatus(int status) {
        this.status = status;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public ResultVO setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public void setRows(List<?> rows, Map<String, Object> objMap) {
        this.rows = rows;
        Object total = objMap.get("total");
        if (null != total) {
            this.total = Long.parseLong(total.toString());
        }
        Integer pageSize = (Integer) objMap.get("pageSize");
        if (null != pageSize) {
            this.pageSize = pageSize;
        }
    }

    public Map<String, ?> getMap() {
        return map;
    }

    public void setMap(Map<String, ?> map) {
        this.map = map;
    }

    public List<?> getColumns() {
        return columns;
    }

    public void setColumns(List<?> columns) {
        this.columns = columns;
    }

    public List<?> getFooter() {
        return footer;
    }

    public void setFooter(List<?> footer) {
        this.footer = footer;
    }

    /**
     * 错误VO
     *
     * @param message 自定义错误消息
     * @return ResultVO
     */
    public ResultVO errorVO(String message) {
        this.setSuccess(Boolean.FALSE);
        this.setMessage(message);
        return this;
    }
}