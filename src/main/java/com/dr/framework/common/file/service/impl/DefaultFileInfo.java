package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.model.FileInfo;

/**
 * 默认文件信息
 *
 * @author dr
 */
class DefaultFileInfo extends DefaultBaseFile implements FileInfo {
    private String id;
    private String refId;
    private String preId;
    private String nextId;
    private String refType;
    private String groupCode;
    private String description;

    public DefaultFileInfo(FileBaseInfo base, FileRelation relation) {
        super(base);
        setId(relation.getId());
        setRefId(relation.getRefId());
        setRefType(relation.getRefType());
        setGroupCode(relation.getGroupCode());
        setDescription(relation.getDescription());
    }

    public DefaultFileInfo() {
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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }

    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }
}

