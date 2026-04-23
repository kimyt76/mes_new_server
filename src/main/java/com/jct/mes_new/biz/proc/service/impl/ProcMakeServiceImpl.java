package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcMakeMapper;
import com.jct.mes_new.biz.proc.service.ProcMakeService;
import com.jct.mes_new.biz.proc.vo.MakeInfoVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcProcTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestMapper;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcMakeServiceImpl implements ProcMakeService {

    private final ProcMakeMapper procMakeMapper;
    private final ProcCommonMapper procCommonMapper;
    private final ItemTestMapper itemTestMapper;
    private final QcTestMapper qcTestMapper;


    public List<ProcMakeVo> getMatList(ProcMakeVo vo){
        return procMakeMapper.getMatList(vo);
    }

    /**
     * 제조 상세 조회
     * @param vo
     * @return
     */
    public MakeInfoVo getMakeInfo(ProcMakeVo vo){
        MakeInfoVo info = new MakeInfoVo();
        String procCd = "PRC001";

        //제조량은 칭량정보를 가져오기 때문에   workProcId를 칭량으로 가져와야함
        Long workProcId = procCommonMapper.getWorkProcId(vo.getWorkBatchId(), procCd);

        info.setProcMake(procMakeMapper.getMakeHeadInfo(vo.getWorkBatchId()));
        //제조량은 칭량정보를 가져오기 때문에   workProcId를 칭량으로 가져와야함
        info.setMakeBomList(procMakeMapper.getRealBomMakeList(workProcId, vo.getItemCd()));
        return info;
    }

    /**
     * 제조 작업 시작
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String startProcMake(ProcMakeVo vo){
        String userId = UserUtil.getUserId();
        ProcMakeVo procMakeVo = procMakeMapper.getMakeHeadInfo(vo.getWorkBatchId());

        //testNo 추출
        String prefix = CodeUtil.createTestNo(LocalDate.now(), procMakeVo.getAreaCd(), "M3");
        Integer nextSeq = itemTestMapper.getNextTestNoSeq(prefix);
        String testNo = prefix + String.format("%03d", nextSeq);

        //work_proc업데이트
        vo.setUserId(userId);
        vo.setTestNo(testNo);
        if(procMakeMapper.updateProcMake(vo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //work_batch 업데이트
        ProcCommonVo procCommonVo = new ProcCommonVo();
        procCommonVo.setWorkBatchId(vo.getWorkBatchId());
        procCommonVo.setBatchStatus(vo.getBatchStatus());
        procCommonVo.setUserId(userId);
        if(procCommonMapper.updateBatchStatus(procCommonVo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        LocalDate shelfLife = LocalDate.now().plusDays(365 * 2);
        LocalDate expiryDate = LocalDate.now().plusDays(365 * 2);

        //시험번호별 내역 등록
        ItemTestVo itemTestVo = new ItemTestVo();
        itemTestVo.setTestNo(testNo);
        itemTestVo.setCreateDate(LocalDate.now());
        itemTestVo.setAreaCd(procMakeVo.getAreaCd());
        itemTestVo.setItemTypeCd("M3");
        itemTestVo.setSeq(1);
        itemTestVo.setItemCd(procMakeVo.getItemCd());
        itemTestVo.setItemName(procMakeVo.getItemName());
        itemTestVo.setMakeNo(procMakeVo.getMakeNo());
        itemTestVo.setLotNo(procMakeVo.getLotNo());
        itemTestVo.setCustomerCd(procMakeVo.getClientId());
        itemTestVo.setQty(BigDecimal.ZERO);
        itemTestVo.setExpiryDate(expiryDate);
        itemTestVo.setShelfLife(shelfLife);
        itemTestVo.setTestState("REQ");
        itemTestVo.setPassState("REQ");
        itemTestVo.setUserId(userId);

        if ( itemTestMapper.insertItemTestNo(itemTestVo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //품질검사 등록
        QcTestVo qcTestVo = new QcTestVo();
        qcTestVo.setTestNo(testNo);
        qcTestVo.setReqDate(LocalDate.now());
        qcTestVo.setRetestYn("N");
        qcTestVo.setAreaCd(procMakeVo.getAreaCd());
        qcTestVo.setStorageCd(procMakeVo.getStorageCd());
        qcTestVo.setReqTesterId(userId);
        qcTestVo.setReqQty(BigDecimal.ZERO);

        if(qcTestMapper.insertSingleQcTest(qcTestVo, userId) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return "제조작업 시작하세요";
    }

    public Long getWeighQty(Long weighId){
        return procMakeMapper.getWeighQty(weighId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String insertRowMake(ProcWeighBomVo vo){
        String userId = UserUtil.getUserId();

//        if ( procMakeMapper.insertRowMake(vo) <= 0){
//            throw new BusinessException(ErrorCode.FAIL_UPDATED);
//        }

        //


        return "저장되었습니다.";
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveMakeInfo(MakeInfoVo vo){
        String userId = UserUtil.getUserId();
        ProcMakeVo mst = vo.getProcMake();
        List<ProcWeighBomVo> makeBomList = vo.getMakeBomList();

        //mst 업데이트
        ProcCommonVo commonVo = new ProcCommonVo();
        commonVo.setWorkProcId(mst.getWorkProcId());
        commonVo.setEtc(mst.getEtc());
        commonVo.setUserId(userId);
        if(procCommonMapper.updateProcEtc(commonVo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        for(ProcWeighBomVo item : makeBomList ){
            item.setUserId(userId);

            if (procMakeMapper.updateMakeRecipe(item) <=0  ){
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
        }
        return "저장되었습니다.";
    }
}
