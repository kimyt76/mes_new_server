package com.jct.mes_new.biz.lab.service;


import com.jct.mes_new.biz.lab.vo.BomProcVo;
import com.jct.mes_new.biz.lab.vo.BomRecipeVo;
import com.jct.mes_new.biz.lab.vo.BomRequestVo;
import com.jct.mes_new.biz.lab.vo.BomVo;

import java.util.List;
import java.util.Map;

public interface BomService {
    List<BomVo> getBomList(BomVo bomVo);

    BomRequestVo getBomInfo(String bomId);

    BomVo getProductTypeInfo(String prodCd);

    String saveBomInfo(BomVo bomInfo, List<BomRecipeVo> bomRecipeList, List<BomProcVo> bomProcList);

    String saveBomVerInfo(BomVo bomInfo, List<BomRecipeVo> bomRecipeList, List<BomProcVo> bomProcList);

    List<BomRecipeVo> getItemBomList(String itemCd);

    List<BomVo> getBomMatList(BomVo bomVo);

    Map<String, Object> getItemsBomList(Map<String, String>  map);
}
