package com.jct.mes_new.biz.common.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BarCodeVo {
    private String menuType;
    private List<Map<String,Object>> barcodeList;
}
