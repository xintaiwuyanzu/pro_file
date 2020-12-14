package com.dr.framework.common.file.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.file.FileDownLoadHandler;
import com.dr.framework.common.file.FileResource;
import com.dr.framework.common.file.autoconfig.CommonFileConfig;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.resource.MultipartFileResource;
import com.dr.framework.common.file.service.CommonFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 前端接口
 * TODO 插入和排序
 *
 * @author dr
 */
@RestController
@RequestMapping("${common.api-path:/api}/${common.file.webPath:files}")
public class CommonFileController {
    @Autowired
    protected CommonFileService fileService;
    @Autowired
    protected CommonFileConfig fileConfig;
    @Autowired
    protected List<FileDownLoadHandler> fileDownLoadHandlers;

    /**
     * 上传文件
     *
     * @param files
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    @PostMapping("/upload")
    public ResultEntity upload(@RequestParam(name = "file") MultipartFile[] files,
                               @RequestParam(name = "refId") String refId,
                               @RequestParam(name = "refType", defaultValue = CommonFileService.DEFAULT_REF_TYPE) String refType,
                               @RequestParam(name = "groupCode", defaultValue = CommonFileService.DEFAULT_GROUP_CODE) String groupCode,
                               @RequestParam(name = "fileType", required = false) String fileType,
                               @RequestParam(name = "fileAttr", required = false) String fileAttr) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null) {
                try {
                    FileResource fileResource = new MultipartFileResource(file, fileType, fileAttr);
                    fileInfos.add(fileService.addFile(fileResource, refId, refType, groupCode));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (fileInfos.isEmpty()) {
            return ResultEntity.error("未上传文件!");
        } else if (fileInfos.size() == 1) {
            return ResultEntity.success(fileInfos.get(0));
        } else {
            return ResultEntity.success(fileInfos);
        }
    }

    /**
     * 指定hash的文件是否存在
     *
     * @param hash
     * @return
     */
    @PostMapping("/hashExist")
    public ResultEntity<Boolean> hashExist(@RequestParam(name = "hash") String hash) {
        return ResultEntity.success(fileService.existByHash(hash));
    }

    /**
     * 根据hash创建文件关联
     *
     * @param hash
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    @PostMapping("/uploadByHash")
    public ResultEntity<FileInfo> uploadByHash(
            @RequestParam(name = "hash") String hash,
            @RequestParam(name = "refId") String refId,
            @RequestParam(name = "refType", defaultValue = CommonFileService.DEFAULT_REF_TYPE) String refType,
            @RequestParam(name = "groupCode", defaultValue = CommonFileService.DEFAULT_GROUP_CODE) String groupCode) {
        return ResultEntity.success(
                fileService.addFile(hash, refId, refType, groupCode)
        );
    }

    /**
     * 下载文件
     * TODO 详细的参数，是否下载，判断请求头，判断图片
     *
     * @param fileId
     * @return
     */
    @RequestMapping("/downLoad/{fileId}")
    public void downLoad(
            @PathVariable(name = "fileId") String fileId,
            @RequestParam(defaultValue = "true", name = "download") boolean download,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        FileInfo fileInfo = fileService.fileInfo(fileId);
        Assert.isTrue(fileInfo != null, "指定的文件不存在");
        for (FileDownLoadHandler handler : fileDownLoadHandlers) {
            if (handler.canHandle(fileInfo)) {
                handler.downLoadFile(fileInfo, download, request, response);
                return;
            }
        }
    }

    /**
     * 获取文件列表
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    @PostMapping("/list")
    public ResultEntity<List<FileInfo>> list(
            @RequestParam(name = "refId") String refId,
            @RequestParam(name = "refType", defaultValue = CommonFileService.DEFAULT_REF_TYPE) String refType,
            @RequestParam(name = "groupCode", defaultValue = CommonFileService.DEFAULT_GROUP_CODE) String groupCode) {
        return ResultEntity.success(fileService.list(refId, refType, groupCode));
    }

    /**
     * 删除文件
     *
     * @param fileId
     * @return
     * @throws IOException
     */
    @PostMapping("/delete/{fileId}")
    public ResultEntity<Long> delete(@PathVariable(name = "fileId") String fileId) {
        return ResultEntity.success(fileService.removeFile(fileId));
    }
}
