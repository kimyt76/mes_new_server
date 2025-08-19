package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.base.service.ItemService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return itemMapper.getItemInfo(itemCd);
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

}
