package com.dr.framework.common.file;

import org.springframework.util.StringUtils;

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
public interface FileResource {
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
    default String getSuffix() {
        String name = getName();
        if (!StringUtils.isEmpty(name) && name.contains(".")) {
            return name.substring(name.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

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
     * 获取文件描述
     *
     * @return
     */
    String getDescription();

    /**
     * 获取文件流
     *
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获取文件业务类型
     *
     * @return
     */
    default String getFileType() {
        return "";
    }

    /**
     * 获取文件额外的信息
     *
     * @return
     */
    default String getFileAttr() {
        return "";
    }

    /**
     * 获取顺序
     *
     * @return
     */
    default Integer getOrder() {
        return 0;
    }
}
