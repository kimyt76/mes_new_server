package com.jct.mes_new.biz.base.vo;

import lombok.Data;

@Data
public class StorageVo {

    private String storageCd;
    private String storageName;
    private String storageType;
    private String storageTypeName;
    private String prodProcessCd;
    private String prodProcessName;
    private String outCustomerCd;
    private String outCustomerName;
    private String companyName;
    private String useYn;

    private String userId;

}
