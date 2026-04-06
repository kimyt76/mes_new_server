package com.jct.mes_new.biz.system.mapper;

import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.system.vo.StorageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StorageMapper {
    List<StorageVo> getStorageList(StorageVo vo);
    StorageVo getStorageInfo(String storageCd);

    List<Map<String, Object>> getStorageCodeList();

    int useCheck(String storageCd);

    int insertStorageInfo(StorageVo vo);

    int updateStorageInfo(StorageVo vo);

    List<Map<String, Object>> getAreaStorageList(@Param("areaCd") String areaCd);
}
