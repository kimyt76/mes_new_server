package com.jct.mes_new.biz.order.controller;

import com.jct.mes_new.biz.order.service.ProgressService;
import com.jct.mes_new.biz.order.vo.ProgressVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/progress")
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/getProgressList")
    public List<ProgressVo> getProgressList(@RequestBody ProgressVo progressVo) {
        return progressService.getProgressList(progressVo);
    }
}
