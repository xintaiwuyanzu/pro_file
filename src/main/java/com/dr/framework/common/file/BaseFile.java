package com.dr.framework.common.file;

/**
 * 基本信息
 *
 * @author dr
 */
public interface BaseFile {

    /**
     * 获取文件基本信息Id
     *
     * @return
     */
    String getBaseFileId();

    /**
     * 获取文件名称
     *
     * @return
     */
    String getName();

    /**
     * 获取文件后缀
     *
     * @return
     */
    String getSuffix();

    /**
     * 获取文件创建日期
     *
     * @return
     */
    long getCreateDate();

    /**
     * 获取文件最后修改日期
     *
     * @return
     */
    long getLastModifyDate();

    /**
     * 文件保存时间
     *
     * @return
     */
    long getSaveDate();

    /**
     * 获取文件大小
     *
     * @return
     */
    long getFileSize();
}
