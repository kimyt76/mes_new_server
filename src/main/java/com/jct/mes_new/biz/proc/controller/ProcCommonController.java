package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.work.vo.WorkOrderItemVo;
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

    /**
     * 작업자 조회
     * @param vo
     * @return
     */
    @PostMapping("/getWorkerList")
    public List<ProcCommonVo> getWorkerList(@RequestBody ProcCommonVo vo) {
        return  procCommonService.getWorkerList(vo);
    }

    /**
     * 칭량용기 조회
     * @return
     */
    @GetMapping("/getBagWeightList")
    public List<ProcCommonVo> getWorkerList() {
        return  procCommonService.getBagWeightList();
    }

    /**
     * 실헙장비 조회
     * @param storageCd
     * @return
     */
    @GetMapping("/getEquipmentList/{id}")
    public List<ProcCommonVo> getEquipmentList(@PathVariable("id") String storageCd) {
        return  procCommonService.getEquipmentList(storageCd);
    }

    /**
     * 곻정 item 상태 업데이트
     * @param vo
     * @return
     */
    @PostMapping("/updateProcStatus")
    public ResponseEntity<ApiResponse<Void>> updateProcStatus(@RequestBody ProcCommonVo vo) {
        String result = procCommonService.updateProcStatus(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }

    @PostMapping("/getProcTranList")
    public List<ProcTranVo> getProcTranList(@RequestBody ProcTranVo vo) {
        return  procCommonService.getProcTranList(vo);
    }

    @PostMapping("/saveProdInfo")
    public ResponseEntity<ApiResponse<Long>> saveProdInfo(@RequestBody ProcUseRequestVo vo) {
        Long prodInfoId = procCommonService.saveProdInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), prodInfoId));
    }

    @GetMapping("/getProdUseList/{id}")
    public List<ProcUseInfoVo> getProdUseList (@PathVariable("id") Long prodUseId) {
        return  procCommonService.getProdUseList(prodUseId);
    }

    @PostMapping("/getProcProdInfo")
    public ProcProdInfoVo getProcProdInfo (@RequestBody ProcCommonVo vo) {
        return  procCommonService.getProcProdInfo(vo);
    }

    @GetMapping("/getWorkRecordInfo/{id}")
    public WorkRecordVo getWorkRecordInfo (@PathVariable("id") Long workRecordId) {
        return  procCommonService.getWorkRecordInfo(workRecordId);
    }

    @PostMapping("/saveWorkRecordInfo")
    public ResponseEntity<ApiResponse<Void>> saveWorkRecordInfo(@RequestBody List<WorkRecordVo> recordList) {
        String result = procCommonService.saveWorkRecordInfo(recordList);
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

    @PostMapping("/getProcItemList")
    public List<ProcItemVo> getProcItemList(@RequestBody List<Long> ids) {
        return procCommonService.getProcItemList(ids);
    }

}
