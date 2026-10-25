package com.jct.mes_new.biz.qc.controller;

import com.jct.mes_new.biz.qc.service.QcTestTypeService;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.TestTypeMethodRequestVo;
import com.jct.mes_new.biz.qc.vo.TestTypeMethodVo;
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

    /**
     * 품질검사 유형 조회
     * @param vo
     * @return
     */
    @PostMapping("/getTestTypeMethodList")
    public List<TestTypeMethodVo> getTestTypeMethodList(@RequestBody TestTypeMethodVo vo) {
        return qcTestTypeService.getTestTypeMethodList(vo);
    }

    /**
     * 품질검사 유형 상세
     * @param itemCd
     * @return
     */
    @GetMapping("/getTestTypeMethod/{id}")
    public List<TestTypeMethodVo> getTestTypeMethod(@PathVariable("id") String itemCd) {
        return qcTestTypeService.getTestTypeMethod(itemCd);
    }

    /**
     * 품지검사 저장
     * @param vo
     * @return
     */
    @PostMapping("/saveTestTypeMethod")
    public ResponseEntity<ApiResponse<Void>> saveTestTypeMethod(@RequestBody TestTypeMethodRequestVo vo){
        String result = qcTestTypeService.saveTestTypeMethod(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }
    @PostMapping("/deleteTestTypeMethod")
    public ResponseEntity<ApiResponse<Void>> deleteTestTypeMethod(@RequestBody TestTypeMethodRequestVo vo){
        String result = qcTestTypeService.deleteTestTypeMethod(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.deleted")));
    }


    /**
     * 품목별검사 비교 조회
     * @param vo
     * @return
     */
    @PostMapping("/getQcTestTypeMethodComp")
    public List<QcTestTypeMethodVo> getQcTestTypeMethodComp(@RequestBody QcTestTypeMethodVo vo) {
        return qcTestTypeService.getQcTestTypeMethodComp(vo);
    }

}
