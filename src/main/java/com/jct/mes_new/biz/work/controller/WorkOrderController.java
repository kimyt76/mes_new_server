package com.jct.mes_new.biz.work.controller;

import com.jct.mes_new.biz.work.service.WorkOrderService;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/workOrder")
public class WorkOrderController {

    private final WorkOrderService workOrderService;


    @PostMapping("/getWorkOrderList")
    public List<WorkOrderVo> getWorkOrderList (WorkOrderVo vo){
        return workOrderService.getWorkOrderList(vo);
    }


}
