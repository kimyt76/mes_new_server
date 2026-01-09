package com.jct.mes_new.biz.system.controller;


import com.jct.mes_new.biz.system.service.StorageService;
import com.jct.mes_new.biz.system.vo.StorageVo;
import com.jct.mes_new.config.common.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/getStorageList")
    public List<StorageVo> getStorageList(@RequestBody StorageVo vo) {
        return storageService.getStorageList(vo);
    }

    @GetMapping("/getStorageInfo/{id}")
    public StorageVo getStorageInfo(@PathVariable("id") String storageCd) {
        return storageService.getStorageInfo(storageCd);
    }

    @PostMapping("/saveStorageInfo")
    public RestResponse saveStorageInfo (@RequestBody StorageVo vo) {
        String result = storageService.saveStorageInfo(vo);
        return RestResponse.ok(result);
    }

}
