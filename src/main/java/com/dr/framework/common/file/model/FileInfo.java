package com.dr.framework.common.file.model;

import com.dr.framework.common.file.BaseFile;

/**
 * 文件基本信息，用来传递和返回参数
 *
 * @author dr
 */
public interface FileInfo extends BaseFile {
    /**
     * 文件Id
     *
     * @return
     */
    String getId();

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
     * 获取文件描述
     *
     * @return
     */
    String getDescription();

    /**
     * 获取文件hash值
     *
     * @return
     */
    String getFileHash();

    /**
     * 文件媒体类型
     *
     * @return
     */
    String getMimeType();

    /**
     * 顺序号
     *
     * @return
     */
    Integer getOrder();
}
