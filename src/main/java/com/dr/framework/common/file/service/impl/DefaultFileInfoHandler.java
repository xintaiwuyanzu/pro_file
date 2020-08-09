package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.FileInfoHandler;
import com.dr.framework.common.file.FileResource;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 借助tika获取文件类型
 *
 * @author dr
 */
public class DefaultFileInfoHandler implements FileInfoHandler {
    final Logger logger = LoggerFactory.getLogger(DefaultFileInfoHandler.class);
    final Tika tika = new Tika();

    @Override
    public String fileMine(FileResource resource) {
        try {
            return tika.detect(resource.getInputStream(), resource.getName());
        } catch (Exception e) {
            logger.warn("获取文件mine类型失败", e);
            return resource.getSuffix();
        }
    }

    class FileResourceByteSource extends ByteSource {
        FileResource resource;

        FileResourceByteSource(FileResource resource) {
            this.resource = resource;
        }

        @Override
        public InputStream openStream() throws IOException {
            return resource.getInputStream();
        }
    }

    @Override
    public String fileHash(FileResource resource) {
        try {
            return new FileResourceByteSource(resource).hash(Hashing.sha512()).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
    }
}
