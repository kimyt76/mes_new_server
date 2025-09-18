package com.jct.mes_new.biz.base.service;

import com.jct.mes_new.biz.base.vo.*;

import java.util.List;
import java.util.Map;

public interface ClientService {

    List<ClientVo> getClientList(ClientVo clientVo);

    ClientRequestVo getClientInfo(String clientId);

    String saveClientInfo(ClientRequestVo vo) throws Exception;

    String getBusinessNoChecked(String businessNo);
}
