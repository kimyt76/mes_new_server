package com.jct.mes_new.biz.base.controller;


import com.jct.mes_new.biz.base.service.ItemService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import com.jct.mes_new.config.util.RestResponse;
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
    private final MessageUtil messageUtil;

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
    public ResponseEntity<ApiResponse<Void>> saveItemInfo(@RequestBody ItemVo itemVo){
        String result = itemService.saveItemInfo(itemVo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("/updateItemInfo")
    public ResponseEntity<ApiResponse<Void>> updateItemInfo(@RequestBody ItemVo itemVo){
        String result = itemService.updateItemInfo(itemVo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
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
    public ResponseEntity<ApiResponse<Void>> saveItemDetailInfo(@RequestBody ItemVo itemVo){
        String result = itemService.saveItemDetailInfo(itemVo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }
    @PostMapping("/saveItemAddInfo")
    public ResponseEntity<ApiResponse<Void>> saveItemAddInfo(@RequestBody ItemVo itemVo){
        String result = itemService.saveItemAddInfo(itemVo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }
    @PostMapping("/updatePriceInfo")
    public ResponseEntity<ApiResponse<Void>> updatePriceInfo(@RequestBody Map<String, Object> paramMap) {
        // Service로 Map 그대로 전달
        itemService.updatePriceInfoMap(paramMap);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }



}
