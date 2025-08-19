package com.jct.mes_new.biz.order.controller;


import com.jct.mes_new.biz.order.service.SaleService;
import com.jct.mes_new.biz.order.vo.SaleItemListVo;
import com.jct.mes_new.biz.order.vo.SaleRequesstVo;
import com.jct.mes_new.biz.order.vo.ContractItemListVo;
import com.jct.mes_new.biz.order.vo.SaleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/getSaleList")
    public List<SaleVo> getSaleList(@RequestBody SaleVo saleVo) {
        return saleService.getSaleList(saleVo);
    }

    @GetMapping("/getSaleInfo/{id}")
    public Map<String, Object> getSaleInfo(@PathVariable("id") String saleId) {
        return saleService.getSaleInfo(saleId);
    }

    @GetMapping("/getContractItemList/{ids}")
    public List<ContractItemListVo> getContractItemList(@PathVariable("ids") String contractIds) {
        return saleService.getContractItemList(contractIds);
    }

    @GetMapping("/getSaleItemList/{id}")
    public List<SaleItemListVo> getSaleItemList(@PathVariable("id") String saleId) {
        return saleService.getSaleItemList(saleId);
    }

    @PostMapping("/saveSaleInfo")
    public ResponseEntity<?> saveSaleInfo(@RequestBody SaleRequesstVo vo) throws Exception {

        try {
            SaleVo saleInfo = vo.getSaleInfo();
            List<SaleItemListVo> itemList = vo.getItemList();

            String result = saleService.saveSaleInfo(saleInfo, itemList);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }


}
