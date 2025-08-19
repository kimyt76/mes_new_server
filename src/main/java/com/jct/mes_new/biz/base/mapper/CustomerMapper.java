package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.CustomerVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {

    List<CustomerVo> getCustomerList(CustomerVo vo);

    CustomerVo getCustomerInfo(String customerCd);

    int getCheckCustomerCd(String customerCd);

    int saveCustomerInfo(CustomerVo vo);
}
