package com.jct.mes_new.biz.mat.service.impl;

import com.jct.mes_new.biz.lab.vo.BomVo;
import com.jct.mes_new.biz.mat.mapper.MatCommonMapper;
import com.jct.mes_new.biz.mat.service.MatCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatCommonServiceImpl implements MatCommonService {

    private final MatCommonMapper matCommonMapper;

    public List<Map<String , Object>> getItemStockList(List<BomVo> mapList ,String itemTypeCd){
        return matCommonMapper.getItemStockList(mapList, itemTypeCd);
    }
    public List<BomVo> getItemsBomList(List<Map<String, Object>> mapList){
        return matCommonMapper.getItemsBomList(mapList);
    }

    public Map<String, Object> getRequiredAmount(List<Map<String, Object>> mapList){
        Map<String, Object> map = new HashMap<>();
        List<BomVo> itemBomList = matCommonMapper.getItemsBomList(mapList);

        map.put("itemBomList", itemBomList);
        String itemTypeCd = "M2";
        map.put("itemStockList", getItemStockList(itemBomList, itemTypeCd));

        return map;
    }


}
