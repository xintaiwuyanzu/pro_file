package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.FileInfoHandler;
import com.dr.framework.common.file.FileResource;
import org.apache.commons.codec.digest.DigestUtils;
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

    @Override
    public String fileHash(InputStream stream) {
        try {
            return DigestUtils.sha512Hex(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return UUID.randomUUID().toString();
        }
    }
}
