package com.jct.mes_new.biz.common.controller;

import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.vo.BarCodeVo;
import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.common.vo.ReqPrinting;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.JasperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {

    private final CommonService commonService;

    /**
     * 공통코드 조회
     * @param commonVo
     * @return
     */
    @PostMapping("/getCommonList")
    public List<CommonVo> getCommonList(@RequestBody CommonVo commonVo) {
        return commonService.getCommonList(commonVo);
    }

    /**
     * 공통코드 상셍조회
     * @param commonVo
     * @return
     */
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

    /**
     * 공토코드 조회(select용)
     * @param type
     * @return
     */
    @GetMapping("/getCodeList/{type}")
    public List<CommonVo> getCodeList(@PathVariable String type ){
        return commonService.getCodeList(type);
    }

    /**
     * 신규  seq  조회
     * @param itemTypeCd
     * @param cd
     * @param seqLen
     * @return
     */
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

    /**
     * 테이블 별  차기 seq  조회
     * @param itemTypeCd
     * @param cd
     * @param date
     * @return
     */
    @GetMapping("/getNextSeq")
    public int getNextSeq( @RequestParam("tb") String itemTypeCd,
                              @RequestParam("cd") String cd,
                              @RequestParam("date") String date) {

        return commonService.getNextSeq( itemTypeCd, cd,  date );
    }

    @PostMapping("barcodePrint")
    public ResponseEntity<Resource>  barcodePrint(@RequestBody ReqPrinting[] vo) throws Exception {
        try {
            byte[] pdfContent = commonService.createQrCodeLabels(vo);

            ByteArrayResource resource = new ByteArrayResource(pdfContent);
            HttpHeaders header = JasperUtil.getHeader("qrcode_label_list", pdfContent.length);
            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException(ErrorCode.FAIL_QRCODE);
        }
    }

}
