package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.proc.vo.ProcItemVo;
import com.jct.mes_new.biz.stock.vo.MoveItemVo;
import com.jct.mes_new.biz.stock.vo.MoveStockProcMapVo;
import com.jct.mes_new.biz.stock.vo.MoveStockVo;
import com.jct.mes_new.biz.stock.vo.StockVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MoveReqMapper {
    
    int insertMoveReqMst(MoveStockVo mst);

    int insertProcItem(ProcItemVo procItemVo);

    void deleteMoveReqItemId(Long id);

    int insertMoveItem(MoveItemVo moveItemVo);

    int updateMoveItem(MoveItemVo moveItemVo);

    int updateMoveReqMst(MoveStockVo mst);

    List<MoveStockVo> getMoveReqList(MoveStockVo vo);

    /**
     * 자재이동요청 상세조회
     * @param moveStockId
     * @return
     */
    MoveStockVo getMoveReqMst(@Param("moveStockId") Long moveStockId);
    List<ProcItemVo> getMoveReqProcList(@Param("moveStockId") Long moveStockId);
    List<MoveItemVo> getMoveItemList(@Param("moveStockId") Long moveStockId);

    int getNextRegSeq(MoveStockVo vo);

    List<StockVo> getMoveReqStockList(StockVo vo);

    int insertMoveStockProcMap(MoveStockProcMapVo mapVo);

    int saveMoveReqComplete(MoveStockVo vo);
}
