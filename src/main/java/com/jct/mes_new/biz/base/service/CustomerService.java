package com.jct.mes_new.biz.base.service;

import com.jct.mes_new.biz.base.vo.CustomerVo;

import java.util.List;

public interface CustomerService {
    List<CustomerVo> getCustomerList(CustomerVo vo);

    CustomerVo getCustomerInfo(String customerCd);

    String getCheckCustomerCd(String customerCd);

    String saveCustomerInfo(CustomerVo vo);
}
