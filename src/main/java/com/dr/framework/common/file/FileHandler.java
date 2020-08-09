package com.dr.framework.common.file;

import org.springframework.core.Ordered;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    default boolean canHandle(BaseFile fileInfo) {
        return false;
    }

    /**
     * 写文件
     *
     * @param file
     * @param fileInfo
     * @throws IOException
     */
    void writeFile(FileResource file, BaseFile fileInfo) throws IOException;

    /**
     * 读文件
     *
     * @param fileInfo
     * @return
     * @throws IOException
     */
    InputStream readFile(BaseFile fileInfo) throws IOException;

    /**
     * 删除文件
     *
     * @param fileInfo
     */
    void deleteFile(BaseFile fileInfo);

    /**
     * 默认最低的，高级的可以实现自定义拦截
     *
     * @return
     */
    @Override
    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * 打开输出文件流
     *
     * @param fileInfo
     * @return
     */
    OutputStream openStream(BaseFile fileInfo) throws IOException;

    /**
     * 使用原生系统方法高效复制文件到指定的文件
     *
     * @param fileInfo
     * @param newFile
     * @return
     * @throws IOException
     */
    boolean copyTo(BaseFile fileInfo, String newFile) throws IOException;
}
