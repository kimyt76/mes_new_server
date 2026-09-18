package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
public class ProcTranVo {

    private LocalDate prodDate;
    private LocalDate tranDate;
    private BigInteger seq;
    private Long inTranId;
    private Long outTranId;
    private Long tranItemId;

    private String poNo;
    private String areaCd;
    private String storageCd;
    private String makeNo;
    private String lotNo;
    private String lotNo2;
    private String itemCd;
    private String itemName;
    private String tranOutItems;
    private String qty;

    private String managerId;
    private String managerName;
    private String tarStorageCd;
    private String srcStorageCd;
    private String etc;
    private String procCd;

    private String remark;
    private String testNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;    //지시일
    private LocalDate endDate;    //지시일

    private String userId;

}
