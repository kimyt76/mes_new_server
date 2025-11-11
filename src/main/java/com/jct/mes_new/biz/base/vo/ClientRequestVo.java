package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class ClientRequestVo {
    private ClientVo clientInfo;
    private List<ClientManagerVo> clientManagerList;
    private List<ClientAddressVo> clientAddressList;
    private List<ClientHistoryVo> clientHistoryList;
}
