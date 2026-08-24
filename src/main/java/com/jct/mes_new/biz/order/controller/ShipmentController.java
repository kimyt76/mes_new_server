package com.jct.mes_new.biz.order.controller;

import com.jct.mes_new.biz.order.service.ShipmentService;
import com.jct.mes_new.biz.order.vo.ShipmentRequestVo;
import com.jct.mes_new.biz.order.vo.ShipmentVo;
import com.jct.mes_new.biz.order.vo.ShipmentWorkOrderVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shipment")
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final MessageUtil messageUtil;

    @PostMapping("/getShipmentList")
    public List<ShipmentVo> getShipmentList(@RequestBody ShipmentVo shipmentVo) {
        return shipmentService.getShipmentList(shipmentVo);
    }

    @GetMapping("/getShipmentInfo/{id}")
    public Map<String, Object> getShipmentInfo (@PathVariable("id") Long shipmentId) {
        return shipmentService.getShipmentInfo(shipmentId);
    }


    @PostMapping("/getWorkOrderItemList")
    public List<ShipmentWorkOrderVo> getWorkOrderItemList(@RequestBody ShipmentWorkOrderVo vo) {
        return shipmentService.getWorkOrderItemList(vo);
    }

    @PostMapping(value = "/saveShipmentInfo",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public  ResponseEntity<ApiResponse<Long>> saveShipmentInfo(@RequestPart("request")
                                                               ShipmentRequestVo vo,
                                                               @RequestPart(value = "newFiles", required = false)
                                                               List<MultipartFile> newFiles ) {
        vo.setNewFiles(newFiles);
        String result = shipmentService.saveShipmentInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("updateShipmentInfo")
    public ResponseEntity<?> updateShipmentInfo ( @RequestPart("request")
                                                      ShipmentRequestVo vo,
                                                  @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles
                                                ) throws Exception {

        vo.setNewFiles(newFiles);

        String result = shipmentService.updateShipmentInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    @GetMapping("/printTransactionStatement/{shipmentId}")
    public ResponseEntity<byte[]> printTransactionStatement(@PathVariable Long shipmentId) {
        byte[] pdf = shipmentService.printTransactionStatement(shipmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("transaction_statement.pdf", StandardCharsets.UTF_8)
                        .build()
        );
        return ResponseEntity.ok().headers(headers).body(pdf);
    }


}
