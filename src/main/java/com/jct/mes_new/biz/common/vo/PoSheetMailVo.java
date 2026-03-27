package com.jct.mes_new.biz.common.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PoSheetMailVo {
    String customerName;
    String orderNo;
    String orderDate;
    String senderEmail;
    String memo;
}
