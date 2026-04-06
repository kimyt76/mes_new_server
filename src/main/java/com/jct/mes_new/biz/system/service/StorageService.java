package com.jct.mes_new.biz.system.service;

import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.system.vo.StorageVo;

import java.util.List;
import java.util.Map;

public interface StorageService {
    List<StorageVo> getStorageList(StorageVo vo);

    List<Map<String, Object>> getAreaStorageList(String areaCd);

    StorageVo getStorageInfo(String storageCd);

    String saveStorageInfo(StorageVo vo);

    List<Map<String, Object>> getStorageCodeList();

    String useCheck(String storageCd);
}
