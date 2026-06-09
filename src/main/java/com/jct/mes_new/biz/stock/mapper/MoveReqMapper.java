package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.proc.vo.ProcItemVo;
import com.jct.mes_new.biz.stock.vo.MoveItemVo;
import com.jct.mes_new.biz.stock.vo.MoveReqVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MoveReqMapper {
    
    int insertMoveReqMst(MoveReqVo mst);

    int insertProcItem(ProcItemVo procItemVo);

    void deleteMoveReqItemId(Long id);

    int insertMoveItem(MoveItemVo moveItemVo);

    int updateMoveItem(MoveItemVo moveItemVo);

    int updateMoveReqMst(MoveReqVo mst);
}
