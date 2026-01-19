package com.jct.mes_new.biz.system.service.impl;

import com.jct.mes_new.biz.system.mapper.StorageMapper;
import com.jct.mes_new.biz.system.service.StorageService;
import com.jct.mes_new.biz.system.vo.StorageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.digester.annotations.rules.SetRoot;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StorageServiceImpl implements StorageService {

    private final StorageMapper storageMapper;

    public List<StorageVo> getStorageList(StorageVo vo){
        return storageMapper.getStorageList(vo);
    }

    public StorageVo getStorageInfo(String storageCd) {
        return storageMapper.getStorageInfo(storageCd);
    }

    public String saveStorageInfo(StorageVo vo){
        return  "저장되었습니다.";
    }
}
