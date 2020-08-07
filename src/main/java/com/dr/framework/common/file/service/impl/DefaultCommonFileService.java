package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.model.FileMeta;
import com.dr.framework.common.file.model.FileResource;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.common.file.service.FileHandler;
import com.dr.framework.common.file.service.FileMineHandler;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.Column;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认附件实现
 *
 * @author dr
 */
public class DefaultCommonFileService implements CommonFileService, InitializingBean, ApplicationContextAware {
    protected static Logger logger = LoggerFactory.getLogger(CommonFileService.class);
    @Autowired
    protected CommonMapper commonMapper;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    FileMineHandler mineHandler;

    FileHandler fileHandler;

    @FunctionalInterface
    interface RelationSaveCallBack {
        /**
         * 保存回调
         *
         * @param relation
         */
        void onBeforeSave(FileRelation relation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(FileResource file, String refId, String refType, String groupCode) throws IOException {
        //如果第一个文件没有的话，说明是第一个文件
        FileInfo firstFile = firstFile(refId, refType, groupCode);
        if (firstFile == null) {
            firstFile = new DefaultFileInfo(refId, refType, groupCode);
        }
        return saveFile(file, null, firstFile);
    }


    @Override
    public FileInfo addFileLast(FileResource file, String refId, String refType, String groupCode) throws IOException {
        FileInfo lastFile = lastFile(refId, refType, groupCode);
        if (lastFile == null) {
            lastFile = new DefaultFileInfo(refId, refType, groupCode);
        }
        return saveFile(file, lastFile, null);
    }

    @Override
    public FileInfo addFileBefore(FileResource file, String fileId) throws IOException {
        FileInfo fileInfo = fileInfo(fileId);
        Assert.isTrue(fileInfo != null, "指定的文件Id不存在");
        return saveFile(file, null, fileInfo);
    }

    @Override
    public FileInfo addFileAfter(FileResource file, String fileId) throws IOException {
        FileInfo fileInfo = fileInfo(fileId);
        Assert.isTrue(fileInfo != null, "指定的文件Id不存在");
        return saveFile(file, fileInfo, null);
    }

    /**
     * 保存文件
     *
     * @param resource 文件对象
     * @param pre      前面的文件
     * @param next     后面的文件
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public FileInfo saveFile(FileResource resource, FileInfo pre, FileInfo next) throws IOException {
        return saveFile(resource, r -> {
            FileInfo fileInfo = pre;
            if (fileInfo == null) {
                fileInfo = next;
            }
            r.setRefId(fileInfo.getRefId());
            r.setGroupCode(fileInfo.getGroupCode());
            r.setRefType(fileInfo.getRefType());
            if (pre != null) {
                r.setPreId(pre.getId());
                if (!StringUtils.isEmpty(pre.getId())) {
                    //更新前面文件的next为当前Id
                    commonMapper.updateIgnoreNullByQuery(
                            SqlQuery.from(FileRelationInfo.class)
                                    .set(FileRelationInfo.NEXTID, r.getId())
                                    .equal(FileRelationInfo.ID, pre.getId())
                    );
                }
                if (!StringUtils.isEmpty(pre.getNextId())) {
                    r.setNextId(pre.getNextId());
                }
            }
            if (next != null) {
                r.setNextId(next.getId());
                if (!StringUtils.isEmpty(next.getId())) {
                    //更新后面文件的pre为当前Id
                    commonMapper.updateIgnoreNullByQuery(
                            SqlQuery.from(FileRelationInfo.class)
                                    .set(FileRelationInfo.PREID, r.getId())
                                    .equal(FileRelationInfo.ID, next.getId())
                    );
                }
                if (!StringUtils.isEmpty(pre.getPreId())) {
                    r.setPreId(pre.getPreId());
                }
            }
        });
    }


    @Transactional(rollbackFor = Exception.class)
    public FileInfo saveFile(FileResource file, RelationSaveCallBack callBack) throws IOException {
        Assert.isTrue(file != null, "要添加的文件不能为空!");
        String hash = file.getFileHash();
        Assert.isTrue(!StringUtils.isEmpty(hash), "文件hash值不能为空！");
        //先查询相同hash的文件是否存在
        FileBaseInfo fileBaseInfo = commonMapper.selectOneByQuery(
                SqlQuery.from(FileBaseInfo.class)
                        .equal(FileBaseInfoInfo.HASH, hash)
        );

        boolean needSave = false;
        if (fileBaseInfo == null) {
            needSave = true;
            fileBaseInfo = new FileBaseInfo();
            CommonService.bindCreateInfo(fileBaseInfo);
            fileBaseInfo.setHash(hash);
            fileBaseInfo.setSize(file.getFileSize());
            fileBaseInfo.setOriginCreateDate(file.getCreateDate());
            fileBaseInfo.setLastModifyDate(file.getLastModifyDate());
            fileBaseInfo.setSuffix(file.getSuffix());
            fileBaseInfo.setOriginName(file.getName());
            fileBaseInfo.setMineType(mineHandler.fileMine(file));
        }
        //创建关联表信息
        FileRelation fileRelation = new FileRelation();
        CommonService.bindCreateInfo(fileRelation);
        fileRelation.setDescription(file.getDescription());
        fileRelation.setFileId(fileBaseInfo.getId());

        FileInfo fileInfo = new DefaultFileInfo(fileBaseInfo, fileRelation);
        //如果是新文件，保存文件流，插入基本信息数据
        if (needSave) {
            fileHandler.writeFile(file, fileInfo);
            commonMapper.insert(fileBaseInfo);
        }
        //回调绑定业务信息
        callBack.onBeforeSave(fileRelation);
        //添加关联表数据
        commonMapper.insert(fileRelation);
        return fileInfo;
    }

    @Override
    public void setMetaToFile(String fileId, String key, String value) {
        doSetMeta(fileId, key, value, false);
    }

    @Override
    public void setMetaToBaseFile(String fileId, String key, String value) {
        doSetMeta(fileId, key, value, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public void doSetMeta(String fileId, String key, String value, boolean base) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件Id不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(key), "元数据编码不能为空！");
        Assert.isTrue(commonMapper.exists(base ? FileBaseInfo.class : FileRelation.class, fileId), "指定的文件不存在！");
        FileMetaData fileMetaData = commonMapper.selectOneByQuery(
                SqlQuery.from(FileMetaData.class)
                        .equal(FileMetaDataInfo.REFID, fileId)
                        .equal(FileMetaDataInfo.REFTYPE, base ? 0 : 1)
                        .equal(FileMetaDataInfo.KEY, key)
        );
        boolean valueExist = StringUtils.isEmpty(value);

        if (fileMetaData == null) {
            //元数据不存在
            if (valueExist) {
                //key value 不为空则插入元数据
                fileMetaData = new FileMetaData();
                CommonService.bindCreateInfo(fileMetaData);
                fileMetaData.setRefId(fileId);
                fileMetaData.setRefType(base ? 0 : 1);
                fileMetaData.setKey(key);
                fileMetaData.setValue(value);
                commonMapper.insert(fileMetaData);
            }
        } else {
            //元数据存在
            if (valueExist) {
                //value 不同则更新元数据
                if (!value.equals(fileMetaData.getValue())) {
                    fileMetaData.setValue(value);
                    CommonService.bindCreateInfo(fileMetaData);
                    commonMapper.updateIgnoreNullById(fileMetaData);
                }
            } else {
                //value为空则删除元数据
                commonMapper.deleteById(FileMetaData.class, fileMetaData.getId());
            }
        }
    }

    protected FileRelation getFileRelationOnlyId(String fileId) {
        FileRelation fileRelation = commonMapper.selectOneByQuery(
                SqlQuery.from(FileRelation.class, false)
                        .column(FileRelationInfo.ID, FileRelationInfo.PREID, FileRelationInfo.NEXTID, FileRelationInfo.FILEID)
                        .equal(FileRelationInfo.ID, fileId)
        );
        Assert.isTrue(fileRelation != null, "未找到指定的文件");
        return fileRelation;
    }

    /**
     * 执行切换
     *
     * @param first
     * @param second
     */
    @Transactional(rollbackFor = Exception.class)
    public void doMove(FileRelation first, FileRelation second) {
        if (first.getId().equals(second.getId())) {
            return;
        }
        String personId = getCurrentPersonId();
        //更新前面的
        commonMapper.updateIgnoreNullByQuery(
                SqlQuery.from(FileRelation.class)
                        .set(FileRelationInfo.PREID, second.getId())
                        .set(FileRelationInfo.NEXTID, second.getNextId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .set(FileRelationInfo.UPDATEPERSON, personId)
                        .equal(FileRelationInfo.ID, first.getId())
        );
        //更新后面的
        commonMapper.updateIgnoreNullByQuery(
                SqlQuery.from(FileRelation.class)
                        .set(FileRelationInfo.PREID, first.getPreId())
                        .set(FileRelationInfo.NEXTID, first.getId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .set(FileRelationInfo.UPDATEPERSON, personId)
                        .equal(FileRelationInfo.ID, second.getId())
        );
    }

    private String getCurrentPersonId() {
        SecurityHolder securityHolder = SecurityHolder.get();
        if (securityHolder != null && securityHolder.currentPerson() != null) {
            return securityHolder.currentPerson().getId();
        }
        return null;
    }

   /* @Override
    public void switchOrder(String firstFileId, String secondFileId) {
        Assert.isTrue(!StringUtils.isEmpty(firstFileId), "前面的文件Id不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(secondFileId), "前面的文件Id不能为空！");
        if (firstFileId.equals(secondFileId)) {
            return;
        }
        FileRelation firstRelation = getFileRelationOnlyId(firstFileId);
        FileRelation secondRelation = getFileRelationOnlyId(secondFileId);
        doSwitch(firstRelation, secondRelation);
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void switchOrder(String firstFileId, String secondFileId) {
        Assert.isTrue(!StringUtils.isEmpty(firstFileId), "指定的文件Id不能为空");
        Assert.isTrue(!StringUtils.isEmpty(secondFileId), "指定的文件Id不能为空");
        if (firstFileId.equals(secondFileId)) {
            return;
        }
        FileRelation first = getFileRelationOnlyId(firstFileId);
        FileRelation second = getFileRelationOnlyId(secondFileId);
        if (!StringUtils.isEmpty(first.getNextId())) {
            if (first.getNextId().equals(second.getId())) {
                doMove(first, second);
            } else {

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveFirst(String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "指定的文件Id不能为空！");
        FileRelation fileRelation = getFileRelationOnlyId(fileId);
        doMoveFirst(fileRelation);
    }

    private void doMoveFirst(FileRelation fileRelation) {
        //当前不是第一个才移动
        if (!StringUtils.isEmpty(fileRelation.getPreId())) {
            String currentPerson = getCurrentPersonId();
            //更新当前
            commonMapper.updateByQuery(
                    SqlQuery.from(FileRelation.class)
                            .equal(FileRelationInfo.ID, fileRelation.getId())
            );
            //更新第一个
            //更新当前前一个
            //更新当前后一个
        }
    }

    @Override
    public void moveLast(String fileId) {

    }

    @Override
    public void moveForward(String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "指定的文件Id不能为空！");
        FileRelation fileRelation = getFileRelationOnlyId(fileId);
        if (!StringUtils.isEmpty(fileRelation.getPreId())) {
            FileRelation pre = getFileRelationOnlyId(fileRelation.getPreId());
            doMove(pre, fileRelation);
        }
    }

    @Override
    public void moveBack(String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "指定的文件Id不能为空！");
        FileRelation fileRelation = getFileRelationOnlyId(fileId);
        if (!StringUtils.isEmpty(fileRelation.getNextId())) {
            FileRelation pre = getFileRelationOnlyId(fileRelation.getNextId());
            doMove(fileRelation, pre);
        }
    }

    protected SqlQuery<DefaultFileInfo> buildParamsQuery(String refId, String refType, String groupCode) {
        Assert.isTrue(!StringUtils.isEmpty(refId), "业务外键不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(refType), "业务类型不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(groupCode), "文件分组不能为空！");
        return buildFileInfoQuery()
                .equal(FileRelationInfo.REFID, refId)
                .equal(FileRelationInfo.REFTYPE, refType)
                .equal(FileRelationInfo.GROUPCODE, groupCode);
    }

    protected FileInfo doSelect(String refId, String refType, String groupCode, Column nullColumn) {
        return commonMapper
                .selectOneByQuery(buildParamsQuery(refId, refType, groupCode).isNull(nullColumn));
    }

    protected FileRelation doSelectRelation(String refId, String refType, String groupCode, Column nullColumn) {
        Assert.isTrue(!StringUtils.isEmpty(refId), "业务外键不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(refType), "业务类型不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(groupCode), "文件分组不能为空！");
        SqlQuery<FileRelation> sqlQuery = SqlQuery.from(FileRelation.class)
                .equal(FileRelationInfo.REFID, refId)
                .equal(FileRelationInfo.REFTYPE, refType)
                .equal(FileRelationInfo.GROUPCODE, groupCode)
                .isNull(nullColumn);

        return commonMapper
                .selectOneByQuery(sqlQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public FileInfo firstFile(String refId, String refType, String groupCode) {
        return doSelect(refId, refType, groupCode, FileRelationInfo.PREID);
    }

    @Override
    public FileInfo lastFile(String refId, String refType, String groupCode) {
        return doSelect(refId, refType, groupCode, FileRelationInfo.NEXTID);
    }

    /**
     * 构造连表查询语句
     *
     * @return
     */
    protected SqlQuery<DefaultFileInfo> buildFileInfoQuery() {
        return SqlQuery.from(FileRelation.class, false)
                .column(
                        FileBaseInfoInfo.ID.alias("baseFileId"),
                        FileBaseInfoInfo.ORIGINNAME.alias("name"),
                        FileBaseInfoInfo.SUFFIX,
                        FileBaseInfoInfo.ORIGINCREATEDATE.alias("createDate"),
                        FileBaseInfoInfo.LASTMODIFYDATE,
                        FileBaseInfoInfo.SIZE.alias("fileSize"),
                        FileBaseInfoInfo.HASH.alias("fileHash"),
                        FileBaseInfoInfo.MINETYPE.alias("mine"),

                        FileRelationInfo.ID,
                        FileRelationInfo.PREID,
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

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo fileInfo(String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件ID不能为空！");
        return commonMapper.selectOneByQuery(
                buildFileInfoQuery()
                        .equal(FileRelationInfo.ID, fileId)
        );
    }


    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo nextFile(String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件ID不能为空！");
        return commonMapper.selectOneByQuery(
                buildFileInfoQuery()
                        .equal(FileRelationInfo.PREID, fileId)
        );
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FileInfo preFile(String fileId) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件ID不能为空！");
        return commonMapper.selectOneByQuery(
                buildFileInfoQuery()
                        .equal(FileRelationInfo.NEXTID, fileId)
        );
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FileMeta> getFileMeta(String fileId, boolean includeBaseFile) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件Id不能为空！");
        FileRelation fileRelation = getFileRelationOnlyId(fileId);
        SqlQuery<FileMetaData> fileMetaDataSqlQuery = SqlQuery.from(FileMetaData.class);
        if (includeBaseFile) {
            fileMetaDataSqlQuery.in(FileMetaDataInfo.REFID, fileId, fileRelation.getFileId());
        } else {
            fileMetaDataSqlQuery.equal(FileMetaDataInfo.REFID, fileId);
        }
        return commonMapper.selectByQuery(fileMetaDataSqlQuery)
                .stream().map(DefaultFileMeta::new)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FileInfo> list(String refId, String refType, String groupCode) {
        return new ArrayList<>(commonMapper.selectByQuery(buildParamsQuery(refId, refType, groupCode)));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long count(String refId, String refType, String groupCode) {
        return commonMapper.countByQuery(buildParamsQuery(refId, refType, groupCode));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long size(String refId, String refType, String groupCode) {
        Assert.isTrue(!StringUtils.isEmpty(refId), "业务外键不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(refType), "业务类型不能为空！");
        Assert.isTrue(!StringUtils.isEmpty(groupCode), "文件分组不能为空！");
        return commonMapper.selectOneByQuery(
                SqlQuery.from(FileBaseInfo.class, false)
                        .column(FileBaseInfoInfo.SIZE.sum())
                        .in(FileBaseInfoInfo.ID,
                                SqlQuery.from(FileRelation.class, false)
                                        .column(FileRelationInfo.FILEID)
                                        .equal(FileRelationInfo.REFID, refId)
                                        .equal(FileRelationInfo.REFTYPE, refType)
                                        .equal(FileRelationInfo.GROUPCODE, groupCode)
                        )
                        .setReturnClass(Long.class)
        );
    }

    @Override
    public long removeFile(String fileId) {
        return 0;
    }

    @Override
    public long removeFileByRef(String refId, String refType, String groupCode) {
        return 0;
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
