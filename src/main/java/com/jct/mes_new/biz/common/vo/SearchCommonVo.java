package com.jct.mes_new.biz.common.vo;

import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Data
public class SearchCommonVo {
    private LocalDate strDate;
    private LocalDate endDate;
    private LocalDate stdDate;

    private String areaCd;
    private String storageCd;
    private String procCd;
    private String storageName;
    private String itemCd;
    private String itemName;
    private String itemTypeCd;

    private String orderType;   //사급:  자급 :

    private String customerCd;
    private String customerName;
    private String clientId;
    private String clientCd;
    private String clientName;

    private String passStatus;
    private String testStatus;
    private String procStatus;
    private String batchStatus;

    private String managerName;
    private String useYn;



}
