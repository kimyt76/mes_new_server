package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ShipmentService {
    List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo);

    List<ShipmentItemListVo> getSalesItemList(String saleIds);

    String saveShipmentInfo(ShipmentVo shipmentInfo, List<ShipmentItemListVo> itemList, List<MultipartFile> attachFileList) throws Exception;

    Map<String, Object> getShipmentInfo(String shipmentId);

    List<ShipmentItemListVo> getShipmentItemList(String shipmentId);
}
