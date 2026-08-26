package com.jct.mes_new.biz.monitoring.vo;

import lombok.Data;

import java.util.List;

@Data
public class MonitoringTagValueVo {
    //SL1P03
    private List<TagValueVo> SL1;
    //SL1P11
    private List<TagValueVo> SL2;
    //SL1P21
    private List<TagValueVo> SL3;
    //SL1P22
    private List<TagValueVo> SL4;
}
