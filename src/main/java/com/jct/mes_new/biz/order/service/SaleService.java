package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.SaleItemListVo;
import com.jct.mes_new.biz.order.vo.ContractItemVo;
import com.jct.mes_new.biz.order.vo.SaleVo;

import java.util.List;
import java.util.Map;

public interface SaleService {

    List<SaleVo> getSaleList(SaleVo saleVo);

    Map<String, Object> getSaleInfo(String saleId);

    String saveSaleInfo(SaleVo saleInfo, List<SaleItemListVo> itemList);

    List<ContractItemVo> getContractItemList(String ids);

    List<SaleItemListVo> getSaleItemList(String saleId);
}
