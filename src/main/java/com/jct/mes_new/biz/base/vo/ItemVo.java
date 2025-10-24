package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemVo {
    private int rowNum;
    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private String itemTypeName;
    private String unit;
    private String spec;
    private String itemGrp1;
    private String itemGrp2;
    private String itemGrp3;
    private String itemCategory1;
    private String itemCategory2;
    private String itemCategory1Name;
    private String itemCategory2Name;
    private String customerCd;
    private String customerName;
    private BigDecimal inPrice;
    private BigDecimal outPrice;
    private String etc;
    private String useYn;
    private String itemGrp1Name;
    private String itemGrp2Name;
    private String itemGrp3Name;
    private String useCate;
    private String category;                 /*유형*/
    private String avgDeliveryDate;     /*평균납품일*/
    private String safeStock;           /* 안전재고*/

    private String unitWeight;         /*단위중량*/
    private String useSafeStockYn;     /*안전재고여부*/
    private BigDecimal safeStockQty;   /*안전재고량*/
    private String itemCondition; /*보관조건*/
    private String appearance;/*성상*/
    private String searchText;/*사용안함*/
    private String cosTypeCd;/*사용안함*/
    private String functionalTypeCd;/*기능성분류*/
    private BigDecimal theoryProdNumber1;/*이론생산계수1*/
    private BigDecimal theoryProdNumber2;/*이론생산계수2*/
    private String woodenPattern;/*목형종류*/
    private String prodType;/*제품타입*/
    private BigDecimal sheetWidth;/*사용안함*/
    private BigDecimal sheetLength;/*사용안함*/
    private BigDecimal sheetStacking;/*적층 수*/
    private String stdWeight;/*기준무게*/
    private String stdSize;/*기준사이즈*/
    private String matName;/*재료명*/
    private String specInfo;/*규격정보*/
    private String exAppearance;/*외관*/
    private String packingSpecValue;/*단위별포장규격(값)*/
    private String packingSpecUnit;/*단위별포장규격(단위)*/
    private String displayCapacity;/*표시용량*/
    private String workFlow;/*포장 공정 작업공정도*/
    private String reminderMemo;/*발행자 특이사항*/
    private String displayYield;/*수율공식(표시용)*/
    private BigDecimal stdYield;/*품목기준수율*/
    private BigDecimal prodPrice;/*사용안함*/
    private String exItemCd;/*사용안함*/
    private String weighType;/*칭량구분*/
    private String weighFuncType;/*기능구분*/
    private String relatedCustomer;/*관련고객사*/
    private String prodLgType;/*제품유형(대분류)*/
    private String prodMdType;/*제품유형(중분류)*/
    private String sheetSpec;/*시트규격(가로x세로)*/
    private String chargeResearcher;/*담당연구원*/
    private String chargeSalesman;/*영업담당자*/
    private String qcProcTestType;/*QC공정검사구분*/
    private String labNo;/*lab 번호*/
    private String chargingQtys;/*충전지시량*/
    private String chargingCnt;/*충전매수*/
    private String cappingRange;/*캡핑세기 측정범위*/
    private String essenceStd;/*에센스 충전량 기준*/
    private String coolingTemp;/*냉각온도기준*/
    private String history;/*이력관리*/
    private String vegan;/* 비건 lab*/
    private String halal;/* 할랄  lab*/
    private String rspo; /* rspo lab*/
    private String addtion; /*추가 lab*/

    List<String> prodLList;
    List<String> prodMList;

    private String codeNm;
    private String code;
    private String userId;

}