package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.service.ContractService;
import com.jct.mes_new.biz.order.vo.ContractItemVo;
import com.jct.mes_new.biz.order.vo.ContractSaveRequestVo;
import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/contract")
public class ContractController {

    private final ContractService contractService;
    private final MessageUtil messageUtil;

    @PostMapping("/getContractList")
    public List<ContractVo> getContractList(@RequestBody ContractVo contractVo) {
        return contractService.getContractList(contractVo);
    }

    @GetMapping("/getContractInfo/{id}")
    public Map<String, Object> getContractInfo(@PathVariable("id") String contractId) {
        return contractService.getContractInfo(contractId);
    }

    @PostMapping(value = "/saveContractInfo",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public  ResponseEntity<ApiResponse<Long>> saveContractInfo( @RequestPart("request")
                                                                                   ContractSaveRequestVo vo,
                                                                               @RequestPart(value = "newFiles", required = false)
                                                                                   List<MultipartFile> newFiles ) {
        vo.setNewFiles(newFiles);
        Long contractId = contractService.saveContractInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), contractId));
    }


    @PostMapping(value = "/updateContractInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<ApiResponse<Map<String, String>>> updateContractInfo(
                                                    @RequestPart("request")
                                                    ContractSaveRequestVo vo,
                                                    @RequestPart(
                                                            value = "newFiles",
                                                            required = false
                                                    )
                                                    List<MultipartFile> newFiles ){

        vo.setNewFiles(newFiles == null? Collections.emptyList(): newFiles);
        String result = contractService.updateContractInfo(vo);

        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }



}
