package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderPlanTypeRequestVo {

    private OrderPlanTypeVo orderPlanTypeInfo;
    private List<OrderPlanTypeVo> orderPlanTypeList;
    private List<Long> deleteOrderPlanIds;
}
