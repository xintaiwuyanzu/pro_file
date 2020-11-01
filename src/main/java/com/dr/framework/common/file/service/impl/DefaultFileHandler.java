package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.BaseFile;
import com.dr.framework.common.file.FileResource;
import com.dr.framework.common.file.FileSaveHandler;
import com.dr.framework.common.file.autoconfig.CommonFileConfig;
import com.dr.framework.core.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DefaultFileHandler implements FileSaveHandler {
    @Autowired
    protected CommonFileConfig fileConfig;

    @Override
    public boolean canHandle(BaseFile fileInfo) {
        return true;
    }

    protected String buildFilePath(BaseFile fileInfo) {
        Date date = new Date(fileInfo.getSaveDate());
        String mineType = StringUtils.isEmpty(fileInfo.getSuffix()) ? Constants.DEFAULT : fileInfo.getSuffix();
        return String.join(
                File.separator,
                fileConfig.getFullDirPath(null, mineType, date),
                fileInfo.getBaseFileId() + "." + fileInfo.getSuffix()
        );
    }

    @Override
    public void writeFile(FileResource file, BaseFile fileInfo) throws IOException {
        String path = buildFilePath(fileInfo);
        File sysFile = new File(path);
        if (!sysFile.getParentFile().exists()) {
            sysFile.getParentFile().mkdirs();
        }
        FileOutputStream outputStream = new FileOutputStream(sysFile);
        InputStream inputStream = file.getInputStream();
        try {
            StreamUtils.copy(inputStream, outputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            outputStream.close();
        }
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
            deleteEmptyDir(sysFile.getParentFile());
        }
    }

    private void deleteEmptyDir(File parent) {
        boolean con = parent != null && parent.exists() && parent.isDirectory();
        String[] files = parent.list();
        con = con && (files == null || files.length == 0);
        if (con) {
            parent.delete();
            if (!parent.equals(new File(fileConfig.getRootDirName()))) {
                deleteEmptyDir(parent.getParentFile());
            }
        }
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
}
