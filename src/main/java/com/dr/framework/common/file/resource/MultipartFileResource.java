package com.dr.framework.common.file.resource;

import com.dr.framework.common.file.FileResource;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * spring上传附件
 *
 * @author dr
 */
public class MultipartFileResource extends ByteSource implements FileResource {
    private final MultipartFile multipartFile;

    public MultipartFileResource(MultipartFile multipartFile) {
        Assert.isTrue(multipartFile != null, "上传附件不能为空！");
        this.multipartFile = multipartFile;
    }

    @Override
    public String getName() {
        return multipartFile.getOriginalFilename();
    }

    @Override
    public long getCreateDate() {
        return System.currentTimeMillis();
    }

    @Override
    public long getLastModifyDate() {
        return System.currentTimeMillis();
    }

    @Override
    public long getFileSize() {
        return multipartFile.getSize();
    }

    @Override
    public InputStream openStream() throws IOException {
        return getInputStream();
    }

    @Override
    public String getFileHash() {
        try {
            return hash(Hashing.sha512()).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
    }

    @Override
    public String getDescription() {
        return multipartFile.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }
}
