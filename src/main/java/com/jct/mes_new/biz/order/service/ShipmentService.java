package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ShipmentService {
    List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo);

    String saveShipmentInfo(ShipmentRequestVo vo);

    Map<String, Object> getShipmentInfo(Long shipmentId);

    String updateShipmentInfo(ShipmentRequestVo vo) throws Exception;

    List<ShipmentWorkOrderVo> getWorkOrderItemList(ShipmentWorkOrderVo vo);
}
