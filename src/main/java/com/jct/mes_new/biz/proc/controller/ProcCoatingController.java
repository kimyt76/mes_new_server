package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcCoatingService;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcProdInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/procCoating")
public class ProcCoatingController {

    private final ProcCoatingService procCoatingService;
    private final MessageUtil messageUtil;

    @PostMapping("/getCoatingList")
    public List<ProcCoatingVo> getCoatingList (@RequestBody ProcCoatingVo vo) {
        return  procCoatingService.getCoatingList(vo);
    }

    @PostMapping("/startProcCoating")
    public ResponseEntity<ApiResponse<Void>>  startProcCoating(@RequestBody ProcCoatingVo vo){
        String result = procCoatingService.startProcCoating(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }

    @PostMapping("/completeCoating")
    public ResponseEntity<ApiResponse<Void>>  completeCoating(@RequestBody ProcCoatingVo vo){
        String result = procCoatingService.completeCoating(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), null));
    }

//    @PostMapping("/downloadCoatingRecord/{id}")
//    public ResponseEntity<byte[]>  downloadCoatingRecord(@PathVariable Long workProcId){

//    byte[] fileBytes = procCoatingService.downloadCoatingRecord(workProcId);
//    String fileName = "성적서_" + qcTestId + ".xlsx";
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(
//            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//            .header(HttpHeaders.CONTENT_DISPOSITION,
//                    ContentDisposition.attachment()
//                                .filename(fileName, StandardCharsets.UTF_8)
//                                .build()
//                                .toString())
//            .body(fileBytes);
//
//
//
//    }

}
