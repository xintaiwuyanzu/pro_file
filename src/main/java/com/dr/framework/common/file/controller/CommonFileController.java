package com.dr.framework.common.file.controller;

import com.dr.framework.common.entity.ResultEntity;
import com.dr.framework.common.file.autoconfig.CommonFileConfig;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.resource.MultipartFileResource;
import com.dr.framework.common.file.service.CommonFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    public ResultEntity upload(@RequestParam("file") MultipartFile[] files,
                               @RequestParam(name = "refId") String refId,
                               @RequestParam(name = "refType", defaultValue = CommonFileService.DEFAULT_REF_TYPE) String refType,
                               @RequestParam(name = "groupCode", defaultValue = CommonFileService.DEFAULT_GROUP_CODE) String groupCode) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null) {
                try {
                    fileInfos.add(fileService.addFile(new MultipartFileResource(file), refId, refType, groupCode));
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
    public ResponseEntity<InputStreamSource> downLoad(@PathVariable(name = "fileId") String fileId) throws IOException {
        FileInfo fileInfo = fileService.fileInfo(fileId);
        Assert.isTrue(fileInfo != null, "指定的文件不存在");
        HttpHeaders headers = new HttpHeaders();
        //TODO 缓存头
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", new String(fileInfo.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        InputStream stream = fileService.fileStream(fileInfo.getId());
        try {
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(fileInfo.getFileSize())
                    .contentType(MediaType.parseMediaType(fileInfo.getMimeType()))
                    .body(new InputStreamResource(stream, fileInfo.getDescription()));
        } finally {
            stream.close();
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
    public ResultEntity<Long> delete(@PathVariable(name = "fileId") String fileId) throws IOException {
        return ResultEntity.success(fileService.removeFile(fileId));
    }
}
