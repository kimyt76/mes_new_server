package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
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
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/procCommon")
public class ProcCommonController {

    private final ProcCommonService procCommonService;
    private final MessageUtil messageUtil;

    @PostMapping("/getWorkerList")
    public List<ProcCommonVo> getWorkerList(@RequestBody ProcCommonVo vo) {
        return  procCommonService.getWorkerList(vo);
    }

    @GetMapping("/getBagWeightList")
    public List<ProcCommonVo> getWorkerList() {
        return  procCommonService.getBagWeightList();
    }

    @GetMapping("/getEquipmentList/{id}")
    public List<ProcCommonVo> getEquipmentList(@PathVariable("id") String storageCd) {
        return  procCommonService.getEquipmentList(storageCd);
    }

    @PostMapping("/updateProcStatus")
    public ResponseEntity<ApiResponse<Void>> updateProcStatus(@RequestBody ProcCommonVo vo) {
        String result = procCommonService.updateProcStatus(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    @PostMapping("/getProcTranList")
    public List<ProcTranVo> getProcTranList(@RequestBody SearchCommonVo vo) {
        return  procCommonService.getProcTranList(vo);
    }

    @PostMapping("/saveProdInfo")
    public ResponseEntity<ApiResponse<Long>> saveProdInfo(@RequestBody ProcUseRequestVo vo) {
        Long prodInfoId = procCommonService.saveProdInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), prodInfoId));
    }

    @PostMapping("/getProdUseList/{id}")
    public List<ProcUseInfoVo> getProdUseList (@PathVariable("id") Long prodInfoId) {
        return  procCommonService.getProdUseList(prodInfoId);
    }


    @PostMapping("/getProcProdInfo")
    public ProcProdInfoVo getProcProdInfo (@RequestBody ProcCommonVo vo) {
        return  procCommonService.getProcProdInfo(vo);
    }

    @PostMapping("/saveWorkRecordInfo")
    public ResponseEntity<ApiResponse<Void>> saveWorkRecordInfo(@RequestBody WorkRecordVo vo) {
        String result = procCommonService.saveWorkRecordInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    @PostMapping("/downloadRecord")
    public ResponseEntity<byte[]> downloadRecord(@RequestBody ProcCommonVo vo){
        byte[] fileBytes = procCommonService.downloadRecord(vo);
        String fileName = "성적서_" + vo.getProcCd() + ".xlsx";
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
