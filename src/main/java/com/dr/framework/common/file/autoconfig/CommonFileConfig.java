package com.dr.framework.common.file.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

/**
 * 文件相关的配置
 *
 * @author dr
 */
@ConfigurationProperties(prefix = "common.file")
public class CommonFileConfig {
    /**
     * 网页端访问路径
     */
    private String webPath = "files";
    /**
     * 所有文件存放位置
     */
    private String fileLocation;
    /**
     * 最大文件上传大小
     */
    private DataSize maxUploadSize = DataSize.ofMegabytes(200);
    /**
     * 根文件夹名称
     */
    private String rootDirName = "files";

    public String getWebPath() {
        return webPath;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public DataSize getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(DataSize maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }

    public String getRootDirName() {
        return rootDirName;
    }

    public void setRootDirName(String rootDirName) {
        this.rootDirName = rootDirName;
    }
}
