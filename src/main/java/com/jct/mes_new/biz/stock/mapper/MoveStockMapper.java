package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.stock.vo.MoveItemVo;
import com.jct.mes_new.biz.stock.vo.MoveStockVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MoveStockMapper {

    List<MoveStockVo> getMoveStockList(MoveStockVo vo);

    /**
     * 자재이동 상세
     * @param moveStockId
     * @return
     */
    MoveStockVo getMoveStockMst(Long moveStockId);
    List<MoveItemVo> getMoveStockItemList(Long moveStockId);

    /**
     * 자재이동 저장
     * @param mst
     * @return
     */
    int insertMoveStockMst(MoveStockVo mst);
    int insertMoveStockItem(MoveItemVo moveItemVo);

    int updateMoveStockMst(MoveStockVo vo);
    int updateMoveStockItem(MoveItemVo moveItemVo);

    int updateTranId(@Param("moveStockId") Long moveStockId, @Param("tranId") Long tranId, @Param("userId") String userId);


    int updateMoveStockConfirm(MoveStockVo vo);
}
