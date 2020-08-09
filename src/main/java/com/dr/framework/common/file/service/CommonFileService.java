package com.dr.framework.common.file.service;

import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.model.FileMeta;
import com.dr.framework.common.file.FileResource;

import java.io.IOException;
import java.util.List;

/**
 * 通用附件处理接口
 * <p>
 * TODO 文件操作日志
 * TODO 文件权限
 * TODO 1、关联文件冗余删除2、文件保存成功，数据库操作失败的数据
 *
 * @author dr
 */
public interface CommonFileService {

    String DEFAULT_REF_TYPE = "default";
    String DEFAULT_GROUP_CODE = "default";

    /*
     * ===================================
     * 添加
     * ===================================
     */

    default FileInfo addFile(FileResource file, String refId) throws IOException {
        return addFile(file, refId, DEFAULT_REF_TYPE);
    }

    default FileInfo addFile(FileResource file, String refId, String refType) throws IOException {
        return addFile(file, refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 添加附件，默认追加到最后一条
     *
     * @param file
     * @return
     */
    default FileInfo addFile(FileResource file, String refId, String refType, String groupCode) throws IOException {
        return addFileLast(file, refId, refType, groupCode);
    }

    default FileInfo addFileFirst(FileResource file, String refId) throws IOException {
        return addFileFirst(file, refId, DEFAULT_REF_TYPE);
    }

    default FileInfo addFileFirst(FileResource file, String refId, String refType) throws IOException {
        return addFileFirst(file, refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 在指定业务类型的最前面插入数据
     *
     * @param file
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo addFileFirst(FileResource file, String refId, String refType, String groupCode) throws IOException;

    default FileInfo addFileLast(FileResource file, String refId) throws IOException {
        return addFileLast(file, refId, DEFAULT_REF_TYPE);
    }

    default FileInfo addFileLast(FileResource file, String refId, String refType) throws IOException {
        return addFileLast(file, refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 在指定业务类型的最侯面插入数据
     *
     * @param file
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo addFileLast(FileResource file, String refId, String refType, String groupCode) throws IOException;

    /**
     * 在指定的文件之前插入一条数据
     * <p>
     * 两个文件的业务外键，业务类型，还有分组类型都会是一样的
     *
     * @param file   附件基本信息
     * @param fileId 在那个文件之前插入
     * @return
     */
    FileInfo addFileBefore(FileResource file, String fileId) throws IOException;

    /**
     * 在指定的文件之后插入一条数据
     * <p>
     * 两个文件的业务外键，业务类型，还有分组类型都会是一样的
     *
     * @param file   附件基本信息
     * @param fileId 在那个文件之前插入
     * @return
     */
    FileInfo addFileAfter(FileResource file, String fileId) throws IOException;

    /**
     * 给附件添加元数据信息，如果有元数据信息，则更新
     *
     * @param fileId
     * @param key
     * @param value
     */
    void setMetaToFile(String fileId, String key, String value);

    /**
     * 给附件基本信息添加元数据信息，如果已经有指定的key，则更新
     *
     * @param fileId
     * @param key
     * @param value
     */
    void setMetaToBaseFile(String fileId, String key, String value);

    /*
     * ===================================
     * 修改
     * ===================================
     */

    /**
     * 互换两个文件的排序
     *
     * @param firstFileId
     * @param secondFileId
     */
    long switchOrder(String firstFileId, String secondFileId);

    /**
     * 挪到第一个
     *
     * @param fileId
     */
    long moveFirst(String fileId);

    /**
     * 挪到最后一个
     *
     * @param fileId
     */

    long moveLast(String fileId);

    /**
     * 向前移动文件
     *
     * @param fileId
     */
    long moveForward(String fileId);

    /**
     * 向后移动文件
     *
     * @param fileId
     */
    long moveBack(String fileId);

    /*
     * ===================================
     * 查询
     * ===================================
     */
    default FileInfo firstFile(String refId) {
        return firstFile(refId, DEFAULT_REF_TYPE);
    }

    default FileInfo firstFile(String refId, String refType) {
        return firstFile(refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 根据条件查询第一个附件
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo firstFile(String refId, String refType, String groupCode);

    default FileInfo lastFile(String refId) {
        return firstFile(refId, DEFAULT_REF_TYPE);
    }

    default FileInfo lastFile(String refId, String refType) {
        return firstFile(refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 根据条件查询最后一个附件
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo lastFile(String refId, String refType, String groupCode);

    /**
     * 根据关联Id获取文件信息
     *
     * @param fileId
     * @return
     */
    FileInfo fileInfo(String fileId);

    /**
     * 获取指定文件的下一个文件
     *
     * @param fileId
     * @return
     */
    FileInfo nextFile(String fileId);

    /**
     * 获取指定文件的下一个文件
     *
     * @param fileId
     * @return
     */
    FileInfo preFile(String fileId);

    /**
     * 获取指定文件的元数据
     *
     * @param fileId 可以是基本文件Id，也可以是关联文件Id
     * @return
     */
    default List<FileMeta> getFileMeta(String fileId) {
        return getFileMeta(fileId, true);
    }

    /**
     * 获取指定文件的元数据
     *
     * @param fileId          可以是基本文件Id，也可以是关联文件Id
     * @param includeBaseFile 是否包含基本文件的元数据信息
     * @return
     */
    List<FileMeta> getFileMeta(String fileId, boolean includeBaseFile);

    default List<FileInfo> list(String refId) {
        return list(refId, DEFAULT_REF_TYPE);
    }

    default List<FileInfo> list(String refId, String refType) {
        return list(refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 获取指定条件的附件列表
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    List<FileInfo> list(String refId, String refType, String groupCode);

    default long count(String refId) {
        return count(refId, DEFAULT_REF_TYPE);
    }

    default long count(String refId, String refType) {
        return count(refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 计数
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    long count(String refId, String refType, String groupCode);

    default long size(String refId) {
        return size(refId, DEFAULT_REF_TYPE);
    }

    default long size(String refId, String refType) {
        return size(refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 计算附件大小
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    long size(String refId, String refType, String groupCode);

    /*
     * ===================================
     * 删除
     * ===================================
     */

    /**
     * 根据文件Id删除文件
     *
     * @param fileId
     */
    long removeFile(String fileId);

    default long removeFileByRef(String refId) {
        return removeFileByRef(refId, DEFAULT_REF_TYPE);
    }

    default long removeFileByRef(String refId, String refType) {
        return removeFileByRef(refId, refType, DEFAULT_GROUP_CODE);
    }

    /**
     * 根据业务外键，业务类型，分组删除所有附件
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    long removeFileByRef(String refId, String refType, String groupCode);
}
