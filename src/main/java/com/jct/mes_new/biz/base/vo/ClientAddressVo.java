package com.jct.mes_new.biz.base.vo;

import lombok.Data;

@Data
public class ClientAddressVo {
    private int addressId;
    private String clientId;
    private String address;
    private int orderDist;

    private String userId ;
}
