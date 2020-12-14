package com.dr.framework.common.file;

import com.dr.framework.common.file.model.FileInfo;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件下载管理器
 *
 * @author dr
 */
public interface FileDownLoadHandler extends Ordered {
    /**
     * 能不能处理指定的文件
     *
     * @param fileInfo
     * @return
     */
    boolean canHandle(FileInfo fileInfo);

    /**
     * 下载文件上传文件
     *
     * @param fileInfo
     * @param download
     * @param request
     * @param response
     */
    void downLoadFile(FileInfo fileInfo, boolean download, HttpServletRequest request, HttpServletResponse response);
}
