package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.stock.vo.*;

import java.util.List;
import java.util.Map;

public interface StockService {

    Map<String, Object> getStockItemList(StockVo vo);

    StockHistResponseVo getStockItemHistList(StockVo vo);


    List<ItemUseVo> getItemUseList(ItemUseVo vo);

    List<UseByVo> getUseByM2List(UseByVo vo);

    Map<String, Object> getUseByM1List(UseByVo vo);

    List<TranLedgerVo> getTranLedger(TranLedgerVo vo);

    List<TestNoProdVo> getTestUseList(TestNoProdVo vo);
}
