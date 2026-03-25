package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.util.List;

@Data
public class QcTestRequestVo {
    private QcTestVo qcTestInfo;
    private List<QcTestTypeVo> qcTestTypeMethodList;
    private List<Long> deleteIds;
}
