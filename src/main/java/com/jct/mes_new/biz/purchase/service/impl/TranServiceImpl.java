package com.jct.mes_new.biz.purchase.service.impl;

import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.service.TranService;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranRequestVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TranServiceImpl implements TranService {

    private final TranMapper invTranMapper;


    /* 원장 신규 저장 */
    @Transactional
    public Long saveTranInfo(TranRequestVo vo){
        String userId = UserUtil.getUserId();
        TranVo mst = vo.getTranInfo();
        mst.setUserId(userId);

        //mst  저장
        Long cnt = invTranMapper.insertTranMst(mst);
        if ( cnt <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        for (TranItemVo d : vo.getTranItemList()) {
            d.setTranId(mst.getTranId());
        }
        // 3. 품목리스트 저장    //품질검사 요청 저장
        if (!vo.getTranItemList().isEmpty()) {
            for (TranItemVo item : vo.getTranItemList()) {

                item.setTranId(mst.getTranId());
                item.setUserId(userId);

                int insertCnt = invTranMapper.insertTranItem(item);

                if (insertCnt <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
                // insert 후 자동으로 PK 세팅됨
                Long tranItemId = item.getTranItemId();

                // QC 요청 생성
                //qcService.createQcRequest(  tranItemId, item);
            }
        }
        return mst.getTranId();
    }

    /* 원장 수정 */
    @Transactional
    public Long updateTranInfo(TranRequestVo vo){
        String msg = "수정되었습니다.";
        String userId = UserUtil.getUserId();

        TranVo mst = vo.getTranInfo();
        Long tranId = mst.getTranId();
        mst.setUserId(userId);

        if ( invTranMapper.updateTranMst(mst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        // 2. 삭제 처리
        List<Long> deletedItemIds = vo.getDeleteTranItems();
        if (deletedItemIds != null && !deletedItemIds.isEmpty()) {
            invTranMapper.deleteItemList(tranId,deletedItemIds);
        }
        //3. 발주 품목 처리
        List<TranItemVo> itemList = vo.getTranItemList();

        if (itemList != null && !itemList.isEmpty()) {
            for (TranItemVo item : itemList) {
                item.setTranId(tranId);
                item.setUserId(userId);
                if (item.getTranItemId() == null) {
                    // 신규 등록
                    int insertCnt = invTranMapper.insertTranItem(item);
                    if (insertCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    // 기존 수정
                    int updateCnt = invTranMapper.updateTranItem(item);
                    if (updateCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }
        return tranId;
    }


    /**
     * 원장 전체 삭제
     * @param purId
     */
    public void deleteTranInfo(Long purId) {
        Long tranId = invTranMapper.getTranId(purId);

        invTranMapper.deleteTranItem(tranId);
        invTranMapper.deleteTranMst(tranId);
    }


}
