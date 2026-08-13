package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.mapper.RealStockMapper;
import com.jct.mes_new.biz.stock.service.RealStockService;
import com.jct.mes_new.biz.stock.vo.RealStockItemVo;
import com.jct.mes_new.biz.stock.vo.RealStockRequestVo;
import com.jct.mes_new.biz.stock.vo.RealStockVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RealStockServiceImpl implements RealStockService {

    private final RealStockMapper realStockMapper;
    private final TranMapper tranMapper;


    public List<RealStockVo> getRealStockList(RealStockVo vo){
        return realStockMapper.getRealStockList(vo);
    }

    public List<RealStockItemVo> getRealStockItemList(Long realStockMstId){
        int stockCnt = realStockMapper.realStockItemCnt(realStockMstId);

        if ( stockCnt > 0  ) {
            return realStockMapper.getRealStockItemList(realStockMstId);
        }else{
            //마스터를 조회
            RealStockVo mst = realStockMapper.getRealStockMst(realStockMstId);
            return realStockMapper.getDocStockItemList(mst);
        }
    }

    public List<TranItemVo> getInvTranItemList(TranItemVo vo){
        return realStockMapper.getInvTranItemList(vo);
    }

    public Long saveRealStock(RealStockVo vo) {
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if (realStockMapper.saveRealStock(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return vo.getRealStockMstId();
    }

    public Long saveRealStockItemList(RealStockRequestVo vo){
        String userId = UserUtil.getUserId();

        RealStockVo mst = vo.getRealStock();
        List<RealStockItemVo> stockItemList = vo.getRealStockItemList();

        for (RealStockItemVo stockItem : stockItemList) {
            stockItem.setUserId(userId);
            stockItem.setRealStockMstId(mst.getRealStockMstId());

            if ( stockItem.getRealStockItemId() == null ){
                if( realStockMapper.insertRealStockItem(stockItem) <= 0  ){
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                if( realStockMapper.updateRealStockItem(stockItem) <= 0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return mst.getRealStockMstId();
    }


    @Transactional(rollbackFor = BusinessException.class)
    public String saveRealStockComplete(Long realStockMstId){
        String userId = UserUtil.getUserId();

        RealStockVo mst = realStockMapper.getRealStockMst(realStockMstId);
        List<RealStockItemVo> stockItemList = realStockMapper.getRealStockItemList(realStockMstId);

        //재고 테이블에 재고 데이터 등록
        //재고 마스터
        TranVo tranVo = new TranVo();

        tranVo.setUserId(userId);
        tranVo.setTranDate(mst.getRealStockDate());
        tranVo.setTranTypeCd("S");
        tranVo.setSrcStorageCd(mst.getStorageCd());
        tranVo.setAreaCd(mst.getAreaCd());
        tranVo.setManagerId(mst.getManagerId());
        tranVo.setTranStatus("C");

        // 반드시 INSERT 먼저
        if (tranMapper.insertTranMst(tranVo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        // selectKey / generatedKeys에 의해 채워져 있어야 함
        Long tranId = tranVo.getTranId();

        if (tranId == null) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //재고 품목
        for (RealStockItemVo stockItem : stockItemList) {
            // 차이가 없는 품목은 조정거래를 만들 필요 없음
            if (stockItem.getDiffStockQty() == null || stockItem.getDiffStockQty().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            TranItemVo tranItemVo = new TranItemVo();

            tranItemVo.setTranId(tranId);
            tranItemVo.setItemTypeCd(mst.getItemTypeCd());
            tranItemVo.setItemCd(stockItem.getItemCd());
            tranItemVo.setItemName(stockItem.getItemName());
            tranItemVo.setTestNo(stockItem.getTestNo());
            tranItemVo.setQty(stockItem.getDiffStockQty());
            tranItemVo.setInYn("Y");
            tranItemVo.setUserId(userId);

            if (tranMapper.insertTranItem(tranItemVo) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        mst.setUserId(userId);
        mst.setTranId(tranVo.getTranId());
        mst.setEndYn("Y");
        //마스터 완료 처리
        if (realStockMapper.updateRealStockComplete(mst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return "실사마감이 완료되었습니다.";
    }






}
