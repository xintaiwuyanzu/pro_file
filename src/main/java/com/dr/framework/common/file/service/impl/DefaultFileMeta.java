package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.model.FileMeta;

/**
 * 默认实现
 */
class DefaultFileMeta implements FileMeta {
    private String key;
    private String value;
    private String fileId;
    private boolean base;

    public DefaultFileMeta(FileMetaData fileMetaData) {
        setKey(fileMetaData.getKey());
        setValue(fileMetaData.getValue());
        setBase(fileMetaData.getRefType() == 0);
        setFileId(fileMetaData.getRefId());
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }
}
