package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClientDealVo {
    private Long clientDealId;
    private Long clientId;
    private String year;
    private BigDecimal salesAmt;
    private BigDecimal dealAmt;
    private BigDecimal orderQty;
    private String managerName;
    private LocalDate lastDealDate;
    private int orderDist;

    private String userId;

}
