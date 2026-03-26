package com.jct.mes_new.biz.qc.controller;

import com.jct.mes_new.biz.qc.service.QcTestTypeService;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qcTestType")
public class QcTestTypeController {

    private final QcTestTypeService qcTestTypeService;
    private final MessageUtil messageUtil;

    /**
     * 품질검사 유형 조회
     * @param vo
     * @return
     */
    @PostMapping("/getQcTestTypeList")
    public List<QcTestTypeVo> getQcTestTypeList(@RequestBody QcTestTypeVo vo) {
        return qcTestTypeService.getQcTestTypeList(vo);
    }

    @GetMapping("/getQcTestTypeMethod/{id}")
    public List<QcTestTypeVo> getQcTestTypeMethod(@PathVariable("id") String itemCd) {
        return qcTestTypeService.getQcTestTypeMethod(itemCd);
    }

    @PostMapping("/saveQcTestTypeMethod")
    public ResponseEntity<ApiResponse<Void>> saveQcTestTypeMethod(@RequestBody QcTestRequestVo vo){
        String result = qcTestTypeService.saveQcTestTypeMethod(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


    /**
     * 품목별검사 비교 조회
     * @param vo
     * @return
     */
    @PostMapping("/getQcTestTypeMethodComp")
    public List<QcTestTypeVo> getQcTestTypeMethodComp(@RequestBody QcTestTypeVo vo) {
        return qcTestTypeService.getQcTestTypeMethodComp(vo);
    }

}
