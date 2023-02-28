package com.dr.framework.common.file.resource;

import com.dr.framework.common.file.FileResource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 系统文件
 *
 * @author dr
 */
public class FileSystemFileResource implements FileResource {
    private final File file;
    private final String description;
    private String fileType;
    private String fileAttr;
    private Integer order;

    public FileSystemFileResource(String filePath) {
        this(new File(filePath), null);
    }

    public FileSystemFileResource(String filePath, String description) {
        this(new File(filePath), description);
    }

    public FileSystemFileResource(File file) {
        this(file, null);
    }

    public FileSystemFileResource(File file, String description) {
        this(file, description, null);
    }

    public FileSystemFileResource(File file, String description, Integer order) {
        this.file = file;
        Assert.isTrue(file != null && file.exists() && file.isFile(), "指定的文件不存在：" + file.getPath());
        this.description = description;
        this.order = order;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public long getCreateDate() {
        //TODO
        return 0;
    }

    @Override
    public long getLastModifyDate() {
        return file.lastModified();
    }

    @Override
    public long getFileSize() {
        return file.length();
    }

    @Override
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String getFileAttr() {
        return fileAttr;
    }

    public void setFileAttr(String fileAttr) {
        this.fileAttr = fileAttr;
    }

    @Override
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
