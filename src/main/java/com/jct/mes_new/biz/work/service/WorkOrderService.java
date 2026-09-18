package com.jct.mes_new.biz.work.service;

import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;

import java.util.List;

public interface WorkOrderService {
    List<WorkOrderVo> getWorkOrderList(WorkOrderVo vo);

    WorkOrderVo getWorkOrderInfo(String workOrderId);

    WorkOrderVo saveWorkOrderInfo(WorkOrderVo vo);

    int deleteWorkOrders(List<Long> workOrderIds);

    List<WorkOrderInfoVo> getWorkOrderProgressList(WorkOrderVo vo);
}
