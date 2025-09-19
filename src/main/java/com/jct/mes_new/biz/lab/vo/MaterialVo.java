package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialVo {

    private BigDecimal mappingId;
    private String itemCd;
    private String attachFileId;
    private String historyId;

    private String userId;
}
