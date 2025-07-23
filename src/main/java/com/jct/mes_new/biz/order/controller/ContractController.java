package com.jct.mes_new.biz.order.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.order.service.ContractService;
import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ItemListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/getNextSeq/{date}")
    public int getNextSeq(@PathVariable("date") String date) {
        return contractService.getNextSeq(date);
    }

    @PostMapping("/saveContractInfo")
    public ResponseEntity<?> saveContractInfo(@RequestPart("contractInfo") String contractInfoJson,
                                              @RequestPart("itemList") String itemListJson,
                                              @RequestPart(value = "attachFile" , required = false) List<MultipartFile> attachFileList ) throws Exception {

        try {
            ObjectMapper mapper = new ObjectMapper();

            ContractVo contractInfo = mapper.readValue(contractInfoJson, ContractVo.class);
            List<ItemListVo> itemList = mapper.readValue(itemListJson, new TypeReference<List<ItemListVo>>() {});

            String result = contractService.saveContractInfo(contractInfo, itemList, attachFileList);

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }


}
