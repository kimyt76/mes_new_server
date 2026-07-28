package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClientMapper {

    int getBusinessNoChecked(String clientId);

    List<ClientVo> getClientList(ClientVo clientVo);
    ClientVo getClientInfo(String clientId);
    List<ClientApprovalVo> getClientApprovalList(String clientId);
    List<ClientDealVo> getClientDealList(String clientId);
    List<ClientManagerVo> getClientManagerList(String clientId);
    List<ClientAddressVo> getClientAddressList(String clientId);
    List<ClientHistoryVo> getClientHistoryList(String clientId);

    int insertClientMst(ClientVo clientInfo);
    int updateClientMst(ClientVo clientInfo);

    int insertClientApproval(ClientApprovalVo approvalVo);
    int updateClientApproval(ClientApprovalVo approvalVo);

    int insertClientDeal(ClientDealVo dealVo);
    int updateClientDeal(ClientDealVo dealVo);

    int insertClientManager(ClientManagerVo managerVo);
    int updateClientManager(ClientManagerVo managerVo);

    int insertClientAddress(ClientAddressVo addressVo);
    int updateClientAddress(ClientAddressVo addressVo);

    int insertClientHistory(ClientHistoryVo historyVo);
    int updateClientHistory(ClientHistoryVo historyVo);

    void deleteClientApproval(@Param("list") List<Long> deleteApprovalIds);
    void deleteClientDeal(@Param("list") List<Long> deleteDealIds);
    void deleteClientManager(@Param("list") List<Long> deleteManagerIds);
    void deleteClientAddress(@Param("list") List<Long> deleteAddressIds);
    void deleteClientHistory(@Param("list") List<Long> deleteHistoryIds);



}
