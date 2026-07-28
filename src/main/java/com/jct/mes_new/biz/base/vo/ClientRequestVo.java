package com.jct.mes_new.biz.base.vo;

import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.Data;

import java.util.List;

@Data
public class ClientRequestVo {
    private ClientVo clientInfo;
    private List<ClientApprovalVo> clientApprovalList;
    private List<ClientDealVo> clientDealList;
    private List<ClientManagerVo> clientManagerList;
    private List<ClientAddressVo> clientAddressList;
    private List<ClientHistoryVo> clientHistoryList;

    private List<Long> deleteApprovalIds;
    private List<Long> deleteDealIds;
    private List<Long> deleteManagerIds;
    private List<Long> deleteAddressIds;
    private List<Long> deleteHistoryIds;
}
