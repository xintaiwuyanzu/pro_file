package com.dr.framework.common.file.service;

import com.dr.framework.common.file.FileInfo;
import com.dr.framework.common.file.model.SimpleFile;

/**
 * 通用附件处理接口
 * <p>
 * TODO 文件操作日志
 * TODO 文件权限
 *
 * @author dr
 */
public interface CommonFileService {
    /**
     * 添加附件
     *
     * @param file
     * @return
     */
    FileInfo addFile(SimpleFile file);

    //添加元数据
    void addMetaToFile(String fileId, String key, String value);

    void addMetaToBaseFile(String fileId, String key, String value);

    //删除
    //查询
}
