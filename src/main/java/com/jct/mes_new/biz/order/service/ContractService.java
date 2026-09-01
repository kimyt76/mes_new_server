package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.*;
import com.jct.mes_new.biz.stock.vo.StockVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;

import java.util.List;
import java.util.Map;

public interface ContractService {

    List<ContractVo> getContractList(ContractVo contractVo);

    Map<String, Object> getContractInfo(String contractId);


    Long saveContractInfo(ContractSaveRequestVo vo);

    String updateContractInfo(ContractSaveRequestVo vo);

    List<OrderPlanVo> getOrderPlanList(OrderPlanVo vo);

    List<OrderPlanTypeVo> getOrderPlanType(OrderPlanTypeVo vo);

    String saveOrderPlan(OrderPlanTypeRequestVo vo);

    String updateOrderPlanYn(OrderPlanVo vo);

    List<WorkOrderInfoVo> getMatWorkOrder(WorkOrderInfoVo vo);

    Map<String, Object> getRequiredQuantityList(OrderPlanVo vo);
}
