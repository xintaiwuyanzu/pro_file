package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.file.FileInfoHandler;
import com.dr.framework.common.file.FileSaveHandler;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.security.SecurityHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 实现类里面的代码太多了，一些简单的抽象放到这个里面实现
 * <p>
 * 子类只处理逻辑
 */
abstract class AbstractCommonFileService implements InitializingBean, ApplicationContextAware {
    protected static Logger logger = LoggerFactory.getLogger(CommonFileService.class);
    @Autowired
    protected CommonMapper commonMapper;
    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    protected FileInfoHandler fileInfoHandler;
    protected FileSaveHandler fileSaveHandler;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    protected FileRelation file(String fileId) {
        FileRelation fileRelation = commonMapper.selectOneByQuery(buildFileIdQuery(buildRelationQuery(), fileId));
        Assert.isTrue(fileRelation != null, "未找到指定的文件");
        return fileRelation;
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    protected FileRelation first(String refId, String refType, String groupCode) {
        return commonMapper.selectOneByQuery(buildFirstQuery(buildParamsQuery(buildRelationQuery(), refId, refType, groupCode)));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    protected FileRelation last(String refId, String refType, String groupCode) {
        return commonMapper.selectOneByQuery(buildLastQuery(buildParamsQuery(buildRelationQuery(), refId, refType, groupCode)));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    protected FileRelation next(String fileId) {
        return commonMapper.selectOneByQuery(buildNextQuery(buildRelationQuery(), fileId));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    protected FileRelation pre(String fileId) {
        return commonMapper.selectOneByQuery(buildPreQuery(buildRelationQuery(), fileId));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo fileInfo(String fileId) {
        return commonMapper.selectOneByQuery(buildFileIdQuery(buildFileInfoQuery(), fileId));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo firstFile(String refId, String refType, String groupCode) {
        return commonMapper.selectOneByQuery(buildFirstQuery(buildParamsQuery(buildFileInfoQuery(), refId, refType, groupCode)));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo lastFile(String refId, String refType, String groupCode) {
        return commonMapper.selectOneByQuery(buildLastQuery(buildParamsQuery(buildFileInfoQuery(), refId, refType, groupCode)));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo nextFile(String fileId) {
        return commonMapper.selectOneByQuery(buildNextQuery(buildFileInfoQuery(), fileId));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo preFile(String fileId) {
        return commonMapper.selectOneByQuery(buildPreQuery(buildFileInfoQuery(), fileId));
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
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

                        FileRelationInfo.ID,
                        FileRelationInfo.NEXTID,
                        FileRelationInfo.PREID,
                        FileRelationInfo.REFID,
                        FileRelationInfo.REFTYPE,
                        FileRelationInfo.GROUPCODE,
                        FileRelationInfo.DESCRIPTION,
                        FileRelationInfo.CREATEDATE.alias("saveDate")
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
        fileSaveHandler = new FileHandlerComposite(
                applicationContext.getBeansOfType(FileSaveHandler.class)
                        .values()
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
