package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShipmentMapper {
    List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo);

    void deleteShipmentItemList(Long shipmentId);

    ShipmentVo getShipmentInfo(Long shipmentId);

    List<ShipmentItemListVo> getItemList(Long shipmentId);

    List<ShipmentItemListVo> getShipmentItemList(Long shipmentId);
    List<ShipmentWorkOrderVo> getWorkOrderItemList(ShipmentWorkOrderVo vo);


    int insertShipmentMst(ShipmentVo mst);
    int insertShipmentItemList(ShipmentItemListVo mst);

    int updateShipmentMst(ShipmentVo mst);
    int updateShipmentItemList(ShipmentItemListVo mst);

    int insertShipmentInvTran(ShipmentInvTranVo relation);

    void updatePrintYn(Long shipmentId);

    List<ShipmentItemListVo> getTransactionShipmentItemList(Long shipmentId);
}
