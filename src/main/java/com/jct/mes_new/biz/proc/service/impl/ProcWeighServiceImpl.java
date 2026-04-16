package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.PurchaseVo;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
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
@RequiredArgsConstructor
@Service
public class ProcWeighServiceImpl implements ProcWeighService {

    private final ProcWeighMapper procWeighMapper;
    private final ProcCommonMapper procCommonMapper;
    private final TranMapper tranMapper;

    /**
     * 칭량조회
     * @param vo
     * @return
     */
    public List<ProcWeighVo> getWeighList(ProcWeighVo vo){
        return procWeighMapper.getWeighList(vo);
    }

    public ProcWeighVo getWeighHeadInfo(Long workProcId){
        return procWeighMapper.getWeighHeadInfo(workProcId);
    }
    /**
     * 칭량 상세 조회
     * @param vo
     * @return
     */
    public WeighInfoVo getWeighInfo(ProcWeighVo vo){
        WeighInfoVo info = new WeighInfoVo();

        info.setProcWeigh(this.getWeighHeadInfo(vo.getWorkProcId()));
        List<ProcWeighBomVo> recipeList = null;

        if (procWeighMapper.checkWeighCnt(vo.getWorkProcId()) > 0 ){
            recipeList = procWeighMapper.getRealBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }else{
            recipeList = procWeighMapper.getBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }
        info.setWeightBomList(recipeList);

        return info;
    }

    /**
     * 칭량 작업 시작
     * @param vo
     * @return
     */
    public String startProcWeigh(ProcWeighVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);
        // 작업지시 상태 업데이트 (작업상태, 배치상태, 작업처, 작업일자, 작업시간
        if (procWeighMapper.startProcWeigh(vo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        ProcCommonVo comVo = new ProcCommonVo();
        comVo.setWorkBatchId(vo.getWorkBatchId());
        comVo.setBatchStatus(vo.getBatchStatus());
        comVo.setUserId(userId);
        if (procCommonMapper.updateBatchStatus(comVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        List<ProcWeighBomVo>  recipeList = procWeighMapper.getBomWeighList(vo.getWorkProcId(), vo.getItemCd());

        for ( ProcWeighBomVo item : recipeList ){
            item.setUserId(userId);
            item.setWorkProcId(vo.getWorkProcId());
            item.setWorkBatchId(vo.getWorkBatchId());

            if ( procWeighMapper.insertWeighRecipe(item) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return "칭량작업을 시작할수 있습니다.";
    }

    /**
     * 칭량량 조회
     * @param vo
     * @return
     */
    public List<ProcWeighVo> getStockTestNoList(ProcWeighVo vo){
        return procWeighMapper.getStockTestNoList(vo);
    }

    /**
     * 칭량량 등록  (재고용)
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String saveWeighList(WeighInvInfo vo){
        ProcWeighVo mst = vo.getWeighInfo();
        List<ProcWeighVo> weighList = vo.getWeighList();
        List<Long> deleteWeighItems = vo.getDeleteWeighItems();

        String userId = UserUtil.getUserId();

        if (deleteWeighItems != null && !deleteWeighItems.isEmpty()) {
            procWeighMapper.deleteWeighList(mst.getWorkProcId(), deleteWeighItems);
        }

        if (weighList != null && !weighList.isEmpty()) {
            for (ProcWeighVo item : weighList) {
                item.setWeighId(mst.getWeighId());
                item.setUserId(userId);
                if (item.getWeighInvId() == null) {
                    // 신규 등록
                    int insertCnt = procWeighMapper.insertWeighInvItem(item);
                    if (insertCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    // 기존 수정
                    int updateCnt = procWeighMapper.updateWeighInvItem(item);
                    if (updateCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }
        return "칭량정보를 저장했습니다.";
    }

    /**
     * 칭량 정보 및 리스트 저장
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public Long saveWeighInfo(WeighInfoVo vo) {
        ProcWeighVo mst = vo.getProcWeigh();
        List<ProcWeighBomVo> recipeList = vo.getWeightBomList();
        String userId = UserUtil.getUserId();

        if (procWeighMapper.updateProcWeigh(mst) <=0  ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        int cntWeigh = procWeighMapper.checkWeighCnt(mst.getWorkProcId());

        for(ProcWeighBomVo item : recipeList ){
            item.setWorkProcId(mst.getWorkProcId());
            item.setWorkBatchId(mst.getWorkBatchId());
            item.setUserId(userId);
            if(cntWeigh <= 0) {
                if (procWeighMapper.insertWeighRecipe(item) <=0  ){
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                item.setUserId(userId);
                if (procWeighMapper.updateWeighRecipe(item) <=0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return mst.getWorkProcId();
    }

    /**
     * 칭량 완료
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public Long completeWeight(ProcWeighVo vo){
        String userId = UserUtil.getUserId();

        //재고 마스터
        ProcWeighVo mst = this.getWeighHeadInfo(vo.getWorkProcId());
        TranVo invMst = new TranVo();
        String fromStorage = "";
        if ( "A001".equals(mst.getAreaCd()) ){
            fromStorage = "WS103";
        }else if ( "A002".equals(mst.getAreaCd()) ){
            fromStorage = "WA102";
        }else{
            fromStorage = "WS103";
        }

        invMst.setTranDate(LocalDate.now());
        invMst.setTranTypeCd("E");
        invMst.setAreaCd(mst.getAreaCd());
        invMst.setFromStorageCd(mst.getStorageCd());
        invMst.setToStorageCd(fromStorage);
        invMst.setManagerId(mst.getManagerId());
        invMst.setEndYn("Y");
        invMst.setTranStatus("C");
        invMst.setPoNo(mst.getPoNo());
        invMst.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //재고 상세
        List<ProcWeighVo> weighInvList = procWeighMapper.getWeighInvInfo(vo.getWorkProcId());

        for(ProcWeighVo item : weighInvList) {
            TranItemVo tranItemVo = new TranItemVo();

            tranItemVo.setTranId(invMst.getTranId());
            tranItemVo.setItemTypeCd(item.getItemTypeCd());
            tranItemVo.setItemCd(item.getItemCd());
            tranItemVo.setItemName(item.getItemName());
            tranItemVo.setLotNo(item.getLotNo());
            tranItemVo.setTestNo(item.getTestNo());
            tranItemVo.setExpiryDate(item.getExpiryDate());
            tranItemVo.setQty(item.getWeighQty());
            tranItemVo.setWeighInvId(item.getWeighInvId());
            tranItemVo.setUserId(userId);

            if ( tranMapper.insertTranItem(tranItemVo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        vo.setTranId(invMst.getTranId() );
        vo.setUserId(userId);
        //작업지시 업데이트 (공정)
        if( procWeighMapper.updateWeighProcComplete(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //작업지시 업데이트 (배치)
        ProcCommonVo comVo = new ProcCommonVo();
        comVo.setWorkProcId(vo.getWorkProcId());
        comVo.setWorkBatchId(vo.getWorkBatchId());
        comVo.setBatchStatus(vo.getBatchStatus());
        comVo.setUserId(userId);
        if ( procCommonMapper.updateBatchStatus(comVo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        return vo.getWorkProcId();
    }
}
