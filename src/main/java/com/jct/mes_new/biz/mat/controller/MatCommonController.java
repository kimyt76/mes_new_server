package com.jct.mes_new.biz.mat.controller;

import com.jct.mes_new.biz.lab.vo.BomVo;
import com.jct.mes_new.biz.mat.service.MatCommonService;
import com.jct.mes_new.biz.mat.vo.MatPlanVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/matCommon")
public class MatCommonController {

    private final MatCommonService matCommonService;


    /**
     * 소요량 계산 원재료
     * @param mapList
     * @return
     */
    @PostMapping("/getItemStockList")
    public List<Map<String , Object>> getItemStockList(@RequestBody List<BomVo> mapList) {
        String itemTypeCd = "M1";
        return matCommonService.getItemStockList(mapList, itemTypeCd);
    }

    /**
     * 소요량 계산 부자재
     * @param mapList
     * @return
     */
    @PostMapping("/getRequiredAmount")
    public Map<String , Object> getRequiredAmount(@RequestBody List<Map<String, Object>> mapList) {
        return matCommonService.getRequiredAmount(mapList);
    }
}
