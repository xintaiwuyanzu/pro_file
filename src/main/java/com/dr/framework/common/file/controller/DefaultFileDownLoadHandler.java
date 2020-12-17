package com.dr.framework.common.file.controller;

import com.dr.framework.common.file.FileDownLoadHandler;
import com.dr.framework.common.file.FileSaveHandler;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.service.impl.FileHandlerComposite;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailabilityBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 默认的文件下载处理器
 *
 * @author dr
 */
@Component
public class DefaultFileDownLoadHandler extends ApplicationAvailabilityBean implements FileDownLoadHandler, InitializingBean {
    FileSaveHandler fileSaveHandler;

    @Override
    public void downLoadFile(FileInfo fileInfo, boolean download, HttpServletRequest request, HttpServletResponse response) {
        //TODO 有些文件可以缓存的
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        if (download) {
            String fileName = new String(fileInfo.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", fileName));
        }
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setHeader(HttpHeaders.EXPIRES, "0");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileInfo.getFileSize()));
        try {
            MediaType mediaType = MediaType.parseMediaType(fileInfo.getMimeType());
            response.setContentType(mediaType.toString());
        } catch (Exception e) {

        }
        try (InputStream inputStream = fileSaveHandler.readFile(fileInfo)) {
            StreamUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canHandle(FileInfo fileInfo) {
        return true;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Autowired
    List<FileSaveHandler> fileSaveHandlerList;

    @Override
    public void afterPropertiesSet() {
        fileSaveHandler = new FileHandlerComposite(fileSaveHandlerList);
    }
}
