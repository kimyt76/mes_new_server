package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.BaseMgmtService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/base")
public class BaseMgmtController {

    private final BaseMgmtService baseMgmtService;

    @PostMapping("/getItemList")
    public List<ItemVo> getItemList(@RequestBody ItemVo itemVo) {

        return baseMgmtService.getItemList(itemVo);
    }

}
