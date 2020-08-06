package com.dr.framework.common.file;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 附件关联信息表
 * <p>
 * 根据文件hash判断重复文件
 *
 * @author dr
 */
@Table(name = Constants.COMMON_TABLE_PREFIX + "FILE_RELATION", genInfo = false, module = Constants.COMMON_MODULE_NAME)
class FileRelation extends BaseCreateInfoEntity {

    @Column(comment = "文件描述", length = 1000)
    private String description;
    /**
     * 关联文件ID
     */
    @Column(length = 100)
    private String fileId;
    /*
     * =========================
     * 文件关联信息
     * =========================
     */
    @Column(comment = "业务外键", length = 100)
    private String refId;
    /**
     * 业务外键类型，可以用来区分是什么类型的业务
     * 也可以用来连表查询数据
     * <p>
     * 比如，档案、文书、图书等等，多条数据的外键类型可以相同
     */
    @Column(comment = "外键类型", length = 200)
    private String refType;
    /**
     * 同一个业务外键可能有多组附件，可以根据分组代码分别展示
     */
    @Column(comment = "分组代码", length = 200)
    private String groupCode;
    /*
     * =========================
     * 双向链表构建文件排序
     * =========================
     */
    /**
     * 第一个附件的上一个附件Id为null
     */
    @Column(comment = "前一个附件Id", length = 100)
    private String preId;
    /**
     * 最后一个附件的下一个Id为null
     */
    @Column(comment = "后一个附件Id", length = 100)
    private String nextId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }
}
