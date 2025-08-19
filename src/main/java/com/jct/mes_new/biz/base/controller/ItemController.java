package com.jct.mes_new.biz.base.controller;


import com.jct.mes_new.biz.base.service.ItemService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> saveItemInfo(@RequestBody ItemVo itemVo){
        try {
            String result = itemService.saveItemInfo(itemVo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }
    @PostMapping("/updateItemInfo")
    public ResponseEntity<?> updateItemInfo(@RequestBody ItemVo itemVo){
        try {
            String result = itemService.updateItemInfo(itemVo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
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



}
