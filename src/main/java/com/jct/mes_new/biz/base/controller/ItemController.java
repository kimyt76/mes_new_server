package com.jct.mes_new.biz.base.controller;


import com.jct.mes_new.biz.base.service.ItemService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.config.common.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/getProdLList")
    public List<ItemVo> getProdLList(){
        return itemService.getProdLList();
    }

    @GetMapping("/getProdMList/{id}")
    public List<ItemVo> getProdMList(@PathVariable String id){
        return itemService.getProdMList(id);
    }

    @PostMapping("/getItemList")
    public List<ItemVo> getItemList(@RequestBody ItemVo itemVo){
        return itemService.getItemList(itemVo);
    }

    @PostMapping("/saveItemInfo")
    public RestResponse<Void> saveItemInfo(@RequestBody ItemVo itemVo){
        String result = itemService.saveItemInfo(itemVo);
        return RestResponse.okMessage(result, null);
    }

    @PostMapping("/updateItemInfo")
    public RestResponse<Void> updateItemInfo(@RequestBody ItemVo itemVo){
        String result = itemService.updateItemInfo(itemVo);
        return RestResponse.okMessage(result, null);
    }

    @GetMapping("/getItemInfo/{id}")
    public ItemVo getItemInfo(@PathVariable("id") String itemCd ){
        return itemService.getItemInfo(itemCd);
    }

    @GetMapping("/getItemCdCheck/{id}")
    public String getItemCdCheck(@PathVariable("id") String itemCd ){
        return itemService.getItemCdCheck(itemCd);
    }

    @PostMapping("/saveItemDetailInfo")
    public ResponseEntity<?> saveItemDetailInfo(@RequestBody ItemVo itemVo){
        try {
            String result = itemService.saveItemDetailInfo(itemVo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

    @PostMapping("/saveItemAddInfo")
    public RestResponse<Void> saveItemAddInfo(@RequestBody ItemVo itemVo){
        String result = itemService.saveItemAddInfo(itemVo);
        return RestResponse.okMessage(result, null);
    }

    @PostMapping("/updatePriceInfo")
    public ResponseEntity<?> updatePriceInfo(@RequestBody Map<String, Object> paramMap) {
       try {
            // Service로 Map 그대로 전달
            itemService.updatePriceInfoMap(paramMap);
            return ResponseEntity.ok("단가 변경 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("단가 변경 실패");
        }
    }



}
