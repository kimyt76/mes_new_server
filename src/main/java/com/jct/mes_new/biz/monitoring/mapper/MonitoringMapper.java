package com.jct.mes_new.biz.monitoring.mapper;

import com.jct.mes_new.biz.monitoring.vo.MatMonitoringVo;
import com.jct.mes_new.biz.monitoring.vo.ProdPerformaceStatusVo;
import com.jct.mes_new.biz.monitoring.vo.TagInfoVo;
import com.jct.mes_new.biz.monitoring.vo.TagValueVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MonitoringMapper {


    List<TagValueVo> getEquipOperationInfoList(String tagCd);

    List<MatMonitoringVo> getMatMonitoring();

    List<ProdPerformaceStatusVo> getProdPerformaceStatus(ProdPerformaceStatusVo vo);

    List<TagInfoVo> getContactTagInfo(TagInfoVo vo);
    TagValueVo getContactTagValue(TagValueVo vo);

    List<TagValueVo> getChargeMonitoringInfo(TagInfoVo vo);
}
