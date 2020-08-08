package com.dr.framework.common.file.model;

import java.io.IOException;
import java.io.InputStream;

/**
 * 简单的附件基本信息对象，需要拓展各种类型的附件信息
 * <p>
 * 目前想到的附件类型有以下几种
 * <p>
 * 文件系统
 * 上传附件
 * jar包文件
 * 压缩包文件
 * ftp文件
 * 字节码文件
 * InputStream文件
 * 网络文件
 *
 * @author dr
 */
public interface FileResource extends BaseFile {
    /**
     * 获取文件流
     *
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获取文件描述
     *
     * @return
     */
    String getDescription();
}
