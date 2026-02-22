package com.jct.mes_new.biz.qc.controller;

import com.jct.mes_new.biz.qc.service.QcTestTypeService;
import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qcTestType")
public class QcTestTypeController {

    private final QcTestTypeService qcTestTypeService;
    private final MessageUtil messageUtil;

    @PostMapping("/getQcTestTypeList")
    public List<QcTestTypeVo> getQcTestTypeList(@RequestBody QcTestTypeVo vo) {
        return qcTestTypeService.getQcTestTypeList(vo);
    }

    @GetMapping("/getQcTestTypeMethod/{id}")
    public List<QcTestTypeVo> getQcTestTypeMethod(@PathVariable("id") String itemCd) {
        return qcTestTypeService.getQcTestTypeMethod(itemCd);
    }

    @PostMapping("/saveQcTestTypeMethod")
    public ResponseEntity<ApiResponse<Void>> saveQcTestTypeMethod(@RequestBody List<QcTestTypeVo> list){
        String result = qcTestTypeService.saveQcTestTypeMethod(list);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }




}
