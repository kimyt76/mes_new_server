package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

@Data
public class ProcTestEquipVo {
    private Long procEquipmentId;
    private Long procTestId;
    private String equipmentCd;
    private String equipmentName;
    private Integer distOrder;

    private String userId;
}
