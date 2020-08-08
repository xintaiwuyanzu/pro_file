package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.autoconfig.CommonFileConfig;
import com.dr.framework.common.file.model.BaseFile;
import com.dr.framework.common.file.model.FileResource;
import com.dr.framework.common.file.service.FileHandler;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Date;

/**
 * 默认的文件处理器
 *
 * @author dr
 */
@Component
public class DefaultFileHandler implements FileHandler, InitializingBean {
    @Autowired
    protected CommonFileConfig fileConfig;
    //默认获取jar包所在位置
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
                fileInfo.getSuffix(),
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
            return applicationHome.getSource().getParent() + File.separator + fileConfig.getRootDirName();
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
    public void afterPropertiesSet() throws Exception {
        applicationHome = new ApplicationHome();
    }
}
