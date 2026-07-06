package com.jct.mes_new.biz.purchase.service;

import com.jct.mes_new.biz.purchase.vo.TranRequestVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;

import java.util.List;

public interface TranService {

    Long saveTranInfo(TranRequestVo vo);
    Long updateTranInfo(TranRequestVo vo);

    void deleteTranInfo(Long purId);
    TranRequestVo getTranInfo(Long tranId);


    /**
     * 자재불출
     * @param vo
     * @return
     */
    List<TranVo> getItemOutList(TranVo vo);
    TranRequestVo getItemOutInfo(Long tranId);
    String saveItemOutInfo(TranRequestVo vo);

}
