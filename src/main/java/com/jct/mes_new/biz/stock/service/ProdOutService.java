package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.stock.vo.ProdOutRequestVo;
import com.jct.mes_new.biz.stock.vo.ProdOutVo;

import java.util.List;

public interface ProdOutService {
    List<ProdOutVo> getProdOutList(ProdOutVo vo);

    ProdOutRequestVo getProdOutInfo(Long tranId);

    String saveProdOut(ProdOutRequestVo vo);
}
