package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.CustomerMapper;
import com.jct.mes_new.biz.base.service.CustomerService;
import com.jct.mes_new.biz.base.vo.CustomerVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    public List<CustomerVo> getCustomerList(CustomerVo vo){
        return customerMapper.getCustomerList(vo);
    }

    public String getCheckCustomerCd(String customerCd) {
        String chkYn = String.valueOf('N');

        if ( customerMapper. getCheckCustomerCd(customerCd) > 0) {
            chkYn = "Y";
        }
        return chkYn;
    }

    public CustomerVo getCustomerInfo(String customerCd) {
        return customerMapper.getCustomerInfo(customerCd);
    }

    public String saveCustomerInfo(CustomerVo vo){
        String msg = "저장되었습니다.";

        if (  customerMapper.saveCustomerInfo(vo) <= 0 ) {
            msg = "저장에 실패했습니다.";
        }
        return msg;
    }
}
