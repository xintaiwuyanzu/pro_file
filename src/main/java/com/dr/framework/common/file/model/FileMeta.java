package com.dr.framework.common.file.model;

/**
 * 附件元数据信息
 *
 * @author dr
 */
public interface FileMeta {
    /**
     * 获取编码
     *
     * @return
     */
    String getKey();

    /**
     * 获取值
     *
     * @return
     */
    String getValue();

    /**
     * 获取文件Id
     *
     * @return
     */
    String getFileId();

    /**
     * 元数据是否再基本文件信息上
     *
     * @return
     */
    boolean isBase();

}
