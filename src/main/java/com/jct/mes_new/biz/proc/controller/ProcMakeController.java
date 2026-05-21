package com.jct.mes_new.biz.proc.controller;

import com.jct.mes_new.biz.proc.service.ProcMakeService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/procMat")
public class ProcMakeController {

    private final ProcMakeService procMakeService;
    private final MessageUtil messageUtil;

    @PostMapping("/getMatList")
    public List<ProcMakeVo> getMatList(@RequestBody ProcMakeVo vo) {
        return procMakeService.getMatList(vo);
    }

    @PostMapping("/getMakeInfo")
    public MakeInfoVo getMakeInfo(@RequestBody ProcMakeVo vo) {
        return procMakeService.getMakeInfo(vo);
    }

    @PostMapping("/startProcMake")
    public String startProcMake(@RequestBody ProcMakeVo vo) {
        return procMakeService.startProcMake(vo);
    }

    @GetMapping("/getWeighQty/{id}")
    public Long getWeighQty(@PathVariable("id") Long weighId ){
        return procMakeService.getWeighQty(weighId);
    }

    @PostMapping("/insertRowMake")
    public ResponseEntity<ApiResponse<Void>> insertRowMake(@RequestBody ProcWeighBomVo vo) {
        String result = procMakeService.insertRowMake(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }
    @PostMapping("/saveMakeInfo")
    public ResponseEntity<ApiResponse<Void>> saveMakeInfo(@RequestBody MakeInfoVo vo) {
        String result = procMakeService.saveMakeInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    @PostMapping("/completeMake")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completeMake(@RequestBody ProcMakeVo vo){
        Long workProcId = procMakeService.completeMake(vo);
        Map<String, Object> result =  new HashMap<>();
        result.put("workProcId", workProcId);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), result));
    }

    @PostMapping("/downloadMatProc")
    public ResponseEntity<byte[]> downloadMatProc(@RequestBody ProcMakeVo vo){
        byte[] fileBytes = procMakeService.downloadMatProc(vo);
        String fileName = "제조공정기록서" + vo.getProcCd() + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(fileName, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .body(fileBytes);
    }


}
