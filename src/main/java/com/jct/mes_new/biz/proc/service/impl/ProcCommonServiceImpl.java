package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProcCommonServiceImpl implements ProcCommonService {

    private final ProcCommonMapper procCommonMapper;


    public List<ProcCommonVo> getWorkerList(String procCd){
        return procCommonMapper.getWorkerList(procCd);
    }

    public List<ProcCommonVo> getEquipmentList(String storageCd){
        return procCommonMapper.getEquipmentList(storageCd);
    }

    public String updateProcStatus(ProcCommonVo vo){
        if (procCommonMapper.updateProcStatus(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        if (procCommonMapper.updateBatchStatus(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return "수정되었습니다.";
    }

}
