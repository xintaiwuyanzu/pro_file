package com.dr.framework.common.file.resource;

import com.dr.framework.common.file.FileResource;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * spring上传附件
 *
 * @author dr
 */
public class MultipartFileResource implements FileResource {
    private final MultipartFile multipartFile;
    private String fileType;
    private String fileAttr;

    public MultipartFileResource(MultipartFile multipartFile) {
        Assert.isTrue(multipartFile != null, "上传附件不能为空！");
        this.multipartFile = multipartFile;
    }

    public MultipartFileResource(MultipartFile multipartFile, String fileType, String fileAttr) {
        this(multipartFile);
        this.fileType = fileType;
        this.fileAttr = fileAttr;
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
    public String getDescription() {
        return multipartFile.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    @Override
    public String getFileAttr() {
        return fileAttr;
    }

    @Override
    public String getFileType() {
        return fileType;
    }
}
