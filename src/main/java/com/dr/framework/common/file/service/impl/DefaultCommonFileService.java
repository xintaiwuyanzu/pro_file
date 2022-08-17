package com.dr.framework.common.file.service.impl;

import com.dr.framework.common.config.model.CommonMeta;
import com.dr.framework.common.config.service.CommonMetaService;
import com.dr.framework.common.file.BaseFile;
import com.dr.framework.common.file.FileResource;
import com.dr.framework.common.file.model.FileInfo;
import com.dr.framework.common.file.model.FileMeta;
import com.dr.framework.common.service.CommonService;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认附件实现
 * TODO 并发导入的顺序问题
 *
 * @author dr
 */
public class DefaultCommonFileService extends AbstractCommonFileService {
    @Autowired
    protected CommonMetaService commonMetaService;

    final Map<String, FileBaseInfo> fileHashMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(FileResource file, String refId, String refType, String groupCode) throws IOException {
        //如果第一个文件没有的话，说明是第一个文件
        FileRelation firstFile = first(refId, refType, groupCode);
        return saveFile(file, refId, refType, groupCode, null, firstFile == null ? null : firstFile.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileFirst(String hash, String refId, String refType, String groupCode) {
        FileBaseInfo baseInfo = baseInfoByHash(hash);
        Assert.isTrue(baseInfo != null, "指定hash的文件不存在！");
        FileRelation firstFile = first(refId, refType, groupCode);
        return saveFile(baseInfo, null, refId, refType, groupCode, null, firstFile == null ? null : firstFile.getId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileLast(FileResource file, String refId, String refType, String groupCode) throws IOException {
        FileRelation lastFile = last(refId, refType, groupCode);
        return saveFile(file, refId, refType, groupCode, lastFile == null ? null : lastFile.getId(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileLast(String hash, String refId, String refType, String groupCode) {
        FileBaseInfo baseInfo = baseInfoByHash(hash);
        Assert.isTrue(baseInfo != null, "指定hash的文件不存在！");
        FileRelation lastFile = last(refId, refType, groupCode);
        return saveFile(baseInfo, null, refId, refType, groupCode, lastFile == null ? null : lastFile.getId(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileBefore(FileResource file, String fileId) throws IOException {
        FileRelation relation = file(fileId);
        return saveFile(
                file,
                relation.getRefId(), relation.getRefType(), relation.getGroupCode(),
                relation.getPreId(), relation.getId()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileBefore(String hash, String fileId) {
        FileBaseInfo baseInfo = baseInfoByHash(hash);
        Assert.isTrue(baseInfo != null, "指定hash的文件不存在！");
        FileRelation relation = file(fileId);
        return saveFile(
                baseInfo,
                null,
                relation.getRefId(), relation.getRefType(), relation.getGroupCode(),
                relation.getPreId(), relation.getId()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileAfter(FileResource file, String fileId) throws IOException {
        FileRelation relation = file(fileId);
        return saveFile(
                file,
                relation.getRefId(), relation.getRefType(), relation.getGroupCode(),
                relation.getId(), relation.getNextId()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo addFileAfter(String hash, String fileId) {
        FileBaseInfo baseInfo = baseInfoByHash(hash);
        Assert.isTrue(baseInfo != null, "指定hash的文件不存在！");
        FileRelation relation = file(fileId);
        return saveFile(
                baseInfo,
                null,
                relation.getRefId(), relation.getRefType(), relation.getGroupCode(),
                relation.getId(), relation.getNextId()
        );
    }


    //TODO 可能要自己写事务
    @Transactional(rollbackFor = Exception.class)
    public FileBaseInfo saveBaseFile(FileResource file) throws IOException {
        Assert.isTrue(file != null, "要添加的文件不能为空!");

        String hash = fileInfoHandler.fileHash(file);
        Assert.isTrue(!StringUtils.isEmpty(hash), "文件hash值不能为空！");
        //先查询相同hash的文件是否存在
        FileBaseInfo fileBaseInfo = baseInfoByHash(hash);
        if (fileBaseInfo == null) {
            synchronized (fileHashMap) {
                fileBaseInfo = fileHashMap.get(hash);
                if (fileBaseInfo == null) {
                    fileBaseInfo = new FileBaseInfo();
                    CommonService.bindCreateInfo(fileBaseInfo);
                    fileBaseInfo.setFileHash(hash);
                    //TODO
                    fileBaseInfo.setHashMethod("sha512");
                    fileBaseInfo.setFileSize(file.getFileSize());
                    fileBaseInfo.setOriginCreateDate(file.getCreateDate());
                    fileBaseInfo.setLastModifyDate(file.getLastModifyDate());
                    fileBaseInfo.setSuffix(file.getSuffix());
                    fileBaseInfo.setOriginName(file.getName());
                    fileBaseInfo.setFileType(file.getFileType());
                    fileBaseInfo.setFileAttr(file.getFileAttr());
                    fileBaseInfo.setMimeType(fileInfoHandler.fileMine(file));
                    fileHashMap.put(hash, fileBaseInfo);
                } else {
                    return fileBaseInfo;
                }
            }
            //如果是新文件，保存文件流，插入基本信息数据
            fileSaveHandler.writeFile(file, new DefaultBaseFile(fileBaseInfo));
            commonMapper.insert(fileBaseInfo);
            //文件创建成功，从数据库就能查到相同的hash了，删除缓存
            fileHashMap.remove(hash);
        }
        return fileBaseInfo;
    }

    /**
     * 保存文件
     *
     * @param file
     * @param refId
     * @param refType
     * @param groupCode
     * @param preId
     * @param nextId
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public FileInfo saveFile(FileResource file,
                             String refId, String refType, String groupCode,
                             String preId, String nextId) throws IOException {
        FileBaseInfo fileBaseInfo = saveBaseFile(file);
        return saveFile(fileBaseInfo, file.getDescription(), refId, refType, groupCode, preId, nextId);
    }

    @Transactional(rollbackFor = Exception.class)
    public FileInfo saveFile(FileBaseInfo fileBaseInfo,
                             String desc,
                             String refId, String refType, String groupCode,
                             String preId, String nextId) {
        //创建关联表信息
        FileRelation fileRelation = new FileRelation();
        //绑定创建人信息
        CommonService.bindCreateInfo(fileRelation);
        //绑定基本信息
        fileRelation.setDescription(desc);
        fileRelation.setFileId(fileBaseInfo.getId());
        //绑定关联信息
        fileRelation.setRefId(refId);
        fileRelation.setRefType(refType);
        fileRelation.setGroupCode(groupCode);
        //绑定前后关联Id
        fileRelation.setPreId(preId);
        fileRelation.setNextId(nextId);

        //更新前后id的链表
        updateRelation(preId, fileRelation.getId(), false);
        updateRelation(nextId, fileRelation.getId(), false);
        //添加关联表数据
        commonMapper.insert(fileRelation);
        return new DefaultFileInfo(fileBaseInfo, fileRelation);
    }

    @Override
    public void setMetaToFile(String fileId, String key, String value) {
        doSetMeta(fileId, key, value, false);
    }

    @Override
    public void setMetaToBaseFile(String fileId, String key, String value) {
        doSetMeta(fileId, key, value, true);
    }

    public static final String META_REF_TYPE_BASE = "file_base";
    public static final String META_REF_TYPE_RELATION = "file_relation";

    @Transactional(rollbackFor = Exception.class)
    public void doSetMeta(String fileId, String key, String value, boolean base) {
        commonMetaService.setMetaData(fileId, base ? META_REF_TYPE_BASE : META_REF_TYPE_RELATION, key, value);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long switchOrder(String firstFileId, String secondFileId) {
        if (firstFileId.equals(secondFileId)) {
            return 0;
        }
        FileRelation first = file(firstFileId);
        FileRelation second = file(secondFileId);
        long count = 0;
        if (first.getId().equals(second.getPreId())) {
            count = changeNeighbour(first, second);
        } else if (first.getId().equals(second.getNextId())) {
            count = changeNeighbour(second, first);
        } else {
            //不是邻居
            //TODO 这里还有一个隔一个邻居的情况，暂时不处理
            count += commonMapper.updateByQuery(
                    SqlQuery.from(FileRelation.class, false)
                            .set(FileRelationInfo.PREID, second.getPreId())
                            .set(FileRelationInfo.NEXTID, second.getNextId())
                            .set(FileRelationInfo.UPDATEPERSON, getCurrentPersonId())
                            .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                            .equal(FileRelationInfo.ID, first.getId())
            );
            count += commonMapper.updateByQuery(
                    SqlQuery.from(FileRelation.class, false)
                            .set(FileRelationInfo.PREID, first.getPreId())
                            .set(FileRelationInfo.NEXTID, first.getNextId())
                            .set(FileRelationInfo.UPDATEPERSON, getCurrentPersonId())
                            .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                            .equal(FileRelationInfo.ID, second.getId())
            );
            count += updateRelation(first.getNextId(), second.getId(), true);
            count += updateRelation(first.getPreId(), second.getId(), false);
            count += updateRelation(second.getNextId(), first.getId(), true);
            count += updateRelation(second.getPreId(), first.getId(), false);
        }
        return count;
    }

    /**
     * 邻居互换
     *
     * @param pre
     * @param next
     * @return
     */
    private long changeNeighbour(FileRelation pre, FileRelation next) {
        long count = updateRelation(pre.getPreId(), next.getId(), false);
        count += updateRelation(next.getNextId(), pre.getId(), true);
        count += commonMapper.updateByQuery(
                SqlQuery.from(FileRelation.class, false)
                        .set(FileRelationInfo.PREID, next.getId())
                        .set(FileRelationInfo.NEXTID, next.getNextId())
                        .set(FileRelationInfo.UPDATEPERSON, getCurrentPersonId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .equal(FileRelationInfo.ID, pre.getId())
        );
        count += commonMapper.updateByQuery(
                SqlQuery.from(FileRelation.class, false)
                        .set(FileRelationInfo.PREID, pre.getPreId())
                        .set(FileRelationInfo.NEXTID, pre.getId())
                        .set(FileRelationInfo.UPDATEPERSON, getCurrentPersonId())
                        .set(FileRelationInfo.UPDATEDATE, System.currentTimeMillis())
                        .equal(FileRelationInfo.ID, next.getId())
        );
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long moveFirst(String fileId) {
        FileRelation relation = file(fileId);
        long count = 0;
        if (!StringUtils.isEmpty(relation.getPreId())) {
            //当前文件不是第一个
            FileRelation first = first(relation.getRefId(), relation.getRefType(), relation.getGroupCode());
            //设置当前文件pre为null
            count += updateRelation(relation.getId(), null, true);
            //设置first的pre为当前id
            count += updateRelation(first.getId(), relation.getId(), true);
            //设置next的pre为pre
            count += updateRelation(relation.getNextId(), relation.getPreId(), true);
            //设置pre的next为next
            count += updateRelation(relation.getPreId(), relation.getNextId(), false);
        }
        return count;
    }

    @Override
    public long moveLast(String fileId) {
        FileRelation relation = file(fileId);
        long count = 0;
        if (!StringUtils.isEmpty(relation.getNextId())) {
            //当前文件不是最后一个
            FileRelation last = last(relation.getRefId(), relation.getRefType(), relation.getGroupCode());
            //设置当前文件next为null
            count += updateRelation(relation.getId(), null, false);
            //设置last的pre为当前id
            count += updateRelation(last.getId(), relation.getId(), false);
            //设置next的pre为pre
            count += updateRelation(relation.getNextId(), relation.getPreId(), true);
            //设置pre的next为next
            count += updateRelation(relation.getPreId(), relation.getNextId(), false);
        }
        return count;
    }

    @Override
    public long moveForward(String fileId) {
        FileRelation fileRelation = file(fileId);
        long count = 0;
        if (!StringUtils.isEmpty(fileRelation.getPreId())) {
            FileRelation pre = file(fileRelation.getPreId());
            count = changeNeighbour(pre, fileRelation);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long moveBack(String fileId) {
        FileRelation fileRelation = file(fileId);
        long count = 0;
        if (!StringUtils.isEmpty(fileRelation.getNextId())) {
            FileRelation next = file(fileRelation.getNextId());
            count = changeNeighbour(fileRelation, next);
        }
        return count;
    }

    protected FileBaseInfo baseInfoByHash(String hash) {
        Assert.isTrue(!StringUtils.isEmpty(hash), "文件hash不能为空！");
        return commonMapper.selectOneByQuery(SqlQuery.from(FileBaseInfo.class).equal(FileBaseInfoInfo.FILEHASH, hash));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public BaseFile fileInfoByHash(String hash) {
        FileBaseInfo baseInfo = baseInfoByHash(hash);
        if (baseInfo == null) {
            return null;
        } else {
            return new DefaultBaseFile(baseInfo);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public boolean existByHash(String hash) {
        Assert.isTrue(!StringUtils.isEmpty(hash), "文件hash不能为空！");
        return commonMapper.existsByQuery(SqlQuery.from(FileBaseInfo.class).equal(FileBaseInfoInfo.FILEHASH, hash));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FileMeta> getFileMeta(String fileId, boolean includeBaseFile) {
        Assert.isTrue(!StringUtils.isEmpty(fileId), "文件Id不能为空！");
        FileRelation fileRelation = file(fileId);
        List<CommonMeta> metas = commonMetaService.metaList(fileId, META_REF_TYPE_RELATION);
        if (includeBaseFile) {
            metas.addAll(commonMetaService.metaList(fileId, META_REF_TYPE_BASE));
        }
        return metas.stream().map(DefaultFileMeta::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public long changeGroup(String refId, String refType, String groupCode, String targetType, String targetGroupCode) {
        Assert.isTrue(StringUtils.hasText(refId), "业务外键不能为空！");
        if (!StringUtils.hasText(refType)) {
            refType = DEFAULT_REF_TYPE;
        }
        if (!StringUtils.hasText(groupCode)) {
            groupCode = DEFAULT_GROUP_CODE;
        }
        if (!StringUtils.hasText(targetType)) {
            targetType = DEFAULT_REF_TYPE;
        }
        if (!StringUtils.hasText(targetGroupCode)) {
            targetGroupCode = DEFAULT_GROUP_CODE;
        }
        if (refType.equals(targetType) && groupCode.equals(targetGroupCode)) {
            return 0;
        }
        return commonMapper.updateIgnoreNullByQuery(SqlQuery.from(FileRelation.class)
                .set(FileRelationInfo.GROUPCODE, targetGroupCode)
                .set(FileRelationInfo.REFTYPE, targetType)
                .equal(FileRelationInfo.REFID, refId)
                .equal(FileRelationInfo.REFTYPE, refType)
                .equal(FileRelationInfo.GROUPCODE, groupCode));
    }

    @Override
    public InputStream fileStream(String fileId) throws IOException {
        FileInfo fileInfo = fileInfo(fileId);
        Assert.isTrue(fileInfo != null, "指定的文件不存在！");
        return fileSaveHandler.readFile(fileInfo);
    }

    @Override
    public InputStream fileStreamByHash(String hash) throws IOException {
        BaseFile fileInfo = fileInfoByHash(hash);
        Assert.isTrue(fileInfo != null, "指定的文件不存在！");
        return fileSaveHandler.readFile(fileInfo);
    }

    @Override
    public boolean copyTo(String fileId, String newFile) throws IOException {
        Assert.isTrue(!StringUtils.isEmpty(newFile), "新文件路径不能为空！");
        FileInfo fileInfo = fileInfo(fileId);
        Assert.isTrue(fileInfo != null, "指定的文件不存在！");
        return fileSaveHandler.copyTo(fileInfo, newFile);
    }

    @Override
    public boolean copyToByHash(String fileHash, String newFile) throws IOException {
        Assert.isTrue(!StringUtils.isEmpty(newFile), "新文件路径不能为空！");
        BaseFile fileInfo = fileInfoByHash(fileHash);
        Assert.isTrue(fileInfo != null, "指定的文件不存在！");
        return fileSaveHandler.copyTo(fileInfo, newFile);
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
                        .column(FileBaseInfoInfo.FILESIZE.sum())
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
            if (hasPre) {
                //前面的文件
                count += updateRelation(relation.getPreId(), relation.getNextId(), false);
                count += updateRelation(relation.getNextId(), relation.getPreId(), true);
            } else {
                //后面有文件，前面没有文件，说明删除的第一个
                count += updateRelation(relation.getNextId(), null, true);
            }
        } else {
            //后面没有，前面有
            if (hasPre) {
                //前面的文件
                count += updateRelation(relation.getPreId(), null, false);
            } else {
                //前后都没有，说明只有一个
                //如果相同文件Id的数据只有一个，删除基本文件
                long sameFileSize = commonMapper.countByQuery(SqlQuery.from(FileRelation.class).equal(FileRelationInfo.FILEID, relation.getFileId()));
                if (sameFileSize == 1) {
                    FileBaseInfo baseInfo = commonMapper.selectById(FileBaseInfo.class, relation.getFileId());
                    Assert.isTrue(baseInfo != null, "未找到实体文件信息");
                    fileSaveHandler.deleteFile(new DefaultBaseFile(baseInfo));
                    count += commonMapper.deleteById(FileBaseInfo.class, relation.getFileId());
                }
            }
        }
        count += commonMapper.deleteById(FileRelation.class, fileId);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long removeFileByRef(String refId, String refType, String groupCode) {
        //删除关联表数据
        long count = commonMapper.deleteByQuery(
                buildParamsQuery(SqlQuery.from(FileRelation.class)
                        , refId,
                        refType,
                        groupCode
                )
        );
        //不管啥情况，只要fileBaseInfo 中有的但是fileRelation 中没有的文件都要删除
        //TODO 数据量到百万的时候有性能问题
        List<FileBaseInfo> baseInfos = commonMapper.selectByQuery(
                SqlQuery.from(FileBaseInfo.class)
                        .notIn(
                                FileBaseInfoInfo.ID, SqlQuery.from(FileRelation.class, false)
                                        .column(FileRelationInfo.FILEID)
                                        .groupBy(FileRelationInfo.FILEID)
                        )
        );
        if (!baseInfos.isEmpty()) {
            String[] idArr = new String[baseInfos.size()];
            //删除文件
            for (int i = 0; i < baseInfos.size(); i++) {
                FileBaseInfo baseInfo = baseInfos.get(i);
                fileSaveHandler.deleteFile(new DefaultBaseFile(baseInfo));
                idArr[i] = baseInfo.getId();
            }
            count += commonMapper.deleteBatchIds(FileBaseInfo.class, idArr);
        }
        return count;
    }


}
