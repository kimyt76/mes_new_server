package com.jct.mes_new.biz.common.controller;

import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.vo.CommonVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {

    private final CommonService commonService;

    @PostMapping("/getCommonList")
    public List<CommonVo> getCommonList(@RequestBody CommonVo commonVo) {
        return commonService.getCommonList(commonVo);
    }

    @PostMapping("/saveCommonInfo")
    public ResponseEntity<?> saveCommonInfo(@RequestBody CommonVo commonVo) {
        try {
            String result = commonService.saveCommonInfo(commonVo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

    @GetMapping("/getCodeList/{type}")
    public List<CommonVo> getCodeList(@PathVariable String type ){
        return commonService.getCodeList(type);
    }

    @GetMapping("/newSeq")
    public String newSeq( @RequestParam("itemTypeCd") String itemTypeCd,
                          @RequestParam("cd") String cd,
                          @RequestParam("seqLen") int seqLen) {

        return commonService.newSeq( itemTypeCd, cd,  seqLen );
    }

    @GetMapping("/getCommonInfo/{id}")
    public CommonVo getCommonInfo(@PathVariable("id") String comId) {
        return commonService.getCommonInfo(comId);
    }

}
