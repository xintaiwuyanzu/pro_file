package com.dr.framework.common.file.resource;

import com.dr.framework.common.file.FileResource;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 系统文件
 *
 * @author dr
 */
public class FileSystemFileResource implements FileResource {
    private final File file;
    private final String description;

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
        this.file = file;
        Assert.isTrue(file != null && file.exists() && file.isFile(), "指定的文件不存在：" + file.getPath());
        this.description = description;
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
    public String getFileHash() {
        try {
            return Files.asByteSource(file).hash(Hashing.sha512()).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
    }
}
