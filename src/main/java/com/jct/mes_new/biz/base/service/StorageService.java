package com.jct.mes_new.biz.base.service;

import com.jct.mes_new.biz.base.vo.StorageVo;

import java.util.List;

public interface StorageService {

    List<StorageVo> getStorageList(StorageVo storageVo);

    String saveStorageInfo(StorageVo storageVo);
}
