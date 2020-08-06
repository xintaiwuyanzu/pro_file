package com.dr.framework.common.file.model;

import java.io.IOException;
import java.io.InputStream;

/**
 * 简单的附件基本信息对象，需要拓展各种类型的附件信息
 *
 * @author dr
 */
public interface SimpleFile {
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
     * 获取文件流
     *
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

}
