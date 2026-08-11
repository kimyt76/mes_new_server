package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

@Data
public class QcProcLinelVo {
    private Long qcProcTestLineId;
    private Long qcProcTestMstId;
    private String  testType;
    private Integer orderDist;
    private String lineName;
    private String scaleCd;
    private String scaleNickname;

    private String userId;

}
