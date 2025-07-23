package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.order.service.OrderService;
import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.OrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/getOrderList")
    public List<OrderVo> getOrderList(@RequestBody OrderVo orderVo) {
        return orderService.getOrderList(orderVo);
    }

    @GetMapping("/getOrderInfo/{id}")
    public Map<String, Object> getOrderInfo(@PathVariable("id") String orderId) {
        return orderService.getOrderInfo(orderId);
    }

    @PostMapping("/saveOrderInfo")
    public ResponseEntity<?> saveOrderInfo(@RequestPart("orderInfo") String orderInfoStr,
                                           @RequestPart("approval") String approvalStr,
                                           @RequestPart(value = "orderFile", required = false) MultipartFile orderFile,
                                           @RequestPart(value = "prodFile", required = false) MultipartFile prodFile) throws Exception {
        //BoardVo boardVo = mapper.readValue(boardStr, BoardVo.class);
        try {
            ObjectMapper mapper = new ObjectMapper();
            OrderVo orderVo = mapper.readValue(orderInfoStr, OrderVo.class);
            ApprovalVo approvalVo = mapper.readValue(approvalStr, ApprovalVo.class);

            String result = orderService.saveOrderInfo(orderVo, approvalVo, orderFile, prodFile);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

    @GetMapping("/getSeq")
    public int getSeq() {
        return orderService.getSeq();
    }

    @GetMapping("/getApprovalInfo")
    public ApprovalVo getApprovalInfo(){
        String type = "order";
        return orderService.getApprovalInfo(type);
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<?> updateInfo(@RequestBody Map<String, String> info) throws Exception{
        try {
            String result = orderService.updateInfo(info);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

}
