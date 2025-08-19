package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemListVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContractMapper {

    List<ContractVo> getContractList(ContractVo contractVo);

    ContractVo getContractInfo(String contractId);

    List<ContractItemListVo> getItemList(String contractId);

    int saveContractInfo(ContractVo contractInfo);

    int saveItemList(ContractItemListVo itemListVo);

    void deleteContractItemList(String contractId);

    List<ContractItemListVo> getContractItemList(List<String> contractIdList);
}
