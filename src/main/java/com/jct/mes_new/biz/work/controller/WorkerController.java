package com.jct.mes_new.biz.work.controller;

import com.jct.mes_new.biz.work.service.WorkerService;
import com.jct.mes_new.config.common.MessageUtil;
import com.jct.mes_new.biz.work.vo.WorkerVo;
import com.jct.mes_new.config.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/worker")
public class WorkerController {

    private final WorkerService workerService;
    private final MessageUtil messageUtil;

    @PostMapping("/getWorkerAllList")
    public List<WorkerVo> getWorkerAllList (@RequestBody WorkerVo vo){
        return workerService.getWorkerAllList(vo);
    }

    @GetMapping("/getWorkerInfo/{id}")
    public WorkerVo getWorkerInfo (@PathVariable("id") String workerId){
        return workerService.getWorkerInfo(workerId);
    }

    @PostMapping("/saveWorkerInfo")
    public ResponseEntity<ApiResponse<Void>> saveWorkerInfo(@RequestBody WorkerVo vo) {
        String msg = workerService.saveWorkerInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

}
