package com.jct.mes_new.biz.mat.mapper;

import com.jct.mes_new.biz.lab.vo.BomVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MatCommonMapper {
    List<Map<String, Object>> getItemStockList(@Param("list") List<BomVo> mapList, @Param("itemTypeCd") String itemTypeCd);

    List<BomVo> getItemsBomList(@Param("list") List<Map<String, Object>> mapList);
}
