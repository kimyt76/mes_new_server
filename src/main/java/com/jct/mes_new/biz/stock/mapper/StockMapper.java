package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.stock.vo.ItemUseVo;
import com.jct.mes_new.biz.stock.vo.StockVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StockMapper {


    /**
     * 재고조회 (품목)
     * @param vo
     * @return
     */
    List<StockVo> getStockItemList(StockVo vo);

    /**
     * 재고조회 (품목)
     * @param vo
     * @return
     */
    List<StockVo> getStockTestList(StockVo vo);

    List<Map<String, Object>> getStockList(Map<String, Object> param);


    /**
     * 품목별 사용량
     * @param vo
     * @return
     */
    List<ItemUseVo> getItemUseList(ItemUseVo vo);
}
