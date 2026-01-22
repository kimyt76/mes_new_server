package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
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


}
