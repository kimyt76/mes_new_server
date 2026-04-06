package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.service.ItemTestService;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemTestServiceImpl implements ItemTestService {

    private final ItemTestMapper itemTestMapper;

    /**
     * 시험내역별 조회
     * @param vo
     * @return
     */
    public List<ItemTestVo> getItemTestNoList(ItemTestVo vo){
        return itemTestMapper.getItemTestNoList(vo);
    }

    /**
     * 시험번호내역 상세 조회
     * @param testNo
     * @return
     */
    public ItemTestVo getItemTestNoInfo(String testNo) {
        return itemTestMapper.getItemTestNoInfo(testNo);
    }

    public List<ItemTestVo> getItemTestNoInfoList(String testNo){
        return itemTestMapper.getItemTestNoInfoList(testNo);
    }

    public String updateItemTestNoInfo(ItemTestVo vo){
        vo.setUserId(UserUtil.getUserId());
        if ( itemTestMapper.updateItemTestNo(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        return  "저장되었습니다.";
    }

    /**
     * QC  > 시험번호별 내역 등록
     * @param itemTestNoList
     * @param tranId
     * @param userId
     * @return
     */
    public int insertItemTestNo(List<ItemTestVo> itemTestNoList, Long tranId, String userId) {
        int seq = 1;
        int cnt = 0;

        List<ItemTestVo> testList = null;

        for( ItemTestVo item : itemTestNoList ){
            ItemTestVo itemTestVo = new ItemTestVo();

            if ( item.getTestNo() == null ){
                throw new BusinessException(ErrorCode.INTERNAL_ERROR);
            }

            if (itemTestMapper.countTestNo(item.getTestNo()) > 0 ){
                throw new BusinessException(ErrorCode.INTERNAL_ERROR);
            }

            itemTestVo.setItemTypeCd(item.getItemTypeCd());
            itemTestVo.setSeq(seq++);
            itemTestVo.setUserId(userId);

            cnt = itemTestMapper.insertItemTestNo(item);

            if (cnt <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return cnt;
    }

    public int updateItemTestNo(List<ItemTestVo> itemTestNoList, String userId){
        int seq = 1;

        for (  ItemTestVo item : itemTestNoList ){
            item.setUserId(userId);

            int testNoCnt = itemTestMapper.countTestNo(item.getTestNo());

            if ( testNoCnt > 0 ){
                if ( itemTestMapper.updateItemTestNo(item) <= 0 ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }else{
                item.setSeq(seq++);

                if ( itemTestMapper.insertItemTestNo(item) <= 0 ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return  1;
    }


}
