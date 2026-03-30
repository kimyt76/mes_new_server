package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ClientMapper;
import com.jct.mes_new.biz.base.service.ClientService;
import com.jct.mes_new.biz.base.vo.*;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        requestVo.setClientAddressList(clientMapper.getAddressList(clientId));
        requestVo.setClientHistoryList(clientMapper.getHistoryList(clientId));

        return requestVo;
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveClientInfo(ClientRequestVo vo) throws Exception {
        String msg = "저장되었습니다.";
        ClientVo clientInfo = vo.getClientInfo();
        List<ClientManagerVo> managerList = vo.getClientManagerList();
        List<ClientHistoryVo> historyList = vo.getClientHistoryList();
        List<ClientAddressVo> addressList = vo.getClientAddressList();
        String userId = clientInfo.getUserId();

        String clientId = clientInfo.getClientId();
        String businessNo = clientInfo.getBusinessNo();

        // 신규일 때만 clientId 생성
        if (clientId == null || clientId.isEmpty()) {
            // 자기 자신 제외하고 사업자번호 중복 체크
            String result = this.getBusinessNoChecked(businessNo);

            if ("N".equals(result)) {
                throw new BusinessException("중복된 사업자 번호입니다.");
            }

            clientId = CommonUtil.generateUUID();
            clientInfo.setClientId(clientId);
        }

        if (clientMapper.saveClientInfo(clientInfo) <= 0) {
            throw new BusinessException("고객사 저장에 실패했습니다.");
        }

        clientMapper.deleteManagerList(clientId);
        clientMapper.deleteHistoryList(clientId);
        clientMapper.deleteAddressList(clientId);

        if (managerList != null && !managerList.isEmpty()) {
            for (ClientManagerVo managerVo : managerList) {
                managerVo.setClientId(clientId);
                managerVo.setUserId(userId);
            }
            if (clientMapper.saveManagerList(managerList) <= 0) {
                throw new RuntimeException("담당자 저장에 실패했습니다.");
            }
        }

        if (historyList != null && !historyList.isEmpty()) {
            for (ClientHistoryVo historyVo : historyList) {
                historyVo.setClientId(clientId);
                historyVo.setUserId(userId);
            }
            if (clientMapper.saveHistoryList(historyList) <= 0) {
                throw new Exception("변경이력 저장에 실패했습니다.");
            }
        }

        if (addressList != null && !addressList.isEmpty()) {
            for (ClientAddressVo addressVo : addressList) {
                addressVo.setClientId(clientId);
                addressVo.setUserId(userId);
            }
            if (clientMapper.saveAddressList(addressList) <= 0) {
                throw new Exception("주소 저장에 실패했습니다.");
            }
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
