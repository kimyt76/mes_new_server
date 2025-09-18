package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContractMapper {

    List<ContractVo> getContractList(ContractVo contractVo);

    ContractVo getContractInfo(String contractId);

    List<ContractItemVo> getItemList(String contractId);

    int saveContractInfo(ContractVo contractInfo);

    int saveItemList(ContractItemVo itemListVo);

    void deleteContractItemList(String contractId);

    List<ContractItemVo> getContractItemList(List<String> contractIdList);
}
