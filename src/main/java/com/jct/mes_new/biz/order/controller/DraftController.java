package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.service.DraftService;
import com.jct.mes_new.biz.order.vo.DraftApprovalVo;
import com.jct.mes_new.biz.order.vo.DraftRequestVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/draft")
public class DraftController {

    private final DraftService draftService;
    private final MessageUtil messageUtil;

    @PostMapping("/getDraftList")
    public List<DraftVo> getDraftList(@RequestBody DraftVo draftVo) {
        return draftService.getDraftList(draftVo);
    }

    @GetMapping("/getDraftInfo/{id}")
    public DraftRequestVo getDraftInfo(@PathVariable("id") Long draftId) {
        return draftService.getDraftInfo(draftId);
    }

    @PostMapping(value = "/saveDraftInfo", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> saveDraftInfo(
                                                        @RequestPart("draftRequest") DraftRequestVo draftRequest,
                                                        @RequestPart(value = "newFiles", required = false)
                                                        List<MultipartFile> newFiles
                                                ) {
        draftRequest.setNewFiles(newFiles);

        Long draftId = draftService.saveDraftInfo(draftRequest);

        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"),draftId));
    }

    @PostMapping("/saveApprovalComment")
    public ResponseEntity<?> saveApprovalComment(@RequestBody DraftApprovalVo info){
        String result = draftService.saveApprovalComment(info);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), result));
    }




}
