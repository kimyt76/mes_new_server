package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ItemListVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ContractService {

    List<ContractVo> getContractList(ContractVo contractVo);

    Map<String, Object> getContractInfo(String contractId);

    int getNextSeq(String date);

    String saveContractInfo(ContractVo contractInfo, List<ItemListVo> itemList, List<MultipartFile> attachFileList) throws Exception;
}
