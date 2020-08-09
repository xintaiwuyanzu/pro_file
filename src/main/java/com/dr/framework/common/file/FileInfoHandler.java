package com.dr.framework.common.file;

/**
 * 用来辅助获取文件mine类型
 *
 * @author dr
 */
public interface FileInfoHandler {
    /**
     * 获取文件mine
     *
     * @param resource
     * @return
     */
    String fileMine(FileResource resource);

    /**
     * 获取文件hash
     *
     * @param resource
     * @return
     */
    String fileHash(FileResource resource);
}
