package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShipmentMapper {
    List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo);

    void deleteShipmentItem(Long shipmentItemId);

    ShipmentVo getShipmentInfo(Long shipmentId);

    List<ShipmentItemListVo> getItemList(Long shipmentId);

    List<ShipmentItemListVo> getShipmentItemList(Long shipmentId);
    List<ShipmentWorkOrderVo> getWorkOrderItemList( @Param("itemName") String itemName,
                                                    @Param("customerCd") String customerCd,
                                                    @Param("itemCd") String itemCd,
                                                    @Param("poNo") String poNo);


    int insertShipmentMst(ShipmentVo mst);
    int insertShipmentItemList(ShipmentItemListVo mst);

    int updateShipmentMst(ShipmentVo mst);
    int updateShipmentItemList(ShipmentItemListVo mst);

    void updatePrintYn(Long shipmentId);
    void updateShipmentItemByTranId(ShipmentItemListVo item);

    void deleteShipmentItemList(List<Long> deleteShipmentIds);

    void deleteShipmentMst(List<Long> deleteShipmentIds);
}
