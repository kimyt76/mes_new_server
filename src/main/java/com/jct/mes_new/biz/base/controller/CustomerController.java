package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.CustomerService;
import com.jct.mes_new.biz.base.vo.CustomerVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final MessageUtil messageUtil;

    @PostMapping("/getCustomerList")
    public List<CustomerVo> getCustomerList(@RequestBody CustomerVo vo){
        return customerService.getCustomerList(vo);
    }

    @GetMapping("/getCheckCustomerCd/{id}")
    public String getCheckCustomerCd(@PathVariable("id") String customerCd){
        return customerService.getCheckCustomerCd(customerCd);
    }

    @GetMapping("/getCustomerInfo/{id}")
    public CustomerVo getCustomerInfo(@PathVariable("id") String customerCd){
        return customerService.getCustomerInfo(customerCd);
    }

    @PostMapping("/saveCustomerInfo")
    public ResponseEntity<ApiResponse<Void>> saveCustomerInfo(@RequestBody CustomerVo vo){
        String result = customerService.saveCustomerInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }
}
