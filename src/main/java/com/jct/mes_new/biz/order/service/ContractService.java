package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.ContractSaveRequestVo;
import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemListVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ContractService {

    List<ContractVo> getContractList(ContractVo contractVo);

    Map<String, Object> getContractInfo(String contractId);

    String saveContractInfo(ContractVo contractInfo, List<ContractItemListVo> itemList, List<MultipartFile> attachFileList) throws Exception;

    String updateContractInfo(ContractSaveRequestVo vo) throws Exception;
}
