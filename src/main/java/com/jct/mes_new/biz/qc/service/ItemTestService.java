package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.purchase.vo.PurchaseRequestVo;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;

import java.util.List;

public interface ItemTestService {


    int insertItemTestNo(List<ItemTestVo> itemTestNoList, Long tranId, String userId);

    List<ItemTestVo> getItemTestNoList(ItemTestVo vo);

    int updateItemTestNo(List<ItemTestVo> itemTestNoList, String userId);

    ItemTestVo getItemTestNoInfo(String testNo);

    String updateItemTestNoInfo(ItemTestVo vo);

    List<ItemTestVo> getItemTestNoInfoList(String testNo);
}
