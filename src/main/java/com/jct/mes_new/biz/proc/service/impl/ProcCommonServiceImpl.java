package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProcCommonServiceImpl implements ProcCommonService {

    private final ProcCommonMapper procCommonMapper;
    private final ProcWeighMapper procWeighMapper;


    public List<ProcCommonVo> getWorkerList(ProcCommonVo vo){
        return procCommonMapper.getWorkerList(vo.getAreaCd(), vo.getProcCd());
    }
    public List<ProcCommonVo> getBagWeightList(){
        return procCommonMapper.getBagWeightList();
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
