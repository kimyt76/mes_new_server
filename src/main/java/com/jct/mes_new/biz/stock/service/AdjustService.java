package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.vo.AdjustRequestVo;
import com.jct.mes_new.biz.stock.vo.AdjustVo;

import java.util.List;

public interface AdjustService {
    List<AdjustVo> getAdjustList(AdjustVo vo);

    AdjustRequestVo getAdjustInfo(Long tranId);

    String saveAdjust(AdjustRequestVo vo);
}
