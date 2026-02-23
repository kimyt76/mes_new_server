package com.jct.mes_new.biz.system.mapper;

import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.system.vo.StorageVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StorageMapper {
    List<StorageVo> getStorageList(StorageVo vo);

    StorageVo getStorageInfo(String storageCd);

    List<CommonVo> getStorageCodeList();

    String saveStorageInfo(StorageVo vo);
}
