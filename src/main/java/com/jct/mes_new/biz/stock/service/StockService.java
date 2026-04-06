package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.stock.vo.StockHistResponseVo;
import com.jct.mes_new.biz.stock.vo.StockVo;

import java.util.List;

public interface StockService {
    StockHistResponseVo getStockItemHistList(StockVo vo);
}
