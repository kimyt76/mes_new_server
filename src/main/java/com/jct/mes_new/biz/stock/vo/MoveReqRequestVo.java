package com.jct.mes_new.biz.stock.vo;

import com.jct.mes_new.biz.proc.vo.ProcItemVo;
import lombok.Data;

import java.util.List;

@Data
public class MoveReqRequestVo {

    //자재이동요청 마스터
    private MoveReqVo moveReqInfo;
    //공정 요청 품목 리스트
    private List<ProcItemVo> procItemList;
    //재고 리스트
    private List<StockVo> stockItemList;
    //실제이동 품목리스트
    private List<MoveItemVo> moveItemList;
    //삭제 품목
    private List<Long> deleteMoveReqItemIds;

}
