package com.jct.mes_new.biz.monitoring.service.impl;

import com.jct.mes_new.biz.monitoring.mapper.MonitoringMapper;
import com.jct.mes_new.biz.monitoring.service.MonitoringService;
import com.jct.mes_new.biz.monitoring.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MonitoringServiceImpl implements MonitoringService {

    private final MonitoringMapper monitoringMapper;


    public MonitoringTagValueVo getEquipOperationInfoList() {
        String tagCd = "";
        MonitoringTagValueVo tagValueVo = new MonitoringTagValueVo();
        //SL1P03
        tagCd = "SL1P04";
        tagValueVo.setSL1(monitoringMapper.getEquipOperationInfoList(tagCd));
        //SL1P11
        tagCd = "SL2P02";
        tagValueVo.setSL2(monitoringMapper.getEquipOperationInfoList(tagCd));
        //SL1P21
        tagCd = "SL3P02";
        tagValueVo.setSL3(monitoringMapper.getEquipOperationInfoList(tagCd));
        //SL1P22
        tagCd = "SL4P02";
        tagValueVo.setSL4(monitoringMapper.getEquipOperationInfoList(tagCd));

        return tagValueVo;
    }

    public List<MatMonitoringVo> getMatMonitoring(){
        return monitoringMapper.getMatMonitoring();
    }

    public List<ProdPerformaceStatusVo> getProdPerformaceStatus(ProdPerformaceStatusVo vo){
        return monitoringMapper.getProdPerformaceStatus(vo);
    }
    public List<TagInfoVo> getContactTagInfo(TagInfoVo vo){
        return monitoringMapper.getContactTagInfo(vo);
    }

    public TagValueVo getContactTagValue(TagValueVo vo){
        return monitoringMapper.getContactTagValue(vo);
    }
    public MonitoringTagValueVo getChargeMonitoringInfo(TagInfoVo vo){
        MonitoringTagValueVo tagValueVo = new MonitoringTagValueVo();
        //SL1P03
        vo.setTagCd("SL1P04");
        tagValueVo.setSL1(monitoringMapper.getChargeMonitoringInfo(vo));
        //SL1P11
        vo.setTagCd("SL2P02");
        tagValueVo.setSL2(monitoringMapper.getChargeMonitoringInfo(vo));
        //SL1P21
        vo.setTagCd("SL3P02");
        tagValueVo.setSL3(monitoringMapper.getChargeMonitoringInfo(vo));
        //SL1P22
        vo.setTagCd("SL4P02");
        tagValueVo.setSL4(monitoringMapper.getChargeMonitoringInfo(vo));

        return tagValueVo;
    }
}
