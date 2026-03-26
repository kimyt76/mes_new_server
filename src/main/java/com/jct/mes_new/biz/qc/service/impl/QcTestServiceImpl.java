package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestTypeMapper;
import com.jct.mes_new.biz.qc.service.QcTestService;
import com.jct.mes_new.biz.qc.vo.ProcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
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
public class QcTestServiceImpl implements QcTestService {

    private final QcTestMapper qcTestMapper;
    private final QcTestTypeMapper qcTestTypeMapper;
    private final ItemTestMapper itemTestMapper;

    /**
     * 품질검사요청 조회
     * @param vo
     * @return
     */
    public List<QcTestVo> getQcTestList(QcTestVo vo){
        return qcTestMapper.getQcTestList(vo);
    }

    /**
     * 품질검사 상세 조회
     * @param qcTestId
     * @return
     */
    public QcTestVo getQcTestDetailInfo(Long qcTestId){
        return qcTestMapper.getQcTestDetailInfo(qcTestId);
    }

    /**
     * 품질검사 상세 및 메소드 조회
     * @param qcTestId
     * @return
     */
    public QcTestRequestVo getQcTestInfo(Long qcTestId){
        QcTestRequestVo vo = new QcTestRequestVo();

        vo.setQcTestInfo(this.getQcTestDetailInfo(qcTestId));
        vo.setQcTestTypeMethodList(qcTestTypeMapper.getQcTestTypeMethod(vo.getQcTestInfo().getItemCd()) );
        return vo;
    }

    /**
     * 품질검사 상세 정보 수정
     * @param vo
     * @return
     */
    public String updateQcTestDetailInfo(QcTestVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if ( qcTestMapper.updateQcTestDetailInfo(vo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return "수정되었습니다.";
    }

    /**
     * 품질검사 재검사 요청
     * @param vo
     * @return
     */
    public String insertRetestInfo(QcTestVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if ( qcTestMapper.insertRetestInfo(vo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return "저장되었습니다.";
    }

    /**
     * 품질검사 등록 및 수정
     * @param vo
     * @return
     */
    public String updateQcTestInfo(QcTestRequestVo vo) {
        String userId = UserUtil.getUserId();

        QcTestVo qcTestMst = vo.getQcTestInfo();
        qcTestMst.setUserId(userId);

        if ( qcTestMapper.updateQcTestAllInfo(qcTestMst) <=  0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //삭제
        List<Long> getDeleteIds = vo.getDeleteIds();
        if (getDeleteIds != null && !getDeleteIds.isEmpty()) {
            qcTestTypeMapper.deleteQcTestTypeMethod(qcTestMst.getItemCd(), vo.getDeleteIds());
        }
        log.info("=========================================vo.getQcTestTypeMethodList()============== : " + vo.getQcTestTypeMethodList());
        for (QcTestTypeVo item : vo.getQcTestTypeMethodList()) {
            item.setItemCd(qcTestMst.getItemCd());
            item.setUserId(userId);
            item.setQcTestId(qcTestMst.getQcTestId());
log.info("=========================================item============== : " + item);
            if ( item.getTestTypeMethodId() != null ){
                if ( qcTestTypeMapper.updateTestTypeMethod(qcTestMst.getItemCd(), item) <= 0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }else{
                if ( qcTestTypeMapper.insertTestTypeMethod(item) <= 0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        //시험내역별 업데이트
        if (itemTestMapper.updateState(qcTestMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return "저장되었습니다.";
    }





    /**
     * 구매 등록시 자동 등록
     * @param qcTestList
     * @param tranId
     * @param userId
     * @return
     */
    public int insertQcTest(List<QcTestVo> qcTestList, Long tranId, String userId){
        int cnt = qcTestMapper.insertQcTest(qcTestList, userId);

        if( cnt <= 0){
            throw new BusinessException(ErrorCode.CREATED);
        }
        return cnt;
    }

    /**
     * 구매등록시 품질검사 등록 수정
     * @param qcTestList
     * @param userId
     * @return
     */
    public int updateQcTest(List<QcTestVo> qcTestList,  String userId){
        for (QcTestVo item : qcTestList ){
            int testNoCnt = qcTestMapper.getTestNoCnt(item.getTestNo(), item.getReqDate());

            if(testNoCnt > 0){
                if (qcTestMapper.updateQcTestInfo(item) <=0){
                    throw new BusinessException(ErrorCode.UPDATED);
                };
            }else{
                if (qcTestMapper.insertSingleQcTest(item ,userId) <=0){
                    throw new BusinessException(ErrorCode.CREATED);
                };
            }
        }
        return 1;
    }
}
