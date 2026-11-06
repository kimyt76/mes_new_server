package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.purchase.vo.TranRequestVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.vo.MoveStockRequestVo;
import com.jct.mes_new.biz.stock.vo.MoveStockVo;
import com.jct.mes_new.biz.stock.vo.ProdOutRequestVo;
import com.jct.mes_new.biz.stock.vo.ProdOutVo;

import java.util.List;

public interface ProdOutService {

    /**
     * 제품출고
     * @param vo
     * @return
     */
    List<ProdOutVo> getProdOutList(ProdOutVo vo);
    ProdOutRequestVo getProdOutInfo(Long tranId);
    String saveProdOut(ProdOutRequestVo vo);






}
