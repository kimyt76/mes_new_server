package com.jct.mes_new.biz.monitoring.controller;

import com.jct.mes_new.biz.monitoring.service.MonitoringService;
import com.jct.mes_new.biz.monitoring.vo.*;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitoring")
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final MessageUtil messageUtil;

    @GetMapping("/getEquipOperationInfoList")
    public MonitoringTagValueVo getEquipOperationInfoList() {
        return monitoringService.getEquipOperationInfoList();
    }

    @GetMapping("/getMatMonitoring")
    public List<MatMonitoringVo> getMatMonitoring() {
        return monitoringService.getMatMonitoring();
    }

    @PostMapping("/getProdPerformaceStatus")
    public List<ProdPerformaceStatusVo> getProdPerformaceStatus(@RequestBody ProdPerformaceStatusVo vo) {
        return monitoringService.getProdPerformaceStatus(vo);
    }

    @PostMapping("/getContactTagInfo")
    public List<TagInfoVo> getContactTagInfo(@RequestBody TagInfoVo vo) {
        return monitoringService.getContactTagInfo(vo);
    }

    @PostMapping("/getContactTagValue")
    public TagValueVo getContactTagValue(@RequestBody TagValueVo vo) {
        return monitoringService.getContactTagValue(vo);
    }

    @PostMapping("/getChargeMonitoringInfo")
    public MonitoringTagValueVo getChargeMonitoringInfo(@RequestBody TagInfoVo vo) {
        return monitoringService.getChargeMonitoringInfo(vo);
    }

}
