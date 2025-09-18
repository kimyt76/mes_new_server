package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.ClientAddressVo;
import com.jct.mes_new.biz.base.vo.ClientHistoryVo;
import com.jct.mes_new.biz.base.vo.ClientManagerVo;
import com.jct.mes_new.biz.base.vo.ClientVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClientMapper {

    List<ClientVo> getClientList(ClientVo clientVo);

    ClientVo getClientInfo(String clientId);

    List<ClientManagerVo> getManagerList(String clientId);

    List<ClientAddressVo> getAddressList(String clientId);

    List<ClientHistoryVo> getHistoryList(String clientId);

    void deleteHistoryList(String clientId);

    void deleteAddressList(String clientId);

    void deleteManagerList(String clientId);

    int saveClientInfo(ClientVo clientInfo);

    int saveManagerList(List<ClientManagerVo> managerList);

    int saveHistoryList(List<ClientHistoryVo> historyList);

    int saveAddressList(List<ClientAddressVo> addressList);

    int getBusinessNoChecked(String clientId);
}
