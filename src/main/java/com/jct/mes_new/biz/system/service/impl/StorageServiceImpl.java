package com.jct.mes_new.biz.system.service.impl;

import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.system.mapper.StorageMapper;
import com.jct.mes_new.biz.system.service.StorageService;
import com.jct.mes_new.biz.system.vo.StorageVo;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.digester.annotations.rules.SetRoot;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        String msg = "저장되었습니다.";

        if (vo.getStorageId() == null) {
            int inserted = storageMapper.insertStorageInfo(vo);
            if (inserted != 1) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        } else {
            int updated = storageMapper.updateStorageInfo(vo);
            if (updated != 1) {
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }
        }
        return msg;
    }

    public List<CommonVo> getStorageCodeList() {
        return storageMapper.getStorageCodeList();
    }

    public String useCheck(String storageCd){
        String msg = "사용가능한 코드입니다.";

        if ( storageMapper.useCheck(storageCd) > 0) {
            msg = "중복된 코드입니다.";
        }
        return msg;
    }
}
