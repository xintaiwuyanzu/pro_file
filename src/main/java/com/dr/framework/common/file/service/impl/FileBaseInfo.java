package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 附件基本信息表
 *
 * @author dr
 */
@Table(name = Constants.COMMON_TABLE_PREFIX + "FILE_INFO", module = Constants.COMMON_MODULE_NAME)
class FileBaseInfo extends BaseCreateInfoEntity {
    @Column(comment = "文件原始名称", length = 500)
    private String originName;
    @Column(comment = "文件原始后缀", length = 100)
    private String suffix;
    @Column(comment = "原始文件创建日期", type = ColumnType.DATE)
    private long originCreateDate;
    @Column(comment = "原始文件最后更新日期", type = ColumnType.DATE)
    private long lastModifyDate;

    @Column(name = "fileSize", comment = "文件大小", length = 10)
    private long size;
    /**
     * 相同hash文件不重复存储
     */
    @Column(name = "fileHash", comment = "文件hash", length = 100)
    private String hash;

    @Column(comment = "文件实际类型", length = 100)
    private String mimeType;

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public long getOriginCreateDate() {
        return originCreateDate;
    }

    public void setOriginCreateDate(long originCreateDate) {
        this.originCreateDate = originCreateDate;
    }

    public long getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
