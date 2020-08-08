package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.common.file.service.FileHandler;
import com.dr.framework.common.file.service.FileMineHandler;
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

import java.util.ArrayList;
import java.util.List;

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
    protected FileMineHandler mineHandler;

    protected FileHandler fileHandler;

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
        return new ArrayList<>(commonMapper.selectByQuery(buildParamsQuery(buildFileInfoQuery(), refId, refType, groupCode)));
    }

    public static SqlQuery<DefaultFileInfo> buildFileInfoQuery() {
        return SqlQuery.from(FileRelation.class, false)
                .column(
                        FileBaseInfoInfo.ID.alias("baseFileId"),
                        FileBaseInfoInfo.ORIGINNAME.alias("name"),
                        FileBaseInfoInfo.SUFFIX,
                        FileBaseInfoInfo.ORIGINCREATEDATE.alias("createDate"),
                        FileBaseInfoInfo.LASTMODIFYDATE,
                        FileBaseInfoInfo.SIZE.alias("fileSize"),
                        FileBaseInfoInfo.HASH.alias("fileHash"),
                        FileBaseInfoInfo.MIMETYPE.alias("mine"),

                        FileRelationInfo.ID,
                        FileRelationInfo.NEXTID,
                        FileRelationInfo.REFID,
                        FileRelationInfo.REFTYPE,
                        FileRelationInfo.GROUPCODE,
                        FileRelationInfo.DESCRIPTION,
                        FileRelationInfo.CREATEDATE.alias("saveDate")
                )
                .join(FileRelationInfo.FILEID, FileBaseInfoInfo.ID)
                .setReturnClass(DefaultFileInfo.class);
    }

    public static SqlQuery<FileRelation> buildRelationQuery() {
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

    protected String getCurrentPersonId() {
        SecurityHolder securityHolder = SecurityHolder.get();
        if (securityHolder != null && securityHolder.currentPerson() != null) {
            return securityHolder.currentPerson().getId();
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        fileHandler = new FileHandlerComposite(
                applicationContext.getBeansOfType(FileHandler.class)
                        .values()
        );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
