package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.SaleItemListVo;
import com.jct.mes_new.biz.order.vo.ShipmentItemListVo;
import com.jct.mes_new.biz.order.vo.ShipmentVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShipmentMapper {
    List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo);

    List<SaleItemListVo> getSalesItemList(List<String> saleIdList);

    int saveShipmentInfo(ShipmentVo shipmentInfo);

    void deleteShipmentItemList(String shipmentId);

    int saveItemList(ShipmentItemListVo item);

    ShipmentVo getShipmentInfo(String shipmentId);

    List<ShipmentItemListVo> getItemList(String shipmentId);

    List<ShipmentItemListVo> getShipmentItemList(String shipmentId);
}
