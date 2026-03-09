package com.jct.mes_new.biz.purchase.service;

import com.jct.mes_new.biz.purchase.vo.PurchaseRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseVo;

import java.util.List;

public interface PurchaseService {

    List<PurchaseVo.searchPurchaseListVo> getPurchaseList(PurchaseVo vo);

    PurchaseRequestVo getPurchaseInfo(Long purId);

    String savePurchaseInfo(PurchaseRequestVo vo);

    String updatePurchaseInfo(PurchaseRequestVo vo);

    String deletePurchase(Long purId);
}
