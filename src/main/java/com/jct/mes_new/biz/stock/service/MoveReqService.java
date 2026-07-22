package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.stock.vo.*;

import java.util.List;

public interface MoveReqService {

    int getNextRegSeq(MoveStockVo vo);

    List<MoveStockVo> getMoveReqList(MoveStockVo vo);

    MoveStockRequestVo getMoveReqInfo(Long moveStockId);

    String saveProcMoveReq(MoveStockRequestVo vo);

    List<StockVo> getMoveReqStockList(StockVo vo);

    String saveMoveItem(MoveStockRequestVo vo);

    String saveMoveReqComplete(MoveStockVo vo);
}
