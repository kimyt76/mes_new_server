package com.jct.mes_new.biz.mat.service;


import com.jct.mes_new.biz.lab.vo.BomVo;

import java.util.List;
import java.util.Map;

public interface MatCommonService {

    List<Map<String , Object>> getItemStockList(List<BomVo> mapList, String itemTypeCd);

    Map<String, Object> getRequiredAmount(List<Map<String, Object>> mapList);

    List<BomVo> getItemsBomList(List<Map<String, Object>> mapList);
}
