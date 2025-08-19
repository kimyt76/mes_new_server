package com.jct.mes_new.biz.lab.controller;

import com.jct.mes_new.biz.lab.service.IngredientService;
import com.jct.mes_new.biz.lab.vo.IngredientVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.util.retry.Retry;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lab")
public class IngredientController {

    private final IngredientService ingredientService;

    /**
     * 성분 정보 조회
     */
    @PostMapping("/getIngredientList")
    public List<IngredientVo> getIngredientList(@RequestBody IngredientVo ingredientVo) {
        return ingredientService.getIngredientList(ingredientVo);
    } ;

    /**
     * 성분정보 상세 조회
     */
    @GetMapping("/getIngredientInfo/{id}")
    public IngredientVo getIngredientInfo(@PathVariable("id") String ingredientCode ) {
        return ingredientService.getIngredientInfo(ingredientCode);
    }
    /**
     * 성분정보 등록
     */
    @PostMapping("/saveIngredientInfo")
    public ResponseEntity<?> saveIngredientInfo(@RequestBody IngredientVo ingredientVo) throws Exception {
        try {
            String result = ingredientService.saveIngredientInfo(ingredientVo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }
}
