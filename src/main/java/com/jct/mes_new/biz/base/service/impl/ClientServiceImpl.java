package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ClientMapper;
import com.jct.mes_new.biz.base.service.ClientService;
import com.jct.mes_new.biz.base.vo.*;
import com.jct.mes_new.config.common.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        requestVo.setClientManagerList(clientMapper.getManagerList(clientId));
        requestVo.setClinetAddressList(clientMapper.getAddressList(clientId));
        requestVo.setClinetHistroryList(clientMapper.getHistoryList(clientId));

        return requestVo;
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveClientInfo(ClientRequestVo vo) throws Exception {
        String msg = "저장되었습니다.";
        ClientVo clientInfo = vo.getClientInfo();
        List<ClientManagerVo> managerList = vo.getClientManagerList();
        List<ClientHistoryVo> historyList = vo.getClinetHistroryList();
        List<ClientAddressVo> addressList = vo.getClinetAddressList();
        String userId = clientInfo.getUserId();

        try{
            if (clientInfo.getClientId().isEmpty() ){
                clientInfo.setClientId(CommonUtil.generateUUID());
            }

            if ( clientMapper.saveClientInfo(clientInfo) <= 0 ){
                throw new Exception("고객사 저장에 실패했습니다.");
            }else{
                clientMapper.deleteManagerList(clientInfo.getClientId());
                clientMapper.deleteHistoryList(clientInfo.getClientId());
                clientMapper.deleteAddressList(clientInfo.getClientId());

                if ( managerList != null && !managerList.isEmpty() ){
                    for (ClientManagerVo managerVo : managerList) {
                        managerVo.setClientId(clientInfo.getClientId());
                        managerVo.setUserId(userId);
                    }
                    if ( clientMapper.saveManagerList(managerList) <= 0 ) {
                        throw new Exception("담당자 저장에 실패했습니다.");
                    }
                }
                if ( historyList != null && !historyList.isEmpty() ){
                    for (ClientHistoryVo historyVo : historyList) {
                        historyVo.setClientId(clientInfo.getClientId());
                        historyVo.setUserId(userId);
                    }
                    if ( clientMapper.saveHistoryList(historyList) <= 0 ) {
                        throw new Exception("변경이력 저장에 실패했습니다.");
                    }
                }
                if ( addressList != null && !addressList.isEmpty()){
                    for (ClientAddressVo addressVo : addressList) {
                        addressVo.setClientId(clientInfo.getClientId());
                        addressVo.setUserId(userId);
                    }
                    if ( clientMapper.saveAddressList(addressList) <= 0 ) {
                        throw new Exception("주소 저장에 실패했습니다.");
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return msg;
    }

    public String getBusinessNoChecked(String businessNo) {
        String chk = "Y";

        if ( clientMapper.getBusinessNoChecked(businessNo) > 0 ){
            chk = "N";
        }
        return chk;
    }


}
