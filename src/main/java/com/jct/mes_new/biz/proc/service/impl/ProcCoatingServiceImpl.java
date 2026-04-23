package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.proc.mapper.ProcCoatingMapper;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.service.ProcCoatingService;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcProdInfoVo;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcCoatingServiceImpl implements ProcCoatingService {

    private final ProcCoatingMapper procCoatingMapper;
    private final ItemMapper itemMapper;
    private final ItemTestMapper itemTestMapper;
    private final WorkOrderMapper workOrderMapper;
    private final ProcCommonMapper procCommonMapper;

    public List<ProcCoatingVo> getCoatingList(ProcCoatingVo vo){
        return procCoatingMapper.getCoatingList(vo);
    }

    public ProcProdInfoVo getCoatingInfo(ProcCommonVo vo){
        ProcProdInfoVo info = new ProcProdInfoVo();

        info.setItemInfo(itemMapper.getItemInfo(vo.getItemCd()));
        info.setProdList(procCommonMapper.getProdList(vo.getProcCd(), vo.getWorkProcId()));
        info.setWorkOrderProcInfo(workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId()) );
        info.setWorkRecordList(procCommonMapper.getWorkRecordList(vo.getWorkProcId()) );

        return info;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String startProcCoating(ProcCoatingVo vo) {
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        ProcCommonVo procCommonVo = new ProcCommonVo();
        procCommonVo.setWorkBatchId(vo.getWorkBatchId());
        procCommonVo.setBatchStatus(vo.getBatchStatus());
        procCommonVo.setUserId(userId);

        if ( procCommonMapper.updateBatchStatus(procCommonVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //코팅에 제조 테스트 번호 추가
        //testNo 추출
        String prefix = CodeUtil.createTestNo(LocalDate.now(), vo.getAreaCd(), "M5");
        Integer nextSeq = itemTestMapper.getNextTestNoSeq(prefix);
        String testNo = prefix + String.format("%03d", nextSeq);

        vo.setTestNo(testNo);
        if ( procCoatingMapper.startProcCoating(vo) <= 0  ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        WorkOrderInfoVo workOrderInfo =  workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());

        //시험번호 등록
        ItemTestVo itemTestVo = new ItemTestVo();

        LocalDate shelfLife = LocalDate.now().plusDays(365 * 2);
        LocalDate expiryDate = LocalDate.now().plusDays(365 * 2);

        itemTestVo.setTestNo(testNo);
        itemTestVo.setCreateDate(LocalDate.now());
        itemTestVo.setAreaCd(vo.getAreaCd());
        itemTestVo.setItemTypeCd("M5");
        itemTestVo.setSeq(1);
        itemTestVo.setItemCd(vo.getItemCd());
        itemTestVo.setLotNo(workOrderInfo.getLotNo());
        itemTestVo.setMakeNo(workOrderInfo.getMakeNo());
        itemTestVo.setQty(workOrderInfo.getOrderQty());
        itemTestVo.setCustomerCd(workOrderInfo.getClientId());
        itemTestVo.setExpiryDate(expiryDate);
        itemTestVo.setShelfLife(shelfLife);
        itemTestVo.setTestState("END");
        itemTestVo.setPassState("PASS");
        itemTestVo.setEndYn("N");
        itemTestVo.setUserId(userId);

        if ( itemTestMapper.insertItemTestNo(itemTestVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return "코팅작업을 시작할수 있습니다.";
    }


    /**
     * 코팅 작업종료
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String completeCoating(ProcCoatingVo vo){

        String userId = UserUtil.getUserId();

        //배치 업데이트
        //if( procCoatingMapper.updateBatchStatus()  )
        //proc 업데이트

        //제조불출로





        return  "저장되었습니다.";
    }


}
