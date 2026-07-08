package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.base.service.ItemService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.base.vo.PriceHistoryVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * 품목코드 신규 생성
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String saveItemInfo(ItemVo vo) {
        //신규는 제품과 원재료 밖에 없음
        String gb = vo.getGb();
        String baseItemCd = vo.getItemCd(); // 기준 제품코드
        String baseName = vo.getItemName(); // 기준 제품코드
        String itemTypeCd = vo.getItemTypeCd(); // 기준 제품코드
        String userId = UserUtil.getUserId();

        vo.setUserId(userId);
        if ( "A".equals(vo.getGb())  ) {
            List<Map<String, String>> itemInfos = makeItemCds(gb, baseItemCd, itemTypeCd);

            for (Map<String, String > itemInfo : itemInfos) {
                vo.setItemCd(itemInfo.get("itemCd"));
                String itemCdCheck =  this.getItemCdCheck(itemInfo.get("itemCd"));

                if ("Y".equals(itemCdCheck)) {
                    throw new BusinessException("중복된 품목코드가 있습니다." + itemInfo.get("itemCd"));
                }

                vo.setItemTypeCd(itemInfo.get("itemTypeCd"));
                String itemName = itemInfo.get("itemTypeName")+baseName;
                vo.setItemName(itemName);

                insertItem(vo, userId);
            }
        }else {
            if ("O".equals(vo.getGb()) && "M4".equals(itemTypeCd)) {
                vo.setItemName("[제품] " + vo.getItemName());
            }

            insertItem(vo, userId);
        }
        return "저장되었습니다.";
    }

    private void insertItem(ItemVo vo, String userId) {
        if (itemMapper.insertItemMst(vo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        if (itemMapper.insertItemDetial(vo.getItemCd(), userId) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
    }

    private List<Map<String, String>> makeItemCds(String gb, String baseItemCd, String itemTypeCd) {
        if ("O".equals(gb)) {
            return List.of(Map.of("itemCd", baseItemCd, "itemTypeCd", "M4", "itemTypeName","[제품] "));
        }
        if ("U".equals(gb) ) {
            return List.of(Map.of("itemCd", baseItemCd, "itemTypeCd", itemTypeCd, "itemTypeName","[제품] "));
        }
        if ("A".equals(gb)) {
            return List.of(
                    Map.of("itemCd", baseItemCd, "itemTypeCd", "M4", "itemTypeName","[제품] "),
                    Map.of("itemCd", baseItemCd + "1I", "itemTypeCd", "M3", "itemTypeName","[반제품] "),
                    Map.of("itemCd", baseItemCd + "AF", "itemTypeCd", "M0", "itemTypeName","[완제품] "),
                    Map.of("itemCd", baseItemCd + "B", "itemTypeCd", "M5", "itemTypeName","[벌크제품] "),
                    Map.of("itemCd", baseItemCd + "AP", "itemTypeCd", "M6", "itemTypeName","[포장품] ")
            );
        }
        return List.of(Map.of("itemCd", baseItemCd, "itemTypeCd", "M4", "itemTypeName","[제품]"));
    }


    @Transactional(rollbackFor = BusinessException.class)
    public String saveItemAddInfo(ItemVo itemVo) {
        String userId = UserUtil.getUserId();
        this.insertItem(itemVo, userId );

        return "저장되었습니다.";
    }

    public String updateItemInfo(ItemVo itemVo){
        String msg ="저장되었습니다.";
        try{
            if ( itemMapper.updateItemInfo(itemVo) <= 0 ) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
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
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED );
        }
        return msg;
    }

    @Transactional(rollbackFor = BusinessException.class)
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
