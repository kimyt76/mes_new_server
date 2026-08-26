package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.ProdService;
import com.jct.mes_new.biz.base.vo.ProdVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prod")
public class ProdController {
    private final ProdService prodService;


    @PostMapping("/getProdPerformanc")
    public List<ProdVo> getProdPerformanc (@RequestBody ProdVo vo){
        return prodService.getProdPerformanc(vo);
    }

}
