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

    @Column(comment = "文件大小", length = 10)
    private long fileSize;
    /**
     * 相同hash文件不重复存储
     */
    @Column(comment = "文件hash", length = 200)
    private String fileHash;
    /**
     * TODO
     */
    @Column(comment = "hash编码类型", length = 100)
    private String hashMethod;

    @Column(comment = "文件实际类型", length = 100)
    private String mimeType;
    @Column(comment = "文件业务类型", length = 200)
    private String fileType;
    @Column(comment = "文件额外的信息", length = 800)
    private String fileAttr;

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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getHashMethod() {
        return hashMethod;
    }

    public void setHashMethod(String hashMethod) {
        this.hashMethod = hashMethod;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileAttr() {
        return fileAttr;
    }

    public void setFileAttr(String fileAttr) {
        this.fileAttr = fileAttr;
    }
}
