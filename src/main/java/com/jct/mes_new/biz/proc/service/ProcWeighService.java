package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.biz.proc.vo.WeighInfoVo;
import com.jct.mes_new.biz.proc.vo.WeighInvInfo;
import com.jct.mes_new.biz.stock.vo.TranLedgerVo;

import java.util.List;

public interface ProcWeighService {

    WeighInfoVo getWeighInfo(ProcWeighVo vo);

    Long saveWeighInfo(WeighInfoVo vo);

    String saveWeighList(WeighInvInfo vo);

    List<ProcWeighVo> getStockTestNoList(ProcWeighVo vo);

    Long completeWeight(ProcWeighVo vo);

    String startProcWeigh(ProcWeighVo vo);

    List<TranLedgerVo> getWeighCloseList(TranLedgerVo vo);

    List<TranLedgerVo> getItemCloseList(TranLedgerVo vo);

    byte[] downloadWeighProc(ProcWeighVo vo);
}
