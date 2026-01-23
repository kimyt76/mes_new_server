package com.jct.mes_new.biz.work.controller;

import com.jct.mes_new.biz.work.service.WorkOrderService;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/workOrder")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
    private final MessageUtil messageUtil;

    @PostMapping("/getWorkOrderList")
    public List<WorkOrderVo> getWorkOrderList (@RequestBody WorkOrderVo vo){
        return workOrderService.getWorkOrderList(vo);
    }

    @GetMapping("/getWorkOrderInfo/{id}")
    public WorkOrderVo getWorkOrderInfo (@PathVariable("id") String workOrderId){
        WorkOrderVo vo = workOrderService.getWorkOrderInfo(workOrderId);
        return  vo;
    }

    @PostMapping("/saveWorkOrderInfo")
    public ResponseEntity<ApiResponse<Void>> saveWorkOrderInfo(@RequestBody WorkOrderVo vo){
        WorkOrderVo saved = workOrderService.saveWorkOrderInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @DeleteMapping("/deleteWorkOrders")
    public ResponseEntity<ApiResponse<Void>> deleteWorkOrders (@RequestBody List<Long> workOrderIds){
        int result = workOrderService.deleteWorkOrders(workOrderIds);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.deleted")));
    }
}
