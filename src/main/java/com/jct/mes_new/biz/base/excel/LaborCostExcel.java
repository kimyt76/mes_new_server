package com.jct.mes_new.biz.base.excel;


import com.jct.mes_new.biz.base.mapper.LaborCostsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LaborCostExcel {

    private final LaborCostsMapper laborCostsMapper;
}
