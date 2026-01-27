package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.base.service.ItemService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.base.vo.PriceHistoryVo;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public String saveItemInfo(ItemVo itemVo) {
        String gb = itemVo.getGb();
        String baseItemCd = itemVo.getItemCd(); // 기준 제품코드
        String itemTeypCd = itemVo.getItemTypeCd(); // 기준 제품코드

        List<Map<String, String>> itemInfos = makeItemCds(gb, baseItemCd, itemTeypCd);

        //Map 리스트에서 itemCd만 뽑아 List<String>으로 만들기
        List<String> itemCdList = itemInfos.stream()
                .map(m -> m.get("itemCd"))
                .filter(Objects::nonNull)
                .toList();

        //다건 중복 체크: 한번에 조회
        List<String> existing = itemMapper.getItemCdCheckList(itemCdList);

        if (existing != null && !existing.isEmpty()) {
            // existing이 바로 중복된 코드 목록
            // 예외 던지거나 메시지 처리
            throw new BusinessException(ErrorCode.DUPLICATE,
                    "이미 존재하는 코드가 있습니다: " + String.join(", ", existing),
                    Map.of("duplicateCodes", existing));
        }

        try{
            for (Map<String, String > itemInfo : itemInfos) {
                itemVo.setItemCd(itemInfo.get("itemCd"));
                itemVo.setItemTypeCd(itemInfo.get("itemTypeCd"));
                itemMapper.saveItemInfo(itemVo);
                itemMapper.insertItemDetial(itemVo.getItemCd(), itemVo.getUserId());
            }
        } catch (Exception e) {
            log.error("saveItemInfo error. baseItemCd={}, gb={}", baseItemCd, gb, e);
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        return "저장되었습니다.";
    }

    private List<Map<String, String>> makeItemCds(String gb, String baseItemCd, String itemTeypCd) {
        if ("O".equals(gb)) {
            return List.of(Map.of("itemCd", baseItemCd, "itemTypeCd", "M4", "itemTypeName","[제품]"));
        }

        if ("U".equals(gb) ) {
            return List.of(Map.of("itemCd", baseItemCd, "itemTypeCd", itemTeypCd, "itemTypeName","[제품]"));
        }

        if ("A".equals(gb)) {
            return List.of(
                    Map.of("itemCd", baseItemCd, "itemTypeCd", "M4", "itemTypeName","[제품]"),
                    Map.of("itemCd", baseItemCd + "1I", "itemTypeCd", "M3", "itemTypeName","[반제품]"),
                    Map.of("itemCd", baseItemCd + "AF", "itemTypeCd", "M0", "itemTypeName","[완제품]"),
                    Map.of("itemCd", baseItemCd + "B", "itemTypeCd", "M5", "itemTypeName","[벌크제품]"),
                    Map.of("itemCd", baseItemCd + "AP", "itemTypeCd", "M6", "itemTypeName","[포장품]")
            );
        }
        return List.of(Map.of("itemCd", baseItemCd, "itemTypeCd", "M4", "itemTypeName","[제품]"));
    }

    public String saveItemAddInfo(ItemVo itemVo) {
        try{
            itemMapper.saveItemInfo(itemVo);
            itemMapper.insertItemDetial(itemVo.getItemCd(), itemVo.getUserId());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
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
