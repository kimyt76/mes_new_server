package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemVo;
import com.jct.mes_new.biz.order.vo.OrderPlanTypeVo;
import com.jct.mes_new.biz.order.vo.OrderPlanVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ContractMapper {

    List<ContractVo> getContractList(ContractVo contractVo);
    ContractVo getContractInfo(String contractId);
    List<ContractItemVo> getContractItemList(String contractId);

    int insertContractInfo(ContractVo contractInfo);
    int insertContractItem(ContractItemVo item);

    int updateContractInfo(ContractVo contractInfo);
    int updateContractItem(ContractItemVo item);


    void updateAttachFileId(Long contractId, String attachFileId);

    List<OrderPlanVo> getOrderPlanList(OrderPlanVo vo);

    List<OrderPlanTypeVo> getOrderPlanType(OrderPlanTypeVo vo);

    void deleteOrderPlanTypeList(List<Long> ids);

    int insertOrderPlanType(OrderPlanTypeVo orderPlanTypeVo);

    int updateOrderPlanType(OrderPlanTypeVo orderPlanTypeVo);

    int updateOrderPlanYn(OrderPlanVo vo);

    List<WorkOrderInfoVo> getMatWorkOrder(WorkOrderInfoVo vo);

    List<Map<String, Object>> getRequiredQuantityList(OrderPlanVo vo);
}
