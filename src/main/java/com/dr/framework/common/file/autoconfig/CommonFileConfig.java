package com.dr.framework.common.file.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dr.common.file")
public class CommonFileConfig {
    /**
     * 所有文件存放位置
     */
    private String fileLocation;
    /**
     * 最大文件上传大小
     */
    private long maxUploadSize;
    /**
     * 跟文件夹名称
     */
    private String rootDirName = "files";


    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(long maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }

    public String getRootDirName() {
        return rootDirName;
    }

    public void setRootDirName(String rootDirName) {
        this.rootDirName = rootDirName;
    }
}
