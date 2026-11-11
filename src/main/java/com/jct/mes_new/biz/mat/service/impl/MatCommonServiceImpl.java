package com.jct.mes_new.biz.mat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public List<Map<String, Object>> getItemStockList(
            List<BomVo> mapList, String itemTypeCd) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String bomListJson = objectMapper.writeValueAsString(mapList);

            return matCommonMapper.getItemStockList(bomListJson);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("BOM 목록 JSON 변환 오류", e);
        }
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
