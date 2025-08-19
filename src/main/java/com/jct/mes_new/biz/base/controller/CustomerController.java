package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.CustomerService;
import com.jct.mes_new.biz.base.vo.CustomerVo;
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
    public ResponseEntity<?> saveCustomerInfo(@RequestBody CustomerVo vo){
        try {
            String result = customerService.saveCustomerInfo(vo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }
}
