package com.dr.framework.common.file.autoconfig;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件相关的配置
 *
 * @author dr
 */
@ConfigurationProperties(prefix = "common.file")
public class CommonFileConfig implements InitializingBean, WebMvcConfigurer {
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
    /*
     *=============================
     * 上面都是配置属性
     * 下面都是工具方法
     *=============================
     */
    /**
     * 根路径文件夹
     */
    private File rootDir;
    /**
     * 根路径文件夹全名
     */
    private String rootDirFullPath;
    /**
     * 上传附件路径
     */
    public static final String uploadDirName = "upload";

    /**
     * 获取根路径
     *
     * @return
     */
    public File getRootDir() {
        return rootDir;
    }

    /**
     * 获取根路径完整路径
     *
     * @return
     */
    public String getRootDirFullPath() {
        return rootDirFullPath;
    }

    /**
     * 根据子文件夹路径和文件类型获取文件夹全路径
     * <p>
     * 文件以当前日期自动分割
     *
     * @param dir
     * @param mineType
     * @return
     */
    public String getFullDirPathWithCurrentDate(String dir, String mineType) {
        return getFullDirPath(dir, mineType, System.currentTimeMillis());
    }

    public String getFullDirPath(@Nullable String dir, @Nullable String mineType, @Nullable long time) {
        return getFullDirPath(dir, mineType, new Date(time));
    }

    /**
     * 根据子文件夹，文件类型，日期获取文件夹全路径
     * 同时创建文件夹
     *
     * @param dir      子文件夹名称，可为空
     * @param mineType 媒体类型，可为空
     * @param date     文件日期，可为空
     * @return
     */
    public String getFullDirPath(@Nullable String dir, @Nullable String mineType, @Nullable Date date) {
        List<String> paths = new ArrayList<>();
        paths.add(getRootDirFullPath());
        if (!StringUtils.isEmpty(dir)) {
            //子文件夹
            paths.add(dir);
        }
        if (!StringUtils.isEmpty(mineType)) {
            //文件类型
            paths.add(mineType);
        }
        if (date != null) {
            //年月日
            paths.add(DateFormatUtils.format(date, "yyyy"));
            paths.add(DateFormatUtils.format(date, "MM"));
            paths.add(DateFormatUtils.format(date, "MM"));
        }
        String fullPath = String.join(File.separator, paths);
        File file = new File(fullPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return fullPath;
    }

    /**
     * 获取上传文件夹路径，带有日期格式
     *
     * @param mineType
     * @return
     */
    public String getUploadDirWithDate(String mineType) {
        return getFullDirPath(uploadDirName, mineType, System.currentTimeMillis());
    }

    /**
     * 获取上传文件夹路径，不带有日期格式
     *
     * @param mineType
     * @return
     */
    public String getUploadDir(String mineType) {
        return getFullDirPath(uploadDirName, mineType, null);
    }

    /**
     * 上传文件的下载相对路径
     *
     * @param filePath
     * @return
     */
    public String getUploadDownLoadPath(String filePath) {
        Assert.isTrue(!StringUtils.isEmpty(filePath), "文件路径不能为空！");
        Path fp = Paths.get(filePath);
        Assert.isTrue(Files.isRegularFile(fp), "文件路径格式不正确！");
        String uploadDir = getUploadDir(null);
        Path dirPath = Paths.get(uploadDir);
        return "/" + uploadDirName + "/" + dirPath.relativize(fp).toString().replace(File.separator, "/");
    }

    public String uploadFile(String dir, InputStream fis, String fileName) throws IOException {
        return uploadFile(dir, fis, fileName, false);
    }

    /**
     * 上传文件到上传文件夹
     *
     * @param dir           子文件夹路径
     * @param fis           文件输入流
     * @param fileName      文件名称
     * @param useCurrentDir 是否使用当前日期创建子文件夹
     * @return
     * @throws IOException
     */
    public String uploadFile(String dir, InputStream fis, String fileName, boolean useCurrentDir) throws IOException {
        Date date = null;
        if (useCurrentDir) {
            date = new Date();
        }
        String fileDir = getFullDirPath(uploadDirName, dir, date);
        File filePath = new File(fileDir, fileName);
        FileCopyUtils.copy(fis, new FileOutputStream(filePath));
        return filePath.getAbsolutePath();
    }


    @Override
    public void afterPropertiesSet() {
        String root;
        if (!StringUtils.isEmpty(getFileLocation())) {
            root = getFileLocation();
        } else {
            ApplicationHome applicationHome = new ApplicationHome();
            root = applicationHome.getDir().getPath() + File.separator + getRootDirName();
        }
        rootDir = new File(root);
        if (!rootDir.exists()) {
            boolean mkDirs = rootDir.mkdirs();
            Assert.isTrue(mkDirs, "创建上传文件夹失败：" + mkDirs);
        }
        rootDirFullPath = rootDir.getAbsolutePath();
    }

    /**
     * 添加上传文件夹为静态资源下载路径
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + uploadDirName + "/**")
                .addResourceLocations("file:" + getUploadDir(null) + File.separator);
    }
}
