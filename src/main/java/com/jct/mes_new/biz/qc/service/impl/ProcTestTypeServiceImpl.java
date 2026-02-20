package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.qc.mapper.ProcTestTypeMapper;
import com.jct.mes_new.biz.qc.service.ProcTestTypeService;
import com.jct.mes_new.biz.qc.vo.ProcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.ProcTestTypeVo;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProcTestTypeServiceImpl implements ProcTestTypeService {

    private final ProcTestTypeMapper procTestTypeMapper;

    public List<ProcTestTypeVo> getProcTestTypeList(){
        return procTestTypeMapper.getProcTestTypeList();
    }

    public List<ProcTestTypeMethodVo> getProcTestTypeMethod(String testType){
        return procTestTypeMapper.getProcTestTypeMethod(testType);
    }

    public ProcTestTypeMethodVo getProcTestTypeMethodInfo(String procTestTypeMethodId){
        return procTestTypeMapper.getProcTestTypeMethodInfo(procTestTypeMethodId);
    }

    public String saveProcTestTypeMethod(ProcTestTypeMethodVo vo){
        String msg = "저장되었습니다.";

        if ( procTestTypeMapper.saveProcTestTypeMethod(vo) <=0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return msg;
    }
}
