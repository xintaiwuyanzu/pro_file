package com.dr.framework.common.file.service;

import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.model.FileResource;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件处理工具类
 * <p>
 * 保存和读取文件流
 *
 * @author dr
 */
public interface FileHandler extends Ordered {
    /**
     * 是否能够处理指定类型的文件
     *
     * @param fileInfo
     * @return
     */
    default boolean canHandle(FileInfo fileInfo) {
        return false;
    }

    /**
     * 写文件
     *
     * @param file
     * @param fileInfo
     * @throws IOException
     */
    void writeFile(FileResource file, FileInfo fileInfo) throws IOException;

    /**
     * 读文件
     *
     * @param fileInfo
     * @return
     * @throws IOException
     */
    InputStream readFile(FileInfo fileInfo) throws IOException;

    /**
     * 删除文件
     *
     * @param fileInfo
     */
    void deleteFile(FileInfo fileInfo);

    @Override
    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
