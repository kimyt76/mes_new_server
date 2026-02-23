package com.jct.mes_new.biz.system.service;

import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.system.vo.StorageVo;

import java.util.List;

public interface StorageService {
    List<StorageVo> getStorageList(StorageVo vo);

    StorageVo getStorageInfo(String storageCd);

    String saveStorageInfo(StorageVo vo);

    List<CommonVo> getStorageCodeList();
}
