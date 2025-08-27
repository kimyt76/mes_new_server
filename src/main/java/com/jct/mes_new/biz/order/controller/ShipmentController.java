package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.service.ShipmentService;
import com.jct.mes_new.biz.order.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping("/getShipmentList")
    public List<ShipmentVo> getShipmentList(@RequestBody ShipmentVo shipmentVo) {
        return shipmentService.getShipmentList(shipmentVo);
    }

    @GetMapping("/getShipmentItemList/{id}")
    public List<ShipmentItemListVo> getShipmentItemList(@PathVariable("id") String shipmentId) {
        return shipmentService.getShipmentItemList(shipmentId);
    }

    @GetMapping("/getShipmentInfo/{id}")
    public Map<String, Object> getShipmentInfo (@PathVariable("id") String shipmentId) {
        return shipmentService.getShipmentInfo(shipmentId);
    }

    @GetMapping("/getSalesItemList/{ids}")
    public List<ShipmentItemListVo> getSalesItemList(@PathVariable("ids") String saleIds) {
        return shipmentService.getSalesItemList(saleIds);
    }

    @PostMapping("/saveShipmentInfo")
    public ResponseEntity<?> saveShipmentInfo ( @RequestPart("shipmentInfo") String shipmentInfoJson,
                                                @RequestPart("itemList") String itemListJson,
                                                @RequestPart(value = "attachFile" , required = false) List<MultipartFile> attachFileList
                                                ) throws Exception{

        try {
            ObjectMapper mapper = new ObjectMapper();

            ShipmentVo shipmentInfo = mapper.readValue(shipmentInfoJson, ShipmentVo.class);
            List<ShipmentItemListVo> itemList = mapper.readValue(itemListJson, new TypeReference<List<ShipmentItemListVo>>() {});
            String result = shipmentService.saveShipmentInfo(shipmentInfo, itemList, attachFileList);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

    @PostMapping("updateShipmentInfo")
    public ResponseEntity<?> updateShipmentInfo ( @RequestPart("shipmentInfo") String shipmentInfoJson,
                                                  @RequestPart("itemList") String itemListJson,
                                                  @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
                                                  @RequestPart(value = "deleteFiles", required = false) String deleteFilesJson,
                                                  @RequestPart(value = "keptFiles", required = false) String keptFilesJson
                                                ) throws Exception {

        try{
            ObjectMapper mapper = new ObjectMapper();

            ShipmentSaveRequestVo vo = new ShipmentSaveRequestVo();

            vo.setShipmentInfo(mapper.readValue((shipmentInfoJson), ShipmentVo.class));
            vo.setItemList(mapper.readValue(itemListJson, new TypeReference<List<ShipmentItemListVo>>() {}));
            vo.setNewFiles(newFiles != null ? newFiles : new ArrayList<>());

            List<FileVo> deleteFiles = new ArrayList<>();
            if (deleteFilesJson != null && !deleteFilesJson.isEmpty()) {
                deleteFiles = mapper.readValue(deleteFilesJson, new TypeReference<List<FileVo>>() {});
            }

            vo.setDeleteFiles(deleteFiles);

            vo.setKeptFiles(keptFilesJson != null ?
                    mapper.readValue(keptFilesJson, new TypeReference<List<FileVo>>() {}) :
                    new ArrayList<>());

            String result = shipmentService.updateShipmentInfo(vo);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }

    }
}
