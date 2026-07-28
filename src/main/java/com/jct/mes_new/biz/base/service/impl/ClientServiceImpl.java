package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ClientMapper;
import com.jct.mes_new.biz.base.service.ClientService;
import com.jct.mes_new.biz.base.vo.*;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientMapper clientMapper;

    public List<ClientVo> getClientList(ClientVo clientVo){
        return clientMapper.getClientList(clientVo);
    }

    public ClientRequestVo getClientInfo(String clientId){
        ClientRequestVo requestVo = new ClientRequestVo();

        requestVo.setClientInfo(clientMapper.getClientInfo(clientId));
        requestVo.setClientApprovalList(clientMapper.getClientApprovalList(clientId));
        requestVo.setClientDealList(clientMapper.getClientDealList(clientId));
        requestVo.setClientManagerList(clientMapper.getClientManagerList(clientId));
        requestVo.setClientAddressList(clientMapper.getClientAddressList(clientId));
        requestVo.setClientHistoryList(clientMapper.getClientHistoryList(clientId));

        return requestVo;
    }

    /**
     * 고객사 정보 저장
     * @param vo
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveClientInfo(ClientRequestVo vo) throws Exception {
        String msg = "저장되었습니다.";
        ClientVo clientInfo = vo.getClientInfo();
        List<ClientApprovalVo> approvalList =
                Optional.ofNullable(vo.getClientApprovalList())
                        .orElseGet(Collections::emptyList);
        List<ClientDealVo> dealList =
                Optional.ofNullable(vo.getClientDealList())
                        .orElseGet(Collections::emptyList);
        List<ClientManagerVo> managerList =
                Optional.ofNullable(vo.getClientManagerList())
                        .orElseGet(Collections::emptyList);
        List<ClientHistoryVo> historyList =
                Optional.ofNullable(vo.getClientHistoryList())
                        .orElseGet(Collections::emptyList);
        List<ClientAddressVo> addressList =
                Optional.ofNullable(vo.getClientAddressList())
                        .orElseGet(Collections::emptyList);

        List<Long> deleteApprovalIds = vo.getDeleteApprovalIds();
        List<Long> deleteDealIds = vo.getDeleteDealIds();
        List<Long> deleteManagerIds = vo.getDeleteManagerIds();
        List<Long> deleteAddressIds = vo.getDeleteAddressIds();
        List<Long> deleteHistoryIds = vo.getDeleteHistoryIds();
        String userId = UserUtil.getUserId();

        clientInfo.setUserId(userId);
        //마스터 저장 및 수정
        if ( clientInfo.getClientId() == null) {
            if (clientMapper.insertClientMst(clientInfo) <= 0 ){
                throw new BusinessException(ErrorCode.CREATED);
            }
        }else{
            if (clientMapper.updateClientMst(clientInfo) <= 0 ){
                throw new BusinessException(ErrorCode.UPDATED);
            }
        }
        //고객사 결재 방식
        for(ClientApprovalVo approvalVo : approvalList ){
            approvalVo.setClientId(clientInfo.getClientId());
            approvalVo.setUserId(userId);

            if(approvalVo.getClientApprovalId() == null){
                if (clientMapper.insertClientApproval(approvalVo) <= 0 ){
                    throw new BusinessException(ErrorCode.CREATED);
                }
            }else{
                if (clientMapper.updateClientApproval(approvalVo) <= 0 ){
                    throw new BusinessException(ErrorCode.UPDATED);
                }
            }
        }
        //고객사 거래방식
        for(ClientDealVo dealVo : dealList ){
            dealVo.setClientId(clientInfo.getClientId());
            dealVo.setUserId(userId);

            if(dealVo.getClientDealId() == null){
                if (clientMapper.insertClientDeal(dealVo) <= 0 ){
                    throw new BusinessException(ErrorCode.CREATED);
                }
            }else{
                if (clientMapper.updateClientDeal(dealVo) <= 0 ){
                    throw new BusinessException(ErrorCode.UPDATED);
                }
            }
        }
        //고객사 담당자
        for(ClientManagerVo managerVo : managerList ){
            managerVo.setClientId(clientInfo.getClientId());
            managerVo.setUserId(userId);

            if(managerVo.getClientManagerId() == null){
                if (clientMapper.insertClientManager(managerVo) <= 0 ){
                    throw new BusinessException(ErrorCode.CREATED);
                }
            }else{
                if (clientMapper.updateClientManager(managerVo) <= 0 ){
                    throw new BusinessException(ErrorCode.UPDATED);
                }
            }
        }
        //고객사 주소
        for(ClientAddressVo addressVo : addressList ){
            addressVo.setClientId(clientInfo.getClientId());
            addressVo.setUserId(userId);

            if(addressVo.getClientAddressId() == null){
                if (clientMapper.insertClientAddress(addressVo) <= 0 ){
                    throw new BusinessException(ErrorCode.CREATED);
                }
            }else{
                if (clientMapper.updateClientAddress(addressVo) <= 0 ){
                    throw new BusinessException(ErrorCode.UPDATED);
                }
            }
        }
        //고객사 변경이력
        for(ClientHistoryVo historyVo : historyList ){
            historyVo.setClientId(clientInfo.getClientId());
            historyVo.setUserId(userId);

            if(historyVo.getClientHistoryId() == null){
                if (clientMapper.insertClientHistory(historyVo) <= 0 ){
                    throw new BusinessException(ErrorCode.CREATED);
                }
            }else{
                if (clientMapper.updateClientHistory(historyVo) <= 0 ){
                    throw new BusinessException(ErrorCode.UPDATED);
                }
            }
        }
        // 고객사 결재방식 삭제
        if (deleteApprovalIds != null && !deleteApprovalIds.isEmpty()) {
            clientMapper.deleteClientApproval(deleteApprovalIds);
        }
        // 고객사 거래방식 삭제
        if (deleteDealIds != null && !deleteDealIds.isEmpty()) {
            clientMapper.deleteClientDeal(deleteDealIds);
        }
        // 고객사 담당자 삭제
        if (deleteManagerIds != null && !deleteManagerIds.isEmpty()) {
            clientMapper.deleteClientManager(deleteManagerIds);
        }
        // 고객사 주소 삭제
        if (deleteAddressIds != null && !deleteAddressIds.isEmpty()) {
            clientMapper.deleteClientAddress(deleteAddressIds);
        }
        // 고객사 변경이력 삭제
        if (deleteHistoryIds != null && !deleteHistoryIds.isEmpty()) {
            clientMapper.deleteClientHistory(deleteHistoryIds);
        }
        return msg;
    }

    public String getBusinessNoChecked(String businessNo) {
        String chk = "Y";

        if (clientMapper.getBusinessNoChecked(businessNo) > 0) {
            chk = "N";
        }
        return chk;
    }


}
