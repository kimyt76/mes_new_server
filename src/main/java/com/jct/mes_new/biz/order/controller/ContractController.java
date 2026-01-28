package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.service.ContractService;
import com.jct.mes_new.biz.order.vo.ContractSaveRequestVo;
import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemVo;
import com.jct.mes_new.config.common.MessageUtil;
import com.jct.mes_new.config.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    @PostMapping("/saveContractInfo")
    public  ResponseEntity<ApiResponse<Map<String, String>>> saveContractInfo(@RequestPart("contractInfo") String contractInfoJson,
                                                            @RequestPart("itemList") String itemListJson,
                                                            @RequestPart(value = "attachFile" , required = false) List<MultipartFile> attachFileList ) throws Exception {

            ObjectMapper mapper = new ObjectMapper();
            ContractVo contractInfo = mapper.readValue(contractInfoJson, ContractVo.class);
            List<ContractItemVo> itemList = mapper.readValue(itemListJson, new TypeReference<List<ContractItemVo>>() {});
            String contractId  = contractService.saveContractInfo(contractInfo, itemList, attachFileList);

        return ResponseEntity.ok(
                ApiResponse.ok(messageUtil.get("success.created"), Map.of("contractId", contractId))
        );
    }


    @PostMapping("/updateContractInfo")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateContractInfo(@RequestPart("contractInfo") String contractInfoJson,
                                                @RequestPart("itemList") String itemListJson,
                                                @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
                                                @RequestPart(value = "deleteFiles", required = false) String deleteFilesJson,
                                                @RequestPart(value = "keptFiles", required = false) String keptFilesJson
                                                ) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            ContractSaveRequestVo vo = new ContractSaveRequestVo();
            vo.setContractInfo(mapper.readValue(contractInfoJson, ContractVo.class));
            vo.setItemList(mapper.readValue(itemListJson, new TypeReference<List<ContractItemVo>>() {}));
            vo.setNewFiles(newFiles != null ? newFiles : new ArrayList<>());

            List<FileVo> deleteFiles = new ArrayList<>();
            if (deleteFilesJson != null && !deleteFilesJson.isEmpty()) {
                deleteFiles = mapper.readValue(deleteFilesJson, new TypeReference<List<FileVo>>() {});
            }
            vo.setDeleteFiles(deleteFiles);
            vo.setKeptFiles(keptFilesJson != null ?
                    mapper.readValue(keptFilesJson, new TypeReference<List<FileVo>>() {}) :
                    new ArrayList<>());

        String contractId  = contractService.updateContractInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated"), Map.of("contractId", contractId)));
    }





}
