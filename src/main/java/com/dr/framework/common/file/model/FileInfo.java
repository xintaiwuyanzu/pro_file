package com.dr.framework.common.file.model;

/**
 * 文件基本信息，用来传递和返回参数
 */
public interface FileInfo extends BaseFile {
    /**
     * 文件Id
     *
     * @return
     */
    String getId();

    /**
     * 获取上一个Id
     *
     * @return
     */
    String getPreId();

    /**
     * 获取下一个Id
     *
     * @return
     */
    String getNextId();


    /**
     * 获取文件基本信息Id
     *
     * @return
     */
    String getBaseFileId();

    /**
     * 文件业务外键
     *
     * @return
     */
    String getRefId();

    /**
     * 文件业务类型
     *
     * @return
     */
    String getRefType();

    /**
     * 文件分组编码
     *
     * @return
     */
    String getGroupCode();

    /**
     * 文件保存时间
     *
     * @return
     */
    long getSaveDate();

    /**
     * 获取文件mine
     *
     * @return
     */
    String getMine();

    /**
     * 获取文件描述
     *
     * @return
     */
    String getDescription();

}
