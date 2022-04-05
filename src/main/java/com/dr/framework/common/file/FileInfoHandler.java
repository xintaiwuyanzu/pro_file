package com.dr.framework.common.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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
    default String fileHash(FileResource resource) {
        try {
            return fileHash(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
    }

    /**
     * 根据流计算文件hash
     *
     * @param stream
     * @return
     */
    String fileHash(InputStream stream);
}
