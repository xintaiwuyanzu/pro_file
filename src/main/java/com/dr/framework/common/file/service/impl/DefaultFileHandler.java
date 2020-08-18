package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.BaseFile;
import com.dr.framework.common.file.FileResource;
import com.dr.framework.common.file.FileSaveHandler;
import com.dr.framework.common.file.autoconfig.CommonFileConfig;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * 默认的文件处理器
 *
 * @author dr
 */
@Component
public class DefaultFileHandler implements FileSaveHandler, InitializingBean {
    @Autowired
    protected CommonFileConfig fileConfig;
    /**
     * 默认获取jar包所在位置
     */
    protected ApplicationHome applicationHome;

    @Override
    public boolean canHandle(BaseFile fileInfo) {
        return true;
    }

    protected String buildFilePath(BaseFile fileInfo) {
        Date date = new Date(fileInfo.getSaveDate());
        return String.join(
                File.separator,
                getRoot(),
                //文件类型
                StringUtils.isEmpty(fileInfo.getSuffix()) ? "default" : fileInfo.getSuffix(),
                //年月日
                DateFormatUtils.format(date, "yyyy"),
                DateFormatUtils.format(date, "MM"),
                DateFormatUtils.format(date, "dd"),
                //文件名称
                fileInfo.getBaseFileId() + "." + fileInfo.getSuffix()
        );
    }

    protected String getRoot() {
        if (!StringUtils.isEmpty(fileConfig.getFileLocation())) {
            return fileConfig.getFileLocation();
        } else {
            return applicationHome.getDir().getPath() + File.separator + fileConfig.getRootDirName();
        }
    }

    @Override
    public void writeFile(FileResource file, BaseFile fileInfo) throws IOException {
        String path = buildFilePath(fileInfo);
        File sysFile = new File(path);
        if (!sysFile.getParentFile().exists()) {
            sysFile.getParentFile().mkdirs();
        }
        StreamUtils.copy(file.getInputStream(), new FileOutputStream(sysFile));
    }

    @Override
    public InputStream readFile(BaseFile fileInfo) throws IOException {
        String path = buildFilePath(fileInfo);
        File sysFile = new File(path);
        if (!sysFile.exists()) {
            throw new IOException("指定的文件不存在:" + sysFile.getPath());
        }
        return new FileInputStream(sysFile);
    }

    @Override
    public void deleteFile(BaseFile fileInfo) {
        String path = buildFilePath(fileInfo);
        File sysFile = new File(path);
        if (sysFile.exists()) {
            sysFile.delete();
        }
    }

    @Override
    public InputStream openStream(BaseFile fileInfo) throws IOException {
        String path = buildFilePath(fileInfo);
        return new FileInputStream(path);
    }

    @Override
    public boolean copyTo(BaseFile fileInfo, String newFile) throws IOException {
        String path = buildFilePath(fileInfo);

        File file = new File(newFile);
        Assert.isTrue(!file.exists(), "指定的文件已存在！");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Files.copy(Paths.get(path), Paths.get(newFile));
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationHome = new ApplicationHome();
    }
}
