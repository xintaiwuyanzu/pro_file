package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.model.FileInfo;

/**
 * 默认文件信息
 *
 * @author dr
 */
class DefaultFileInfo implements FileInfo {
    private String id;
    private String preId;
    private String nextId;
    private String baseFileId;
    private String refId;
    private String refType;
    private String groupCode;
    private long saveDate;
    private String name;
    private String suffix;
    private long createDate;
    private long lastModifyDate;
    private long fileSize;
    private String fileHash;
    private String mine;
    private String description;


    public DefaultFileInfo(FileBaseInfo base, FileRelation relation) {
        setId(relation.getId());
        setPreId(relation.getPreId());
        setNextId(relation.getNextId());
        setRefId(relation.getRefId());
        setRefType(relation.getRefType());
        setGroupCode(relation.getGroupCode());
        setSaveDate(relation.getCreateDate());
        setDescription(relation.getDescription());


        setBaseFileId(base.getId());
        setName(base.getOriginName());
        setSuffix(base.getSuffix());
        setCreateDate(base.getOriginCreateDate());
        setLastModifyDate(base.getLastModifyDate());
        setFileSize(base.getSize());
        setFileHash(base.getHash());
        setMine(base.getMineType());

    }

    public DefaultFileInfo() {
    }


    public DefaultFileInfo(String refId, String refType, String groupCode) {
        this.refId = refId;
        this.refType = refType;
        this.groupCode = groupCode;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    @Override
    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    @Override
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    @Override
    public long getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(long saveDate) {
        this.saveDate = saveDate;
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

    @Override
    public String getMine() {
        return mine;
    }

    public void setMine(String mine) {
        this.mine = mine;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getBaseFileId() {
        return baseFileId;
    }

    public void setBaseFileId(String baseFileId) {
        this.baseFileId = baseFileId;
    }

    @Override
    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }

    @Override
    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }
}
