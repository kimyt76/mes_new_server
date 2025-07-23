package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ItemListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContractMapper {


    List<ContractVo> getContractList(ContractVo contractVo);

    ContractVo getContractInfo(String contractId);

    List<ItemListVo> getContractItemList(String contractId);

    int getNextSeq(@Param("contractDate") String date);

    int saveContractInfo(ContractVo contractInfo);

    int saveItemList(ItemListVo itemListVo);

    void deleteContractItemList(String contractId);
}
