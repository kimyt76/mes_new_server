package com.jct.mes_new.biz.purchase.service;

import com.jct.mes_new.biz.purchase.vo.TranRequestVo;

public interface TranService {

    Long saveTranInfo(TranRequestVo vo);
    Long updateTranInfo(TranRequestVo vo);

    void deleteTranInfo(Long purId);
}
