package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.stock.vo.RealStockItemVo;
import com.jct.mes_new.biz.stock.vo.RealStockRequestVo;
import com.jct.mes_new.biz.stock.vo.RealStockVo;

import java.util.List;

public interface RealStockService {
    List<RealStockVo> getRealStockList(RealStockVo vo);

    List<RealStockItemVo> getRealStockItemList(Long realStockMstId);

    Long saveRealStock(RealStockVo vo);

    List<TranItemVo> getInvTranItemList(TranItemVo vo);

    Long saveRealStockItemList(RealStockRequestVo vo);

    String saveRealStockComplete(Long realStockMstId);

    String deleteRealStock(Long realStockMstId);
}
