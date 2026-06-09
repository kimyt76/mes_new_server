package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.stock.vo.MoveReqRequestVo;

public interface MoveReqService {
    String saveProcMoveReq(MoveReqRequestVo vo);

    String saveMoveReq(MoveReqRequestVo vo);
}
