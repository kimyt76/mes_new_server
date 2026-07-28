package com.jct.mes_new.biz.base.vo;

import lombok.Data;

@Data
public class ClientAddressVo {
    private Long clientAddressId;
    private Long clientId;
    private String addressType;
    private String location;
    private String address;
    private int orderDist;

    private String userId ;
}
