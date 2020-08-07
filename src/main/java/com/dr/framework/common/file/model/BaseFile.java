package com.dr.framework.common.file.model;

public interface BaseFile {
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
     * 获取文件大小
     *
     * @return
     */
    long getFileSize();

    /**
     * 获取文件hash值
     *
     * @return
     */
    String getFileHash();

    /**
     * 获取文件描述
     *
     * @return
     */
    String getDescription();
}
