package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class SaleRequesstVo {
    SaleVo saleInfo;
    List<SaleItemListVo> itemList;
}


