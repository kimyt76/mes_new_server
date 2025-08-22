package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.order.service.DraftService;
import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/draft")
public class DraftController {

    private final DraftService draftService;

    @PostMapping("/getDraftList")
    public List<DraftVo> getDraftList(@RequestBody DraftVo draftVo) {
        return draftService.getDraftList(draftVo);
    }

    @GetMapping("/getDraftInfo/{id}")
    public Map<String, Object> getDraftInfo(@PathVariable("id") String draftId) {
        return draftService.getDraftInfo(draftId);
    }

    @PostMapping("/saveDraftInfo")
    public ResponseEntity<?> saveDraftInfo(@RequestPart("draftInfo") String draftInfoStr,
                                           @RequestPart("approval") String approvalStr,
                                           @RequestPart(value = "orderFile", required = false) MultipartFile orderFile,
                                           @RequestPart(value = "prodFile", required = false) MultipartFile prodFile) throws Exception {
        //BoardVo boardVo = mapper.readValue(boardStr, BoardVo.class);
        try {
            ObjectMapper mapper = new ObjectMapper();
            DraftVo draftVo = mapper.readValue(draftInfoStr, DraftVo.class);
            ApprovalVo approvalVo = mapper.readValue(approvalStr, ApprovalVo.class);

            String result = draftService.saveDraftInfo(draftVo, approvalVo, orderFile, prodFile);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

    @GetMapping("/getSeq")
    public int getSeq() {
        return draftService.getSeq();
    }

    @GetMapping("/getApprovalInfo")
    public ApprovalVo getApprovalInfo(){
        String type = "order";
        return draftService.getApprovalInfo(type);
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<?> updateInfo(@RequestBody Map<String, String> info) throws Exception{
        try {
            String result = draftService.updateInfo(info);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

}
