package com.jct.mes_new.biz.order.controller;

import com.jct.mes_new.biz.order.service.ContractService;
import com.jct.mes_new.biz.order.vo.*;
import com.jct.mes_new.biz.stock.vo.StockVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @PostMapping("/getOrderPlanList")
    public List<OrderPlanVo> getOrderPlanList(@RequestBody OrderPlanVo vo) {
        return contractService.getOrderPlanList(vo);
    }

    @PostMapping("/getOrderPlanType")
    public List<OrderPlanTypeVo> getOrderPlanType(@RequestBody OrderPlanTypeVo vo) {
        return contractService.getOrderPlanType(vo);
    }
    @PostMapping("/getMatWorkOrder")
    public List<WorkOrderInfoVo> getMatWorkOrder(@RequestBody WorkOrderInfoVo vo) {
        return contractService.getMatWorkOrder(vo);
    }
    @PostMapping("/getRequiredQuantityList")
    public Map<String, Object> getRequiredQuantityList(@RequestBody OrderPlanVo vo) {
        return contractService.getRequiredQuantityList(vo);
    }

    @PostMapping("/saveOrderPlan")
    public ResponseEntity<ApiResponse<Long>> saveOrderPlan(@RequestBody OrderPlanTypeRequestVo vo) {
        String result = contractService.saveOrderPlan(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("/updateOrderPlanYn")
    public ResponseEntity<ApiResponse<Long>> updateOrderPlanYn(@RequestBody OrderPlanVo vo) {
        String result = contractService.updateOrderPlanYn(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }



}
