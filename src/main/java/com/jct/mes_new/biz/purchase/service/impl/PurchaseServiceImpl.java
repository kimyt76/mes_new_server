package com.jct.mes_new.biz.purchase.service.impl;

import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.service.PurchaseService;
import com.jct.mes_new.biz.purchase.vo.PurchaseRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseMapper purchaseMapper;


    /**
     * 구매 조회
     * @param vo
     * @return
     */
    public List<PurchaseVo.searchPurchaseListVo> getPurchaseList(PurchaseVo vo){
        return purchaseMapper.getPurchaseList(vo);
    }

    /**
     * 구매상세조회
     * @param purId
     * @return
     */
    public PurchaseRequestVo getPurchaseInfo(Long purId){
        PurchaseRequestVo vo = new PurchaseRequestVo();

        vo.setPurchaseInfo(purchaseMapper.getPurchaseInfo(purId));
        vo.setPurchaseItemList(purchaseMapper.getPurchaseItemList(purId));
        return vo;
    }

    /**
     * 구매 저장
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String savePurchaseInfo(PurchaseRequestVo vo){
        String msg = "저장되었습니다.";
        String userId = UserUtil.getUserId();
        if(vo.getPurchaseInfo() != null){
            vo.getPurchaseInfo().setUserId(userId);
            if(purchaseMapper.savePurchaseMst(vo.getPurchaseInfo()) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
            Long purId = vo.getPurchaseInfo().getPurId();

            for( PurchaseVo.PurchaseListVo item : vo.getPurchaseItemList()){
                item.setPurId(purId);
                item.setUserId(userId);
            }
            if(purchaseMapper.savePurchaseItemList(vo.getPurchaseItemList()) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return msg;
    }

    /**
     * 구매 수정
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String updatePurchaseInfo(PurchaseRequestVo vo){
        String msg = "수정되었습니다.";
            if(vo.getPurchaseInfo() != null){
                if(purchaseMapper.updatePurchaseMst(vo.getPurchaseInfo()) <= 0 ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
                purchaseMapper.deletePurchaseItemList(vo.getPurchaseInfo().getPurId());

                if(purchaseMapper.updatePurchaseItemList(vo.getPurchaseItemList()) <= 0 ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        return msg;
    }

    /**
     *구매 삭제
     * @param purId
     * @return
     */
    public String deletePurchase(Long purId){
        String msg="삭제되었습니다.";

        purchaseMapper.deletePurMst(purId);
        purchaseMapper.deletePurchaseItemList(purId);

        return msg;
    }


}
