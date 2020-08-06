package com.dr.framework.common.file;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.ColumnType;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 附件元数据，可以挂在附件基本信息上，也可以挂在附件关联信息上
 * key不能重复
 * <p>
 * 当基本信息和关联信息都有相同的key时，关联信息的key覆盖基本信息
 *
 * @author dr
 */
@Table(name = Constants.COMMON_TABLE_PREFIX + "FILE_META", comment = "文件元数据", module = Constants.COMMON_MODULE_NAME, genInfo = false)
class FileMetaData extends BaseCreateInfoEntity {

    @Column(comment = "业务外键", length = 100)
    private String refId;
    /**
     * 0表示基本信息
     * 1表示关联信息
     */
    @Column(comment = "业务外键类型", length = 4)
    private Integer refType;
    @Column(comment = "业务外键key", length = 50)
    private String key;
    @Column(comment = "业务外键value", type = ColumnType.CLOB)
    private String value;

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Integer getRefType() {
        return refType;
    }

    public void setRefType(Integer refType) {
        this.refType = refType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
