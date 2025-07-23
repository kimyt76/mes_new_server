package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.StorageVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StorageMapper {

    List<StorageVo> getStorageList(StorageVo storageVo);

    int saveStorageInfo(StorageVo storageVo);
}
