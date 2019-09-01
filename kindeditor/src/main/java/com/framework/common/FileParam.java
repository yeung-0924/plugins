package com.framework.common;

import com.alibaba.fastjson.JSON;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * FileParam 文件参数封装类
 *
 * @author D.Yeung
 * @since 2019.05.14
 */
@ToString
public class FileParam {

    /**
     * 上传的文件（单文件）
     */
    private MultipartFile file;

    /**
     * 上传的文件（多文件）
     */
    private MultipartFile[] files;

    /**
     * 前台参数
     */
    private Map<String, Object> params = new HashMap<>(1 << 4);

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = JSON.parseObject(params);
    }
}