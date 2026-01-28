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

    int insertContractInfo(ContractVo contractInfo);
    int insertContractItem(ContractItemVo item);

    int updateContractInfo(ContractVo contractInfo);

    void deleteContractItemList(String contractId);

    List<ContractItemVo> getContractItemList(List<String> contractIdList);


    void updateAttachFileId(String contractId, String attachFileId);
}
