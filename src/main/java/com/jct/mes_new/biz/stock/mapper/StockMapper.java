package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.stock.vo.ItemUseVo;
import com.jct.mes_new.biz.stock.vo.StockVo;
import com.jct.mes_new.biz.stock.vo.TranLedgerVo;
import com.jct.mes_new.biz.stock.vo.UseByVo;
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

    /**
     * 사용기한 (원재료)
     * @param vo
     * @return
     */
    List<UseByVo> getUseByM1List(UseByVo vo);

    /**
     * 사용기한 (부재료)
     * @param vo
     * @return
     */
    List<UseByVo> getUseByM2List(UseByVo vo);


    List<TranLedgerVo> getTranLedger(TranLedgerVo vo);
}
