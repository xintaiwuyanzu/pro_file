package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.file.FileInfoHandler;
import com.dr.framework.common.file.FileResource;
import com.dr.framework.common.file.FileSaveHandler;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.model.FileMeta;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.SecurityHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 实现类里面的代码太多了，一些简单的抽象放到这个里面实现
 * <p>
 * 子类只处理逻辑
 */
abstract class AbstractCommonFileService implements CommonFileService, InitializingBean {
    protected static Logger logger = LoggerFactory.getLogger(CommonFileService.class);
    @Autowired
    protected CommonMapper commonMapper;
    @Autowired
    protected FileInfoHandler fileInfoHandler;
    @Autowired
    protected FileSaveHandler fileSaveHandler;
    @Autowired
    CacheManager cacheManager;
    protected Cache cache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFile(FileResource file, String refId) throws IOException {
        return addFile(file, refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFile(FileResource file, String refId, String refType) throws IOException {
        return addFile(file, refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFile(FileResource file, String refId, String refType, String groupCode) throws IOException {
        return addFileLast(file, refId, refType, groupCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFile(String hash, String refId) {
        return addFile(hash, refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFile(String hash, String refId, String refType) {
        return addFile(hash, refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFile(String hash, String refId, String refType, String groupCode) {
        return addFileLast(hash, refId, refType, groupCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(FileResource file, String refId) throws IOException {
        return addFileFirst(file, refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(FileResource file, String refId, String refType) throws IOException {
        return addFileFirst(file, refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(String hash, String refId) {
        return addFileFirst(hash, refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(String hash, String refId, String refType) {
        return addFileFirst(hash, refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileLast(FileResource file, String refId) throws IOException {
        return addFileLast(file, refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileLast(String hash, String refId) {
        return addFileLast(hash, refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileLast(String hash, String refId, String refType) {
        return addFileLast(hash, refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileLast(FileResource file, String refId, String refType) throws IOException {
        return addFileLast(file, refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo firstFile(String refId) {
        return firstFile(refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo firstFile(String refId, String refType) {
        return firstFile(refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo lastFile(String refId) {
        return firstFile(refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo lastFile(String refId, String refType) {
        return firstFile(refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileMeta> getFileMeta(String fileId) {
        return getFileMeta(fileId, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileInfo> list(String refId) {
        return list(refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileInfo> list(String refId, String refType) {
        return list(refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long count(String refId) {
        return count(refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long count(String refId, String refType) {
        return count(refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long size(String refId) {
        return size(refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long size(String refId, String refType) {
        return size(refId, refType, DEFAULT_GROUP_CODE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeFileByRef(String refId) {
        return removeFileByRef(refId, DEFAULT_REF_TYPE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeFileByRef(String refId, String refType) {
        return removeFileByRef(refId, refType, DEFAULT_GROUP_CODE);
    }

    @Transactional(rollbackFor = Exception.class)
    protected FileRelation file(String fileId) {
        FileRelation fileRelation = commonMapper.selectOneByQuery(buildFileIdQuery(buildRelationQuery(), fileId));
        Assert.isTrue(fileRelation != null, "未找到指定的文件");
        return fileRelation;
    }

    @Transactional(rollbackFor = Exception.class)
    protected FileRelation first(String refId, String refType, String groupCode) {
        return commonMapper.selectOneByQuery(buildFirstQuery(buildParamsQuery(buildRelationQuery(), refId, refType, groupCode)));
    }

    @Transactional(rollbackFor = Exception.class)
    public FileRelation last(String refId, String refType, String groupCode) {
        FileRelation relation = commonMapper.selectOneByQuery(buildLastQuery(buildParamsQuery(buildRelationQuery(), refId, refType, groupCode)));
        if (relation == null) {
            String cacheKey = buildCacheKey(refId, refType, groupCode);
        }
        return relation;
    }


    @Transactional(rollbackFor = Exception.class)
    protected FileRelation next(String fileId) {
        return commonMapper.selectOneByQuery(buildNextQuery(buildRelationQuery(), fileId));
    }

    @Transactional(rollbackFor = Exception.class)
    protected FileRelation pre(String fileId) {
        return commonMapper.selectOneByQuery(buildPreQuery(buildRelationQuery(), fileId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo fileInfo(String fileId) {
        return commonMapper.selectOneByQuery(buildFileIdQuery(buildFileInfoQuery(), fileId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo firstFile(String refId, String refType, String groupCode) {
        return commonMapper.selectOneByQuery(buildFirstQuery(buildParamsQuery(buildFileInfoQuery(), refId, refType, groupCode)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo lastFile(String refId, String refType, String groupCode) {
        return commonMapper.selectOneByQuery(buildLastQuery(buildParamsQuery(buildFileInfoQuery(), refId, refType, groupCode)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo nextFile(String fileId) {
        return commonMapper.selectOneByQuery(buildNextQuery(buildFileInfoQuery(), fileId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo preFile(String fileId) {
        return commonMapper.selectOneByQuery(buildPreQuery(buildFileInfoQuery(), fileId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileInfo> list(String refId, String refType, String groupCode) {
        List<DefaultFileInfo> list = commonMapper.selectByQuery(buildParamsQuery(buildFileInfoQuery(), refId, refType, groupCode));
        if (list.isEmpty()) {
            return Collections.emptyList();
        } else if (list.size() == 1) {
            return new LinkedList<>(list);
        } else {
            Map<String, DefaultFileInfo> idMap = new HashMap<>(list.size() - 1);
            LinkedList<FileInfo> linkedList = new LinkedList<>();
            DefaultFileInfo key = null;
            for (DefaultFileInfo f : list) {
                if (f.getPreId() == null) {
                    key = f;
                } else {
                    idMap.put(f.getId(), f);
                }
            }
            linkedList.addFirst(key);
            while (key.getNextId() != null) {
                key = idMap.get(key.getNextId());
                linkedList.addLast(key);
            }
            return linkedList;
        }
    }

    protected SqlQuery<DefaultFileInfo> buildFileInfoQuery() {
        return SqlQuery.from(FileRelation.class, false)
                .column(
                        FileBaseInfoInfo.ID.alias("baseFileId"),
                        FileBaseInfoInfo.ORIGINNAME.alias("name"),
                        FileBaseInfoInfo.SUFFIX,
                        FileBaseInfoInfo.ORIGINCREATEDATE.alias("createDate"),
                        FileBaseInfoInfo.LASTMODIFYDATE,
                        FileBaseInfoInfo.FILESIZE,
                        FileBaseInfoInfo.FILEHASH,
                        FileBaseInfoInfo.MIMETYPE,
                        FileBaseInfoInfo.CREATEDATE.alias("saveDate"),
                        FileBaseInfoInfo.FILETYPE,
                        FileBaseInfoInfo.FILEATTR,

                        FileRelationInfo.ID,
                        FileRelationInfo.NEXTID,
                        FileRelationInfo.PREID,
                        FileRelationInfo.REFID,
                        FileRelationInfo.REFTYPE,
                        FileRelationInfo.GROUPCODE,
                        FileRelationInfo.DESCRIPTION
                )
                .join(FileRelationInfo.FILEID, FileBaseInfoInfo.ID)
                .setReturnClass(DefaultFileInfo.class);
    }

    protected SqlQuery<FileRelation> buildRelationQuery() {
        return SqlQuery.from(FileRelation.class, false)
                .column(
                        FileRelationInfo.ID,
                        FileRelationInfo.NEXTID,
                        FileRelationInfo.PREID,
                        FileRelationInfo.FILEID
                );
    }


    protected <T> SqlQuery<T> buildParamsQuery(SqlQuery<T> sqlQuery, String refId, String refType, String groupCode) {
        Assert.isTrue(!StringUtils.isEmpty(refId), "业务外键不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(refType), "业务类型不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(groupCode), "文件分组不能为空！");
        return sqlQuery
                .equal(FileRelationInfo.REFID, refId)
                .equal(FileRelationInfo.REFTYPE, refType)
                .equal(FileRelationInfo.GROUPCODE, groupCode);
    }

    protected <T> SqlQuery<T> buildFileIdQuery(SqlQuery<T> sqlQuery, String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件Id不能为空！");
        return sqlQuery.equal(FileRelationInfo.ID, fileId);
    }

    protected <T> SqlQuery<T> buildPreQuery(SqlQuery<T> sqlQuery, String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件Id不能为空！");
        return sqlQuery.equal(FileRelationInfo.NEXTID, fileId);
    }

    protected <T> SqlQuery<T> buildNextQuery(SqlQuery<T> sqlQuery, String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件Id不能为空！");
        return sqlQuery.equal(FileRelationInfo.PREID, fileId);
    }

    protected <T> SqlQuery<T> buildFirstQuery(SqlQuery<T> sqlQuery) {
        return sqlQuery.isNull(FileRelationInfo.PREID);
    }

    protected <T> SqlQuery<T> buildLastQuery(SqlQuery<T> sqlQuery) {
        return sqlQuery.isNull(FileRelationInfo.NEXTID);
    }

    protected long updateRelation(String updateId, String relationId, boolean isRelationPre) {
        if (StringUtils.isEmpty(updateId)) {
            return 0;
        }
        return commonMapper.updateByQuery(
                SqlQuery.from(FileRelation.class, false)
                        .set(isRelationPre ? FileRelationInfo.PREID : FileRelationInfo.NEXTID, relationId)
                        .set(FileRelationInfo.UPDATEPERSON, getCurrentPersonId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .equal(FileRelationInfo.ID, updateId)
        );
    }


    protected String getCurrentPersonId() {
        SecurityHolder securityHolder = SecurityHolder.get();
        if (securityHolder != null && securityHolder.currentPerson() != null) {
            return securityHolder.currentPerson().getId();
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        cache = cacheManager.getCache("common_files");
    }
    /**
     * 根据各种外键构造缓存外键
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */
    protected String buildCacheKey(String refId, String refType, String groupCode) {
        return String.join("-", refId, refType, groupCode).toLowerCase();
    }

}
