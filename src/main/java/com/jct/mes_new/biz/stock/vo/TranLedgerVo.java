package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TranLedgerVo {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String itemGrp1;
    private String orderType;
    private String itemTypeCd;
    private String itemTypeName;
    private String itemCd;
    private String itemName;
    private String customerName;

    private BigDecimal asQty;   //입고
    private BigDecimal esQty;   //출고
    private BigDecimal psQty;   //반품조정
    private BigDecimal rsQty;   //폐기조정
    private BigDecimal tsQty;   //검사샘풀
    private BigDecimal qsQty;   //연구샘플
    private BigDecimal ssQty;   //실사조정
    private BigDecimal vsQty;   //과입조정
    private BigDecimal wsQty;   //계정변경
    private BigDecimal xsQty;   //매출조정
    private BigDecimal usQty;   //사용조정
    private BigDecimal basicQty;   //기초재고
    private BigDecimal finalQty;   //기말재고
    private BigDecimal basicAmt;   //기초금액
    private BigDecimal finalAmt;   //기말재고
    private BigDecimal inPrice;   //단가
    private BigDecimal asAmt;   //입고금액
    private BigDecimal esAmt;   //출고금액
}
