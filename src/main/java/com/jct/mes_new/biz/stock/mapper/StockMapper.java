package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.stock.vo.StockVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StockMapper {
    List<Map<String, Object>> getStockList(Map<String, Object> param);
}
