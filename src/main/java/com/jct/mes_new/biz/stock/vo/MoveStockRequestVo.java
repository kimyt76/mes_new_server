package com.jct.mes_new.biz.stock.vo;

import com.jct.mes_new.biz.proc.vo.ProcItemVo;
import lombok.Data;

import java.util.List;

@Data
public class MoveStockRequestVo {

    //자재이동 마스터
    private MoveStockVo moveStockInfo;
    //실제이동 품목리스트
    private List<MoveItemVo> moveItemList;
    //공정 요청 품목 리스트
    private List<ProcItemVo> procItemList;
    //삭제 품목
    private List<Long> deleteMoveStockItemIds;
    //재고 리스트
    private List<StockVo> stockItemList;

}
