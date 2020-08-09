package com.dr.framework.common.file.service.impl;

import com.dr.framework.core.orm.annotations.Mapper;
import com.dr.framework.util.Constants;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * sqlQuery满足不了复杂的查询条件
 *
 * @author dr
 */
@Mapper(module = Constants.COMMON_MODULE_NAME)
interface CommonFileMapper {

    /**
     * 复杂的查询
     *
     * @param refId
     * @param refType
     * @param groupCode
     * @return
     */

    @Select({"select * from ", FileBaseInfoInfo.TABLE,
            " where id in (select fileId from ", FileRelationInfo.TABLE,
            " where refId=#{refId} and refType=#{refType} and groupCode=#{groupCode}",
            " group by fileId having count(fileId)=1)"
    })
    List<FileBaseInfo> needDeleteBaseFiles(String refId, String refType, String groupCode);
}
