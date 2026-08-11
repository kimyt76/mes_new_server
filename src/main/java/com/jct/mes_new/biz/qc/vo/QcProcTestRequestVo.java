package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.util.List;

@Data
public class QcProcTestRequestVo {

    QcProcTestVo qcProcTestMst;
    QcProcSampleVo qcProcSample;
    List<QcProcMethodVo> methodList;
    List<QcProcDetailVo> detailList;
    List<QcProcLinelVo> lineList;

}
