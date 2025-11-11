package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.SaleItemListVo;
import com.jct.mes_new.biz.order.vo.SaleVo;
import com.jct.mes_new.biz.order.vo.ShipmentItemListVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SaleMapper {
    List<SaleVo> getSaleList(SaleVo saleVo);

    SaleVo getSaleInfo(String saleId);

    List<SaleItemListVo> getSaleItemList(String saleId);

    int saveSaleInfo(SaleVo saleInfo);

    void deleteSaleItemList(String saleId);

    int saveItemList(SaleItemListVo item);

    List<ShipmentItemListVo> getSalesItemList(List<String> saleIdList);
}
