package com.jct.mes_new.biz.stock.service.impl;


import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.mapper.AdjustMapper;
import com.jct.mes_new.biz.stock.service.AdjustService;
import com.jct.mes_new.biz.stock.vo.AdjustItemVo;
import com.jct.mes_new.biz.stock.vo.AdjustRequestVo;
import com.jct.mes_new.biz.stock.vo.AdjustVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdjustServiceImpl implements AdjustService {

    private final TranMapper tranMapper;
    private final AdjustMapper adjustMapper;

    public List<AdjustVo> getAdjustList(AdjustVo vo){
        return adjustMapper.getAdjustList(vo);
    }

    public AdjustRequestVo getAdjustInfo(Long tranId){
        AdjustRequestVo vo = new AdjustRequestVo();

        vo.setAdjustInfo(adjustMapper.getAdjustMst(tranId));
        vo.setAdjustItemList(adjustMapper.getAdjustItemList(tranId));

        return vo;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveAdjust(AdjustRequestVo vo){
        String userId = UserUtil.getUserId();
        AdjustVo mst = vo.getAdjustInfo();
        List<AdjustItemVo> adjustItemList = vo.getAdjustItemList();
        List<Long> deleteTranItemIds = vo.getDeleteTranItemIds();

        if (deleteTranItemIds != null && !deleteTranItemIds.isEmpty() ) {
            for(Long tranItemId : deleteTranItemIds){
                tranMapper.deleteTranItem(tranItemId);
            }
        }

        if (mst.getTranId() == null) {
            mst.setUserId(userId);
            if ( adjustMapper.insertAdjustMst(mst) <= 0 ) {
                throw new BusinessException(ErrorCode.CREATED);
            }
            for(AdjustItemVo adjustItem : adjustItemList){
                adjustItem.setTranId(mst.getTranId());
                adjustItem.setUserId(userId);
                if(adjustMapper.insertAdjustItemList(adjustItem) <= 0){
                    throw new BusinessException(ErrorCode.CREATED);
                }
            }
        }else{
            for(AdjustItemVo adjustItem : adjustItemList){
                adjustItem.setUserId(userId);
                if ( adjustItem.getTranItemId() == null ) {
                    if(adjustMapper.insertAdjustItemList(adjustItem) <= 0){
                        throw new BusinessException(ErrorCode.CREATED);
                    }
                }else{
                    if(adjustMapper.updateAdjustItemList(adjustItem) <= 0){
                        throw new BusinessException(ErrorCode.UPDATED);
                    }
                }
            }
        }

        return "저장되었습니다.";
    }

    @Transactional(rollbackFor = Exception.class)
    public  String deleteAdjust(Long tranId){
        tranMapper.deleteAllTranItem(tranId);
        tranMapper.deleteTranMst(tranId);

        return "삭제되었습니다.";
    }

}
