package com.dr.framework.common.file;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;

/**
 * 文件原始基本信息
 * <p>
 * 不管是基本信息还是关联信息，都需要有这些信息。
 * 关联信息覆盖基本信息的属性
 *
 * @author dr
 */
class BaseFile extends BaseCreateInfoEntity {
    @Column(comment = "文件原始名称", length = 500)
    private String originName;
    @Column(comment = "文件原始后缀", length = 100)
    private String suffix;
    @Column(comment = "原始文件创建日期", type = ColumnType.DATE)
    private long originCreateDate;
    @Column(comment = "原始文件最后更新日期", type = ColumnType.DATE)
    private long lastModifyDate;

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
}
