package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jfree.util.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProcCommonServiceImpl implements ProcCommonService {

    private final ProcCommonMapper procCommonMapper;
    private final ProcWeighMapper procWeighMapper;


    public List<ProcCommonVo> getWorkerList(ProcCommonVo vo){
        return procCommonMapper.getWorkerList(vo.getAreaCd(), vo.getProcCd());
    }
    public List<ProcCommonVo> getBagWeightList(){
        return procCommonMapper.getBagWeightList();
    }

    public List<ProcCommonVo> getEquipmentList(String storageCd){
        return procCommonMapper.getEquipmentList(storageCd);
    }

    /**
     * 작업지시 상태 업데이트
     * @param vo
     * @return
     */
    public String updateProcStatus(ProcCommonVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if (procCommonMapper.updateProcStatus(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        if (procCommonMapper.updateBatchStatus(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        return "수정되었습니다.";
    }

    /**
     * 제조출고 조회
     * @param vo
     * @return
     */
    public List<ProcTranVo> getProcTranList(SearchCommonVo vo){
        return procCommonMapper.getProcTranList(vo);
    }


    @Transactional(rollbackFor = Exception.class)
    public Long saveProdInfo(ProcUseRequestVo vo){
        String userId = UserUtil.getUserId();

        ProcUseInfoVo mst = vo.getProdInfo();
        List<ProcUseInfoVo> prodUseList = vo.getProdUseList();
        mst.setUserId(userId);

        //투입자재 정보 등록
        Long prodInfoId = procCommonMapper.insertProdInfo(mst);

        if ( prodInfoId == null || prodInfoId <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        Log.info("========================prodInfoId============= : " + prodInfoId);
        Log.info("========================mst.getProdInfoId()============= : " + mst.getProdInfoId());
        //투입량 저장
        for (ProcUseInfoVo prodUseInfo : prodUseList){
            prodUseInfo.setProdInfoId(mst.getProdInfoId());
            prodUseInfo.setUserId(userId);

            if ( procCommonMapper.insertProdUseInfo(prodUseInfo)  <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        return prodInfoId;
    }

}
