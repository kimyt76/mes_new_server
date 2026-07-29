package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.ContractSaveRequestVo;
import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ContractService {

    List<ContractVo> getContractList(ContractVo contractVo);

    Map<String, Object> getContractInfo(String contractId);


    Long saveContractInfo(ContractSaveRequestVo vo);

    String updateContractInfo(ContractSaveRequestVo vo);
}
