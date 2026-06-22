package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.proc.vo.ProcItemVo;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.mapper.MoveReqMapper;
import com.jct.mes_new.biz.stock.service.MoveReqService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveReqServiceImpl implements MoveReqService {

    private final MoveReqMapper moveReqMapper;
    private final TranMapper tranMapper;

    public int getNextRegSeq(MoveStockVo vo){
        return moveReqMapper.getNextRegSeq(vo);
    }

    public List<MoveStockVo> getMoveReqList(MoveStockVo vo){
        return moveReqMapper.getMoveReqList(vo);
    }

    public MoveStockRequestVo getMoveReqInfo(Long moveStockId){
        MoveStockRequestVo vo = new MoveStockRequestVo();
        //자재이동 마스터
        vo.setMoveStockInfo( moveReqMapper.getMoveReqMst(moveStockId)   );
        //요청 리스트
        vo.setProcItemList( moveReqMapper.getMoveReqProcList(moveStockId) );
        //실제 이동 품목 리스트
        vo.setMoveItemList( moveReqMapper.getMoveItemList(moveStockId) );

        return vo;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveProcMoveReq(MoveStockRequestVo vo) {
        String userId = UserUtil.getUserId();
        //이동요청 마스터 mst
        MoveStockVo mst = vo.getMoveStockInfo();
        mst.setUserId(userId);
        if ( moveReqMapper.insertMoveReqMst(mst) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //공정 요청 건prodItemList
        List<ProcItemVo> procItemList = vo.getProcItemList();

        for( ProcItemVo procItemVo : procItemList ) {
            procItemVo.setMoveReqId(mst.getMoveStockId());
            procItemVo.setUserId(userId);

            if ( moveReqMapper.insertProcItem(procItemVo) <=0 ) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return "요청되었습니다.";
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveMoveItem(MoveStockRequestVo vo) {
        String userId = UserUtil.getUserId();

        //이동요청 마스터 mst 업데이트
        MoveStockVo mst = vo.getMoveStockInfo();
        mst.setUserId(userId);
        if ( moveReqMapper.updateMoveReqMst(mst) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //실제 이동요청 품목
        //삭제 품목건
        List<Long> deleteMoveReqItemIds = vo.getDeleteMoveStockItemIds();
        if ( deleteMoveReqItemIds != null && !deleteMoveReqItemIds.isEmpty()  ){
            for ( Long id : deleteMoveReqItemIds ) {
                moveReqMapper.deleteMoveReqItemId(id);
            }
        }
        //이동 품목 건
        List<MoveItemVo> moveItemList = vo.getMoveItemList();
        if (moveItemList != null && !moveItemList.isEmpty() ){
            for( MoveItemVo moveItemVo : moveItemList ) {
                moveItemVo.setMoveStockId(mst.getMoveStockId());
                moveItemVo.setUserId(userId);

                if ( moveItemVo.getMoveStockItemId() == null ) {
                    if ( moveReqMapper.insertMoveItem(moveItemVo) <=0 ) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }else{
                    if ( moveReqMapper.updateMoveItem(moveItemVo) <=0 ) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }
        return "요청되었습니다.";
    }

    public List<StockVo> getMoveReqStockList(StockVo vo){
        return moveReqMapper.getMoveReqStockList(vo);
    }

    /**
     * 자재이동요청 승인
     * @param moveReqId
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String saveConfirmMoveReq(Long moveReqId){
        String userId = UserUtil.getUserId();

        MoveStockVo mst = moveReqMapper.getMoveReqMst(moveReqId);
        List<MoveItemVo> moveItemList = moveReqMapper.getMoveItemList(moveReqId);

        mst.setUserId(userId);
        //승인완료
        if ( moveReqMapper.updateMoveReqMst(mst) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        //재고 등록
        TranVo tranVo = new TranVo();
        tranVo.setTranDate(mst.getMoveRegDate());
        tranVo.setSeq(mst.getRegSeq());
        tranVo.setAreaCd(mst.getAreaCd());
        tranVo.setTranTypeCd("J");
        tranVo.setSrcStorageCd(mst.getSrcStorageCd());
        tranVo.setTarStorageCd(mst.getTarStorageCd());
        tranVo.setManagerId(mst.getMoveManagerId());
        tranVo.setEndYn("Y");
        tranVo.setUserId(userId);

        if( tranMapper.insertTranMst(tranVo) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

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

        return "승인되었습니다";
    }

}
