package com.jct.mes_new.biz.order.controller;

import com.jct.mes_new.biz.common.service.FileHandlerService;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.service.OrderService;
import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.BoardVo;
import com.jct.mes_new.biz.order.vo.OrderVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.HashMap;
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
    public String saveOrderInfo(OrderVo orderVo,
                                @ModelAttribute("approvalVo") ApprovalVo approvalVo,
                                @RequestPart(value = "orderFile", required = false) MultipartFile orderFile,
                                @RequestPart(value = "prodFile", required = false) MultipartFile prodFile) throws Exception {
        return orderService.saveOrderInfo(orderVo, approvalVo, orderFile, prodFile);
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

}
