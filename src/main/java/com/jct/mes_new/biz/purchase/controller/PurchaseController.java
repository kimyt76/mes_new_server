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
import com.jct.mes_new.config.util.JasperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param purId
     * @return
     */
    @GetMapping("/deletePurchase/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePurchase (@PathVariable("id") Long purId) {
        String result = purchaseService.deletePurchase(purId);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.deleted")));
    }

    /**
     * 구매 인쇄
     * @param vo
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/printOut")
    public ResponseEntity<Resource> printOut(@RequestBody PurchaseVo vo) throws Exception {
        List<PurchaseVo.searchPurchaseListVo> purList = getPurchaseList(vo);
        List<Map<String, Object>> list = new ArrayList<>();

        try {

            for(PurchaseVo.searchPurchaseListVo pur : purList) {
                Map<String,Object> map = new HashMap<>();

                map.put("transNo", pur.getPurDateSeq());
                map.put("customerName", pur.getCustomerName());
                map.put("itemName", pur.getItemName());
                map.put("sumQty", pur.getTotQty());
                map.put("sumSupplyAmt", pur.getTotPrice());
                map.put("srcStorageName", pur.getStorageName());
                map.put("orderType", pur.getOrderType());
                map.put("erpYn", "Y" );
                map.put("memberName", pur.getManagerName());
                list.add(map);
            }
            InputStream reportStream = getClass().getResourceAsStream("/report/mat_in_list.jrxml");
            byte[] pdfContent = JasperUtil.getPdfBinary(reportStream, list);
            ByteArrayResource resource = new ByteArrayResource(pdfContent);
            HttpHeaders header = JasperUtil.getHeader("mat_in_list", pdfContent.length);

            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("리포트 생성중 에러발생!");
        }
    }


}
