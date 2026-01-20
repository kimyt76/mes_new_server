package com.jct.mes_new.biz.purchase.service.impl;

import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.mapper.PurchaseOrderMapper;
import com.jct.mes_new.biz.purchase.service.PurchaseOrderService;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;


    public List<PurchaseOrderVo> getPurchaseOrderList(String id) {
        return purchaseOrderMapper.getPurchaseOrderList(id);
    }


}
