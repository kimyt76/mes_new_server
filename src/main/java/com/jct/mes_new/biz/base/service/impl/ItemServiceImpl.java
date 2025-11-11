package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.base.service.ItemService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.base.vo.PriceHistoryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;


    public List<ItemVo> getProdLList(){
        return itemMapper.getProdLList();
    }

    public List<ItemVo> getProdMList(String id){
        return itemMapper.getProdMList(id);
    }

    public List<ItemVo> getItemList(ItemVo itemVo){
        return itemMapper.getItemList(itemVo);
    }

    public String saveItemInfo(ItemVo itemVo){
        String msg ="저장되었습니다.";
        try{
            if ( itemMapper.saveItemInfo(itemVo) <= 0 ) {
                throw new Exception("저장에 실패했습니다.");
            }
            itemMapper.insertItemDetial(itemVo.getItemCd(), itemVo.getUserId());
        } catch (Exception e) {
            throw new RuntimeException("저장 실패 : " + e.getMessage(), e );
        }
        return msg;
    }

    public String updateItemInfo(ItemVo itemVo){
        String msg ="저장되었습니다.";
        try{
            if ( itemMapper.updateItemInfo(itemVo) <= 0 ) {
                throw new Exception("저장에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("저장 실패 : " + e.getMessage(), e );
        }
        return msg;
    }

    public ItemVo getItemInfo(String itemCd){
        ItemVo item = itemMapper.getItemInfo(itemCd);
        //pricd history 조회 추가
        item.setPriceHistory(itemMapper.getItemPriceHistory(itemCd));
log.info("===================item==================== : " + item);
        return item;
    }

    public String getItemCdCheck(String itemCd){
        String chk = "N";

        if (  itemMapper.getItemCdCheck(itemCd) > 0 ){
            chk = "Y";
        }
        return chk;
    }

    public String saveItemDetailInfo(ItemVo itemVo){
        String msg ="저장되었습니다.";
        try{
            if ( itemMapper.saveItemDetailInfo(itemVo) <= 0 ) {
                throw new Exception("저장에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("저장 실패 : " + e.getMessage(), e );
        }
        return msg;
    }

    public void updatePriceInfoMap(Map<String, Object> paramMap){
        String itemCd = (String) paramMap.get("itemCd");
        String type = (String) paramMap.get("type");
        String userId = (String) paramMap.get("userId");

        BigDecimal inPrice = paramMap.get("inPrice") != null
                ? new BigDecimal(paramMap.get("inPrice").toString())
                : null;

        BigDecimal outPrice = paramMap.get("outPrice") != null
                ? new BigDecimal(paramMap.get("outPrice").toString())
                : null;

        // 입고 단가 변경
        if ((type.equals("I") || type.equals("A")) && inPrice != null ) {
            itemMapper.insertPriceHistory(itemCd, "I", inPrice, userId);
        }

        // 출고 단가 변경
        if ((type.equals("O") || type.equals("A")) && outPrice != null ) {
            BigDecimal oldOutPrice;
            itemMapper.insertPriceHistory(itemCd, "O",  outPrice, userId);
        }
        // tb_item 단가 업데이트
        itemMapper.updateItemPrice(itemCd, inPrice, outPrice, userId);
    }
}
