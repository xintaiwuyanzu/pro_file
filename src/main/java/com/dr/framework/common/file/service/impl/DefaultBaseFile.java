package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.BaseFile;

/**
 * 默认的基本附件信息表
 * 没有任务业务关联信息
 *
 * @author dr
 */
class DefaultBaseFile implements BaseFile {
    private String baseFileId;
    private String name;
    private String suffix;
    private long createDate;
    private long lastModifyDate;
    private long saveDate;
    private long fileSize;
    private String fileHash;

    public DefaultBaseFile() {
    }

    public DefaultBaseFile(FileBaseInfo info) {
        setBaseFileId(info.getId());
        setName(info.getOriginName());
        setSuffix(info.getSuffix());
        setCreateDate(info.getOriginCreateDate());
        setLastModifyDate(info.getLastModifyDate());
        setFileSize(info.getFileSize());
        setFileHash(info.getFileHash());
    }

    @Override
    public String getBaseFileId() {
        return baseFileId;
    }

    public void setBaseFileId(String baseFileId) {
        this.baseFileId = baseFileId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    @Override
    public long getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    @Override
    public long getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(long saveDate) {
        this.saveDate = saveDate;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
