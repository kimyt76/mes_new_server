package com.jct.mes_new.biz.purchase.controller;


import com.jct.mes_new.biz.base.service.CustomerService;
import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.service.MailService;
import com.jct.mes_new.biz.purchase.service.PurchaseOrderService;
import com.jct.mes_new.biz.purchase.service.PurchaseService;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final MailService mailService;
    private final MessageUtil messageUtil;
    private final CommonService commonService;
    private final CustomerService customerService;


    /**
     * 구매 조회
     * @param vo
     * @return
     */
    @PostMapping("/getPurchaseList")
    public List<PurchaseVo.searchPurchaseListVo > getPurchaseList(@RequestBody PurchaseVo vo) {
        return purchaseService.getPurchaseList(vo);
    }

    /**
     * 구매 상세
     * @param purId
     * @return
     */
    @GetMapping("/getPurchaseInfo/{id}")
    public PurchaseRequestVo getPurchaseInfo(@PathVariable("id") Long purId) {
        return purchaseService.getPurchaseInfo(purId);
    }

    /**
     * 구매저장
     * @param vo
     * @return
     */
    @PostMapping("/savePurchaseInfo")
    public ResponseEntity<ApiResponse<Void>> savePurchaseInfo (@RequestBody PurchaseRequestVo vo) {
        String result = purchaseService.savePurchaseInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    /**
     * 구매 수정
     * @param vo
     * @return
     */
    @PostMapping("/updatePurchaseInfo")
    public ResponseEntity<ApiResponse<Void>> updatePurchaseInfo (@RequestBody PurchaseRequestVo vo) {
        String result = purchaseService.updatePurchaseInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    /**
     * 구매 삭제
     * @param vo
     * @return
     */
    @GetMapping("/deletePurchase/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePurchase (@PathVariable("id") Long purId) {
        String result = purchaseService.deletePurchase(purId);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.deleted")));
    }




}
