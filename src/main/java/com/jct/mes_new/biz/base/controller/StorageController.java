package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.StorageService;
import com.jct.mes_new.biz.base.vo.StorageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/storage")
public class StorageController {

    public final StorageService storageService;

    @PostMapping("/getStorageList")
    public List<StorageVo> getStorageList(@RequestBody StorageVo storageVo) {
        return storageService.getStorageList(storageVo);
    }

    @PostMapping("/saveStorageInfo")
    public ResponseEntity<?> saveStorageInfo (@RequestBody StorageVo storageVo) {
        try {
            String result = storageService.saveStorageInfo(storageVo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

}
