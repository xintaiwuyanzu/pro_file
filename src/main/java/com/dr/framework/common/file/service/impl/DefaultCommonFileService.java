package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.model.FileMeta;
import com.dr.framework.common.file.model.FileResource;
import com.dr.framework.common.file.service.CommonFileService;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认附件实现
 *
 * @author dr
 */
public class DefaultCommonFileService extends AbstractCommonFileService implements CommonFileService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(FileResource file, String refId, String refType, String groupCode) throws IOException {
        //如果第一个文件没有的话，说明是第一个文件
        FileRelation firstFile = first(refId, refType, groupCode);
        if (firstFile == null) {
            firstFile = new FileRelation(refId, refType, groupCode);
        }
        return saveFile(file, null, firstFile);
    }


    @Override
    public FileInfo addFileLast(FileResource file, String refId, String refType, String groupCode) throws IOException {
        FileRelation lastFile = last(refId, refType, groupCode);
        if (lastFile == null) {
            lastFile = new FileRelation(refId, refType, groupCode);
        }
        return saveFile(file, lastFile, null);
    }

    @Override
    public FileInfo addFileBefore(FileResource file, String fileId) throws IOException {
        return saveFile(file, pre(fileId), file(fileId));
    }

    @Override
    public FileInfo addFileAfter(FileResource file, String fileId) throws IOException {
        return saveFile(file, file(fileId), next(fileId));
    }

    //TODO 可能要自己写事务
    @Transactional(rollbackFor = Exception.class)
    public FileBaseInfo saveBaseFile(FileResource file) throws IOException {
        Assert.isTrue(file != null, "要添加的文件不能为空!");
        String hash = file.getFileHash();
        Assert.isTrue(!StringUtils.isEmpty(hash), "文件hash值不能为空！");
        //先查询相同hash的文件是否存在
        FileBaseInfo fileBaseInfo = commonMapper.selectOneByQuery(
                SqlQuery.from(FileBaseInfo.class)
                        .equal(FileBaseInfoInfo.HASH, hash)
        );
        if (fileBaseInfo == null) {
            fileBaseInfo = new FileBaseInfo();
            CommonService.bindCreateInfo(fileBaseInfo);
            fileBaseInfo.setHash(hash);
            fileBaseInfo.setSize(file.getFileSize());
            fileBaseInfo.setOriginCreateDate(file.getCreateDate());
            fileBaseInfo.setLastModifyDate(file.getLastModifyDate());
            fileBaseInfo.setSuffix(file.getSuffix());
            fileBaseInfo.setOriginName(file.getName());
            fileBaseInfo.setMimeType(mineHandler.fileMine(file));

            //如果是新文件，保存文件流，插入基本信息数据
            fileHandler.writeFile(file, new DefaultBaseFile(fileBaseInfo));
            commonMapper.insert(fileBaseInfo);
        }
        return fileBaseInfo;
    }

    /**
     * 保存文件
     *
     * @param file 文件对象
     * @param pre  前面的文件
     * @param next 后面的文件
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public FileInfo saveFile(FileResource file, FileRelation pre, FileRelation next) throws IOException {
        FileBaseInfo fileBaseInfo = saveBaseFile(file);
        //创建关联表信息
        FileRelation fileRelation = new FileRelation();
        //绑定创建人信息
        CommonService.bindCreateInfo(fileRelation);
        //绑定基本信息
        fileRelation.setDescription(file.getDescription());
        fileRelation.setFileId(fileBaseInfo.getId());

        //绑定关联信息
        FileRelation noNullRelation = pre == null ? next : pre;
        fileRelation.setRefId(noNullRelation.getRefId());
        fileRelation.setRefType(noNullRelation.getRefType());
        fileRelation.setGroupCode(noNullRelation.getGroupCode());

        if (pre != null) {
            fileRelation.setPreId(pre.getId());
        }
        if (next != null) {
            fileRelation.setNextId(next.getId());
        }
        //更新前后的关联信息
        updateRelation(fileRelation.getId(), pre, true);
        updateRelation(fileRelation.getId(), next, false);
        //添加关联表数据
        commonMapper.insert(fileRelation);
        return new DefaultFileInfo(fileBaseInfo, fileRelation);
    }

    @Transactional(rollbackFor = Exception.class)
    public long updateRelation(String newId, FileRelation relation, boolean isPre) {
        if (relation != null && !StringUtils.isEmpty(relation.getId())) {
            return updateRelation(relation.getId(), newId, !isPre);
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public long updateRelation(String updateId, String relationId, boolean isPre) {
        Assert.isTrue(!StringUtils.isEmpty(updateId), "要更新的文件Id不能为空！");
        return commonMapper.updateByQuery(
                SqlQuery.from(FileRelation.class, false)
                        .set(isPre ? FileRelationInfo.NEXTID : FileRelationInfo.PREID, relationId)
                        .set(FileRelationInfo.UPDATEPERSON, getCurrentPersonId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .equal(FileRelationInfo.ID, updateId)
        );
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
                        .set(FileRelationInfo.NEXTID, second.getNextId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .set(FileRelationInfo.UPDATEPERSON, personId)
                        .equal(FileRelationInfo.ID, first.getId())
        );
        //更新后面的
        commonMapper.updateIgnoreNullByQuery(
                SqlQuery.from(FileRelation.class)
                        .set(FileRelationInfo.NEXTID, first.getId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .set(FileRelationInfo.UPDATEPERSON, personId)
                        .equal(FileRelationInfo.ID, second.getId())
        );
    }


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


    /*
     * ===============================================
     * 统计方法
     * ===============================================
     */
    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long count(String refId, String refType, String groupCode) {
        return commonMapper.countByQuery(buildParamsQuery(SqlQuery.from(FileRelation.class), refId, refType, groupCode));
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


    /*
     * ===============================================
     * 删除文件
     * ===============================================
     */

    /**
     * 删除文件
     *
     * @param fileId
     * @return
     */
    @Override
    public long removeFile(String fileId) {
        //获取文件
        FileRelation relation = file(fileId);
        long count = 0;
        boolean hasNext = !StringUtils.isEmpty(relation.getNextId());
        boolean hasPre = !StringUtils.isEmpty(relation.getPreId());

        if (hasNext) {
            //后面的文件
            FileRelation next = next(fileId);
            if (hasPre) {
                //前面的文件
                FileRelation pre = pre(fileId);
                count += updateRelation(next, );
                count += updateRelation(next, );
            } else {
                //后面有文件，前面没有文件，说明删除的第一个


            }
        } else {
            //后面没有，前面有
            if (hasPre) {
                //前面的文件
                count += updateRelation(null, pre(fileId), true);
            } else {
                FileBaseInfo baseInfo = commonMapper.selectById(FileBaseInfo.class, relation.getFileId());
                Assert.isTrue(baseInfo != null, "未找到实体文件信息");
                fileHandler.deleteFile(new DefaultBaseFile(baseInfo));
                //前后都没有，说明只有一个
                count += commonMapper.deleteById(FileBaseInfo.class, relation.getFileId());
            }
        }
        count += commonMapper.deleteById(FileRelation.class, fileId);
        return count;
    }

    @Override
    public long removeFileByRef(String refId, String refType, String groupCode) {
        return 0;
    }


}
