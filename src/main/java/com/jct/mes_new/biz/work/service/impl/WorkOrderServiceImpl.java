package com.jct.mes_new.biz.work.service.impl;

import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.service.WorkOrderService;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderMapper workOrderMapper;

    public List<WorkOrderVo> getWorkOrderList(WorkOrderVo vo){
        return workOrderMapper.getWorkOrderList(vo);
    }
}
