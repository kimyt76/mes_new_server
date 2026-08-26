package com.jct.mes_new.biz.monitoring.service;

import com.jct.mes_new.biz.monitoring.vo.*;

import java.util.List;

public interface MonitoringService {
    MonitoringTagValueVo getEquipOperationInfoList();

    List<MatMonitoringVo> getMatMonitoring();

    List<ProdPerformaceStatusVo> getProdPerformaceStatus(ProdPerformaceStatusVo vo);

    List<TagInfoVo> getContactTagInfo(TagInfoVo vo);

    TagValueVo getContactTagValue(TagValueVo vo);

    MonitoringTagValueVo getChargeMonitoringInfo(TagInfoVo vo);
}
