package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.model.FileResource;
import com.dr.framework.common.file.service.FileMineHandler;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 借助tika获取文件类型
 *
 * @author dr
 */
public class DefaultTikaFileMineHandler implements FileMineHandler {
    Logger logger = LoggerFactory.getLogger(DefaultTikaFileMineHandler.class);
    Tika tika = new Tika();

    @Override
    public String fileMine(FileResource resource) {
        try {
            return tika.detect(resource.getInputStream(), resource.getName());
        } catch (Exception e) {
            logger.warn("获取文件mine类型失败", e);
            return resource.getSuffix();
        }
    }
}
