package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
public class OrderPlanTypeVo {
    private Long orderPlanId;

    private String poNo;
    private String typeCd;
    private Integer seq;
    private String itemType;
    private BigInteger qty;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate planDate;


    private String userId;


}
