package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.proc.vo.ProcItemVo;
import com.jct.mes_new.biz.stock.mapper.MoveReqMapper;
import com.jct.mes_new.biz.stock.service.MoveReqService;
import com.jct.mes_new.biz.stock.vo.MoveItemVo;
import com.jct.mes_new.biz.stock.vo.MoveReqRequestVo;
import com.jct.mes_new.biz.stock.vo.MoveReqVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoveReqServiceImpl implements MoveReqService {

    private final MoveReqMapper moveReqMapper;


    @Transactional(rollbackFor = BusinessException.class)
    public String saveProcMoveReq(MoveReqRequestVo vo) {
        String userId = UserUtil.getUserId();
        //이동요청 마스터 mst
        MoveReqVo mst = vo.getMoveReqInfo();
        mst.setUserId(userId);
        if ( moveReqMapper.insertMoveReqMst(mst) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //공정 요청 건prodItemList
        List<ProcItemVo> procItemList = vo.getProcItemList();

        for( ProcItemVo procItemVo : procItemList ) {
            procItemVo.setMoveReqId(mst.getMoveReqId());
            procItemVo.setUserId(userId);

            if ( moveReqMapper.insertProcItem(procItemVo) <=0 ) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return "요청되었습니다.";
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveMoveReq(MoveReqRequestVo vo) {
        String userId = UserUtil.getUserId();

        //이동요청 마스터 mst
        MoveReqVo mst = vo.getMoveReqInfo();
        mst.setUserId(userId);
        if ( moveReqMapper.updateMoveReqMst(mst) <=0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //실제 이동요청 품목
        //삭제 품목건
        List<Long> deleteMoveReqItemIds = vo.getDeleteMoveReqItemIds();
        if ( deleteMoveReqItemIds != null && !deleteMoveReqItemIds.isEmpty()  ){
            for ( Long id : deleteMoveReqItemIds ) {
                moveReqMapper.deleteMoveReqItemId(id);
            }
        }
        //이동 품목 건
        List<MoveItemVo> moveItemList = vo.getMoveItemList();
        if (moveItemList != null && !moveItemList.isEmpty() ){
            for( MoveItemVo moveItemVo : moveItemList ) {
                moveItemVo.setMoveReqId(mst.getMoveReqId());
                moveItemVo.setUserId(userId);

                if ( moveItemVo.getMoveReqItemId() == null ) {
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


}
