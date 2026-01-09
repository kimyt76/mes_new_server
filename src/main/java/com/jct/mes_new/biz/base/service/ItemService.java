package com.jct.mes_new.biz.base.service;

import com.jct.mes_new.biz.base.vo.ItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface ItemService {
    public List<ItemVo> getProdLList();

    public List<ItemVo> getProdMList(String id);

    public String saveItemInfo(ItemVo itemVo);

    List<ItemVo> getItemList(ItemVo itemVo);

    ItemVo getItemInfo(String itemCd);

    String getItemCdCheck(String itemCd);

    String updateItemInfo(ItemVo itemVo);

    String saveItemDetailInfo(ItemVo itemVo);

    void updatePriceInfoMap(Map<String, Object> paramMap);

    String saveItemAddInfo(ItemVo itemVo);
}
