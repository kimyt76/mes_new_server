package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.StorageMapper;
import com.jct.mes_new.biz.base.service.StorageService;
import com.jct.mes_new.biz.base.vo.StorageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StorageServiceImpl implements StorageService {

    private final StorageMapper storageMapper;

    public List<StorageVo> getStorageList(StorageVo storageVo){
        return storageMapper.getStorageList(storageVo);
    }

    public String saveStorageInfo(StorageVo storageVo){
        String msg = "저장되었습니다.";

        try {
            if ( storageMapper.saveStorageInfo(storageVo) <= 0 ){
                throw new Exception("저장에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("저장 실패 : " + e.getMessage(), e );
        }

        return msg;
    }


}
