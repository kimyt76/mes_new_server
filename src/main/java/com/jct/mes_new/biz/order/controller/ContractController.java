package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.service.ContractService;
import com.jct.mes_new.biz.order.vo.ContractSaveRequestVo;
import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemListVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/getContractList")
    public List<ContractVo> getContractList(@RequestBody ContractVo contractVo) {
        return contractService.getContractList(contractVo);
    }

    @GetMapping("/getContractInfo/{id}")
    public Map<String, Object> getContractInfo(@PathVariable("id") String contractId) {
        return contractService.getContractInfo(contractId);
    }

    @PostMapping("/saveContractInfo")
    public ResponseEntity<?> saveContractInfo(@RequestPart("contractInfo") String contractInfoJson,
                                              @RequestPart("itemList") String itemListJson,
                                              @RequestPart(value = "attachFile" , required = false) List<MultipartFile> attachFileList ) throws Exception {

        try {
            ObjectMapper mapper = new ObjectMapper();

            ContractVo contractInfo = mapper.readValue(contractInfoJson, ContractVo.class);
            List<ContractItemListVo> itemList = mapper.readValue(itemListJson, new TypeReference<List<ContractItemListVo>>() {});
            String result = contractService.saveContractInfo(contractInfo, itemList, attachFileList);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }


    @PostMapping("/updateContractInfo")
    public ResponseEntity<?> updateContractInfo(@RequestPart("contractInfo") String contractInfoJson,
                                                @RequestPart("itemList") String itemListJson,
                                                @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
                                                @RequestPart(value = "deleteFiles", required = false) String deleteFilesJson,
                                                @RequestPart(value = "keptFiles", required = false) String keptFilesJson
                                                ) throws Exception {

        try {
            ObjectMapper mapper = new ObjectMapper();

            ContractSaveRequestVo vo = new ContractSaveRequestVo();

            vo.setContractInfo(mapper.readValue(contractInfoJson, ContractVo.class));
            vo.setItemList(mapper.readValue(itemListJson, new TypeReference<List<ContractItemListVo>>() {}));
            vo.setNewFiles(newFiles != null ? newFiles : new ArrayList<>());

            List<FileVo> deleteFiles = new ArrayList<>();
            if (deleteFilesJson != null && !deleteFilesJson.isEmpty()) {
                deleteFiles = mapper.readValue(deleteFilesJson, new TypeReference<List<FileVo>>() {});
            }
            vo.setDeleteFiles(deleteFiles);
            vo.setKeptFiles(keptFilesJson != null ?
                    mapper.readValue(keptFilesJson, new TypeReference<List<FileVo>>() {}) :
                    new ArrayList<>());

            String result = contractService.updateContractInfo(vo);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }
}
