package com.dr.framework.common.file.service;

import com.dr.framework.common.file.model.FileResource;

/**
 * 用来辅助获取文件mine类型
 *
 * @author dr
 */
public interface FileMineHandler {
    /**
     * 获取文件mine
     *
     * @param resource
     * @return
     */
    String fileMine(FileResource resource);
}
