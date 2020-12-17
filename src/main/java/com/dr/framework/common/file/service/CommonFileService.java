package com.dr.framework.common.file.service;

import com.dr.framework.common.file.BaseFile;
import com.dr.framework.common.file.FileResource;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.model.FileMeta;

import java.io.IOException;
import java.io.InputStream;
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

    FileInfo addFile(FileResource file, String refId) throws IOException;

    FileInfo addFile(FileResource file, String refId, String refType) throws IOException;

    /**
     * 添加附件，默认追加到最后一条
     *
     * @param file
     * @return
     */
    FileInfo addFile(FileResource file, String refId, String refType, String groupCode) throws IOException;

    FileInfo addFile(String hash, String refId);

    FileInfo addFile(String hash, String refId, String refType);

    /**
     * 根据hash添加文件
     *
     * @param hash
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo addFile(String hash, String refId, String refType, String groupCode);

    FileInfo addFileFirst(FileResource file, String refId) throws IOException;

    FileInfo addFileFirst(FileResource file, String refId, String refType) throws IOException;

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

    FileInfo addFileFirst(String hash, String refId);

    FileInfo addFileFirst(String hash, String refId, String refType);

    /**
     * 在头部根据hash添加文件
     *
     * @param hash
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo addFileFirst(String hash, String refId, String refType, String groupCode);

    FileInfo addFileLast(FileResource file, String refId) throws IOException;

    FileInfo addFileLast(FileResource file, String refId, String refType) throws IOException;

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

    FileInfo addFileLast(String hash, String refId);

    FileInfo addFileLast(String hash, String refId, String refType);

    /**
     * 根据hash在最后添加文件
     *
     * @param hash
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo addFileLast(String hash, String refId, String refType, String groupCode);

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
     * 根据hash 在指定的文件之前插入文件
     *
     * @param hash
     * @param fileId
     * @return
     */
    FileInfo addFileBefore(String hash, String fileId);

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
     * 根据hash 在指定的文件之后插入文件
     *
     * @param hash
     * @param fileId
     * @return
     */
    FileInfo addFileAfter(String hash, String fileId);

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
    FileInfo firstFile(String refId);

    FileInfo firstFile(String refId, String refType);

    /**
     * 根据条件查询第一个附件
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    FileInfo firstFile(String refId, String refType, String groupCode);

    FileInfo lastFile(String refId);

    FileInfo lastFile(String refId, String refType);

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
     * 根据hash查找文件基本信息
     *
     * @param hash
     * @return
     */
    BaseFile fileInfoByHash(String hash);

    /**
     * 指定的hash的文件是否存在
     *
     * @param hash
     * @return
     */
    boolean existByHash(String hash);

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
    List<FileMeta> getFileMeta(String fileId);

    /**
     * 获取指定文件的元数据
     *
     * @param fileId          可以是基本文件Id，也可以是关联文件Id
     * @param includeBaseFile 是否包含基本文件的元数据信息
     * @return
     */
    List<FileMeta> getFileMeta(String fileId, boolean includeBaseFile);

    List<FileInfo> list(String refId);

    List<FileInfo> list(String refId, String refType);

    /**
     * 获取指定条件的附件列表
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    List<FileInfo> list(String refId, String refType, String groupCode);

    /**
     * 获取文件输出流
     *
     * @param fileId
     * @return
     * @throws IOException
     */
    InputStream fileStream(String fileId) throws IOException;

    /**
     * 根据Hash打开文件流
     *
     * @param hash
     * @return
     * @throws IOException
     */
    InputStream fileStreamByHash(String hash) throws IOException;

    /**
     * 复制指定的文件到新文件
     *
     * @param fileId
     * @param newFile
     * @return
     * @throws IOException
     */
    boolean copyTo(String fileId, String newFile) throws IOException;

    /**
     * 根据hash复制文件到新的文件
     *
     * @param fileHash
     * @param newFile
     * @return
     * @throws IOException
     */
    boolean copyToByHash(String fileHash, String newFile) throws IOException;

    /**
     * 根据业务外键计算文件数量
     *
     * @param refId
     * @return
     */
    long count(String refId);

    /**
     * 根据业务外键和类型计算数量
     *
     * @param refId
     * @param refType
     * @return
     */
    long count(String refId, String refType);

    /**
     * 计数
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    long count(String refId, String refType, String groupCode);

    /**
     * 计算附件大小
     *
     * @param refId
     * @return
     */
    long size(String refId);

    /**
     * 计算附件大小
     *
     * @param refId
     * @param refType
     * @return
     */
    long size(String refId, String refType);

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
     * @return
     */
    long removeFile(String fileId);

    /**
     * 根据文件外键Id删除文件
     *
     * @param refId
     * @return
     */
    long removeFileByRef(String refId);

    /**
     * 根据文件外键Id删除文件
     *
     * @param refId
     * @param refType
     * @return
     */
    long removeFileByRef(String refId, String refType);

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
