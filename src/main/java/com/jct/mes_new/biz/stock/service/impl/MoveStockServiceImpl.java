package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.mapper.MoveReqMapper;
import com.jct.mes_new.biz.stock.mapper.MoveStockMapper;
import com.jct.mes_new.biz.stock.service.MoveStockService;
import com.jct.mes_new.biz.stock.vo.MoveItemVo;
import com.jct.mes_new.biz.stock.vo.MoveStockRequestVo;
import com.jct.mes_new.biz.stock.vo.MoveStockVo;
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
public class MoveStockServiceImpl implements MoveStockService {

    private final MoveStockMapper moveStockMapper;
    private final MoveReqMapper moveReqMapper;
    private final TranMapper tranMapper;

    public List<MoveStockVo> getMoveStockList(MoveStockVo vo){
        return moveStockMapper.getMoveStockList(vo);
    }

    public MoveStockRequestVo getMoveStockInfo(Long moveStockId){
        MoveStockRequestVo vo = new MoveStockRequestVo();

        vo.setMoveStockInfo( moveStockMapper.getMoveStockMst(moveStockId));
        vo.setMoveItemList(moveStockMapper.getMoveStockItemList(moveStockId));

        return vo;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveMoveStockInfo(MoveStockRequestVo vo){
        String userId = UserUtil.getUserId();
        MoveStockVo mst = vo.getMoveStockInfo();
        mst.setUserId(userId);

        if ( mst.getMoveStockId() == null ){
            if ( moveStockMapper.insertMoveStockMst(mst) <=0 ) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }

            List<MoveItemVo> moveItemList = vo.getMoveItemList();

            for(MoveItemVo moveItemVo : moveItemList){
                moveItemVo.setMoveStockId(mst.getMoveStockId());
                moveItemVo.setUserId(userId);

                if ( moveStockMapper.insertMoveStockItem(moveItemVo) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }else{
            if ( moveStockMapper.updateMoveStockMst(mst) <=0 ) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }

            //삭제 품목건
            List<Long> deleteMoveReqItemIds = vo.getDeleteMoveStockItemIds();
            if ( deleteMoveReqItemIds != null && !deleteMoveReqItemIds.isEmpty()  ){
                for ( Long id : deleteMoveReqItemIds ) {
                    moveReqMapper.deleteMoveReqItemId(id);
                }
            }

            List<MoveItemVo> moveItemList = vo.getMoveItemList();

            for(MoveItemVo moveItemVo : moveItemList){
                moveItemVo.setMoveStockId(mst.getMoveStockId());
                moveItemVo.setUserId(userId);

                if ( moveStockMapper.updateMoveStockItem(moveItemVo) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }


        return "저장되었습니다.";
    }


    /**
     * 자재이동 승인
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String saveMoveStockConfirm(MoveStockVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        //승인완료
        if ( moveStockMapper.updateMoveStockConfirm(vo) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //마스터
        MoveStockVo moveStockVo = moveStockMapper.getMoveStockMst(vo.getMoveStockId());

        //재고 등록
        TranVo tranVo = new TranVo();
        tranVo.setTranDate(moveStockVo.getMoveStockDate());
        tranVo.setSeq(moveStockVo.getRegSeq());
        tranVo.setAreaCd(moveStockVo.getAreaCd());
        tranVo.setTranTypeCd("J");
        tranVo.setSrcStorageCd(moveStockVo.getSrcStorageCd());
        tranVo.setTarStorageCd(moveStockVo.getTarStorageCd());
        tranVo.setManagerId(moveStockVo.getConfirmerId());
        tranVo.setRemark(moveStockVo.getEtc());
        tranVo.setTranStatus("C");
        tranVo.setEndYn("Y");
        tranVo.setUserId(userId);

        if( tranMapper.insertTranMst(tranVo) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        List<MoveItemVo> moveItemList = moveStockMapper.getMoveStockItemList(vo.getMoveStockId());

        for (MoveItemVo moveItemVo : moveItemList) {
            TranItemVo tranItemVo = new TranItemVo();

            tranItemVo.setTranId(tranVo.getTranId());
            tranItemVo.setItemTypeCd(moveItemVo.getItemTypeCd());
            tranItemVo.setItemCd(moveItemVo.getItemCd());
            tranItemVo.setItemName(moveItemVo.getItemName());
            tranItemVo.setTestNo(moveItemVo.getTestNo());
            tranItemVo.setQty(moveItemVo.getQty());
            tranItemVo.setInYn("Y");
            tranItemVo.setUserId(userId);
            if( tranMapper.insertTranItem(tranItemVo) <=0 ) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        if( moveStockMapper.updateTranId( vo.getMoveStockId(), tranVo.getTranId(), userId) <=0 ) {
            throw new BusinessException(ErrorCode.UPDATED);
        }

        return "승인되었습니다";
    }

}
