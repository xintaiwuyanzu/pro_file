package com.dr.framework.common.file.resource;

import com.dr.framework.common.file.FileResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 根据二进制创建文件流
 *
 * @author caor
 * @date 2021-10-25 17:18
 */
public class ByteArrayFileResource implements FileResource {
    private byte[] fileBytes;
    private long date;
    private String fileName;

    public ByteArrayFileResource(byte[] fileBytes, String fileName) {
        this.fileBytes = fileBytes;
        this.date = System.currentTimeMillis();
        this.fileName = fileName;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public long getCreateDate() {
        return date;
    }

    @Override
    public long getLastModifyDate() {
        return date;
    }

    @Override
    public long getFileSize() {
        return this.fileBytes.length;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(fileBytes);
    }
}
