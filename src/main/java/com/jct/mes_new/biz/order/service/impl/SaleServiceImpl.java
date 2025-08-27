package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.order.mapper.ContractMapper;
import com.jct.mes_new.biz.order.mapper.SaleMapper;
import com.jct.mes_new.biz.order.service.SaleService;
import com.jct.mes_new.biz.order.vo.SaleItemListVo;
import com.jct.mes_new.biz.order.vo.SaleVo;
import com.jct.mes_new.biz.order.vo.ContractItemListVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;
    private final ContractMapper contractMapper;


    public List<SaleVo> getSaleList(SaleVo saleVo) {
        return saleMapper.getSaleList(saleVo);
    }

    public Map<String, Object> getSaleInfo(String saleId){
        Map<String, Object> map = new HashMap<>();

        SaleVo saleInfo = saleMapper.getSaleInfo(saleId);
        List<SaleItemListVo> itemList = saleMapper.getSaleItemList(saleId);

        map.put("saleInfo", saleInfo);
        map.put("itemList", itemList);

        return map;
    }

    public String saveSaleInfo(SaleVo saleInfo, List<SaleItemListVo> itemList){
        Snowflake snowflake = new Snowflake(1, 1);
        String msg = "저장되었습니다.";

        try{
            /* 판매id 채번*/
            if ( saleInfo.getSaleId() == null || saleInfo.getSaleId().isEmpty()   ){
                saleInfo.setSaleId(CommonUtil.generateUUID());
            }

            if ( saleMapper.saveSaleInfo(saleInfo) <= 0 ) {
                throw new Exception("주문서 저장에 실패했습니다.");
            }else{
                saleMapper.deleteSaleItemList(saleInfo.getSaleId());

                for (SaleItemListVo item : itemList) {
                    item.setSaleItemId(String.valueOf(snowflake.nextId()));
                    item.setSaleId(saleInfo.getSaleId());
                    item.setUserId(saleInfo.getUserId());

                    if ( saleMapper.saveItemList(item)  <= 0 ){
                        throw new Exception("품목리스트 저장 실패");
                    }
                }
            }
        }catch (Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return msg;
    }

    public List<ContractItemListVo> getContractItemList(String ids){
        List<String> contractIdList = Arrays.asList(ids.split(","));
        return contractMapper.getContractItemList(contractIdList);
    }

    public List<SaleItemListVo> getSaleItemList(String saleId){
        return saleMapper.getSaleItemList(saleId);
    }

}
