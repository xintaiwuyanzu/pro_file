package com.dr.framework.common.file;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 附件基本信息表
 *
 * @author dr
 */
@Table(name = Constants.COMMON_TABLE_PREFIX + "FILE_INFO", genInfo = false, module = Constants.COMMON_MODULE_NAME)
class FileBaseInfo extends BaseFile {
    @Column(comment = "文件大小", length = 10)
    private long size;
    /**
     * 相同hash文件不重复存储
     */
    @Column(name = "fileHash", comment = "文件hash", length = 100)
    private String hash;

    @Column(comment = "文件实际类型", length = 100)
    private String mineType;

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

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }
}
