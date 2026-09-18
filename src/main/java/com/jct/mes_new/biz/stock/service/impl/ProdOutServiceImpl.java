package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranRequestVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.mapper.ProdOutMapper;
import com.jct.mes_new.biz.stock.service.ProdOutService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProdOutServiceImpl implements ProdOutService {

    private final ProdOutMapper prodOutMapper;
    private final TranMapper tranMapper;

    public List<ProdOutVo> getProdOutList(ProdOutVo vo){
        return prodOutMapper.getProdOutList(vo);
    }

    public ProdOutRequestVo getProdOutInfo(Long tranId){
        ProdOutRequestVo vo = new ProdOutRequestVo();

        vo.setProdOutInfo(prodOutMapper.getProdOutMst(tranId));
        vo.setProdOutItemList(prodOutMapper.getProdOutItemList(tranId));

        return vo;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveProdOut(ProdOutRequestVo vo){
        String userId = UserUtil.getUserId();
        ProdOutVo mst = vo.getProdOutInfo();
        List<ProdOutItemVo> prodOutItemList = vo.getProdOutItemList();

        if (mst.getTranId() == null) {
            mst.setUserId(userId);
            if ( prodOutMapper.insertProdOutMst(mst) <= 0 ) {
                throw new BusinessException(ErrorCode.CREATED);
            }

            for(ProdOutItemVo prodOutItem : prodOutItemList){
                prodOutItem.setTranId(mst.getTranId());
                prodOutItem.setUserId(userId);

                if(prodOutMapper.insertProdOutItemList(prodOutItem) <= 0){
                    throw new BusinessException(ErrorCode.CREATED);
                }
            }
        }else{
            for(ProdOutItemVo prodOutItem : prodOutItemList){
                prodOutItem.setUserId(userId);

                if(prodOutMapper.updateProdOutItemList(prodOutItem) <= 0){
                    throw new BusinessException(ErrorCode.UPDATED);
                }
            }
        }
        return "저장되었습니다.";
    }



}
