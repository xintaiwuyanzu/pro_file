package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.entity.BaseCreateInfoEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Index;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.util.Constants;

/**
 * 附件关联信息表
 * <p>
 * 根据文件hash判断重复文件
 *
 * @author dr
 */
@Table(name = Constants.COMMON_TABLE_PREFIX + "FILE_RELATION", module = Constants.COMMON_MODULE_NAME)
class FileRelation extends BaseCreateInfoEntity {
    /**
     * 关联文件ID
     */
    @Index
    @Column(length = 100)
    private String fileId;

    @Column(comment = "文件描述", length = 1000)
    private String description;
    /*
     * =========================
     * 文件关联信息
     * =========================
     */
    @Index
    @Column(comment = "业务外键", length = 100)
    private String refId;
    /**
     * 业务外键类型，可以用来区分是什么类型的业务
     * 也可以用来连表查询数据
     * <p>
     * 比如，档案、文书、图书等等，多条数据的外键类型可以相同
     */
    @Index
    @Column(comment = "外键类型", length = 200)
    private String refType;
    /**
     * 同一个业务外键可能有多组附件，可以根据分组代码分别展示
     */
    @Index
    @Column(comment = "分组代码", length = 200)
    private String groupCode;
    /*
     * =========================
     * 单链表构建文件排序
     * =========================
     */
    /**
     * 最后一个附件的下一个Id为null
     */

    @Column(comment = "后一个附件Id", length = 100)
    private String preId;

    @Column(comment = "后一个附件Id", length = 100)
    private String nextId;

    @Column(comment = "order_info")
    private Integer orderBy;
    @Column(comment = "扩展字段1", length = 1000)
    private String ext1;

    @Column(comment = "扩展字段2", length = 1000)
    private String ext2;

    @Column(comment = "扩展字段3", length = 1000)
    private String ext3;

    @Column(comment = "扩展字段4", length = 1000)
    private String ext4;

    @Column(comment = "扩展字段5", length = 1000)
    private String ext5;

    public FileRelation() {
    }

    public FileRelation(String refId, String refType, String groupCode) {
        this.refId = refId;
        this.refType = refType;
        this.groupCode = groupCode;
    }


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

    public Integer getOrder() {
        return orderBy;
    }

    public void setOrder(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }
}
