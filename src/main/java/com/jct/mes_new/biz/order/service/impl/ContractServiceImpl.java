package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.ContractMapper;
import com.jct.mes_new.biz.order.service.ContractService;
import com.jct.mes_new.biz.order.vo.*;
import com.jct.mes_new.biz.stock.vo.StockVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.util.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final FileHandlerMapper fileHandlerMapper;

    public List<ContractVo> getContractList(ContractVo contractVo){
        return contractMapper.getContractList(contractVo);
    }

    public Map<String, Object> getContractInfo(String contractId){
        Map<String, Object> map = new HashMap<>();

        ContractVo contractInfo = contractMapper.getContractInfo(contractId);
        List<ContractItemVo> itemList = contractMapper.getContractItemList(contractId);

        map.put("contractInfo", contractInfo);
        map.put("itemList", itemList);
        map.put("attachFileInfo",  fileHandlerMapper.getAttachFileList(contractInfo.getAttachFileId()) );

        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveContractInfo(ContractSaveRequestVo vo) {
        String userId = UserUtil.getUserId();
        ContractVo contractVo = vo.getContractInfo();

        //점부파일
        if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());
            // 업로드 결과가 비정상인 경우 방어
            if (fileVoList == null || fileVoList.isEmpty() || fileVoList.get(0).getAttachFileId() == null) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
            contractVo.setAttachFileId(fileVoList.get(0).getAttachFileId());

            for (FileVo f : fileVoList) {
                f.setUserId(userId);
                if (!fileHandlerMapper.saveFile(f)) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }
        //마스터
        if(contractMapper.insertContractInfo(contractVo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //수주품목
        for(ContractItemVo contractItemVo : vo.getItemList()){
            contractItemVo.setContractId(contractVo.getContractId());
            contractItemVo.setUserId(userId);

            if(contractMapper.insertContractItem(contractItemVo) <= 0){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return  contractVo.getContractId();
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateContractInfo(ContractSaveRequestVo vo) {
        String userId = UserUtil.getUserId();
        ContractVo contractInfo = vo.getContractInfo();

        //마스터 업데이트
        if(  contractMapper.updateContractInfo(contractInfo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        if (vo.getItemList() != null && !vo.getItemList().isEmpty()) {
            for(ContractItemVo contractItemVo : vo.getItemList()) {
                contractItemVo.setContractId(contractInfo.getContractId());
                contractItemVo.setUserId(userId);

                if(contractMapper.updateContractItem(contractItemVo) <= 0 ) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        // 4) 삭제 파일 처리
        if (vo.getDeleteFiles() != null && !vo.getDeleteFiles().isEmpty()) {
            for (FileVo f : vo.getDeleteFiles()) {
                fileHandlerMapper.deleteFile(f.getAttachFileId(), f.getSeq());
            }
        }
        // 5) 신규 파일 업로드/저장
        if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());

            if (fileVoList != null && !fileVoList.isEmpty()) {
                // attachFileId 없으면 새로 세팅
                if (contractInfo.getAttachFileId() == null) {
                    contractInfo.setAttachFileId(fileVoList.get(0).getAttachFileId());
                }

                int nextSeq = fileHandlerMapper.nextSeq(contractInfo.getAttachFileId());

                for (FileVo f : fileVoList) {
                    f.setAttachFileId(contractInfo.getAttachFileId());
                    f.setSeq(nextSeq++);
                    f.setUserId(userId);

                    if (!fileHandlerMapper.saveFile(f)) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
                // attachFileId가 새로 생긴 케이스면 mst에 반영 필요할 수 있음(선택)
                // (현재 updateContractInfo SQL에 attach_file_id가 업데이트에 없으면 아래 추가 필요)
                contractMapper.updateAttachFileId(contractInfo.getContractId(), contractInfo.getAttachFileId());
            }
        }
        return "수정되었습니다.";
    }


    public List<OrderPlanVo> getOrderPlanList(OrderPlanVo vo){
        return contractMapper.getOrderPlanList(vo);
    }

    public List<OrderPlanTypeVo> getOrderPlanType(OrderPlanTypeVo vo){
        return contractMapper.getOrderPlanType(vo);
    }

    public String saveOrderPlan(OrderPlanTypeRequestVo vo){
        String userId = UserUtil.getUserId();

        OrderPlanTypeVo mst  = vo.getOrderPlanTypeInfo();
        List<OrderPlanTypeVo> orderPlanTypeList = vo.getOrderPlanTypeList();
        List<Long> ids = vo.getDeleteOrderPlanIds();

        if ( ids.size() > 0 ) {
            contractMapper.deleteOrderPlanTypeList(ids);
        }

        for(OrderPlanTypeVo orderPlanTypeVo : orderPlanTypeList){
            orderPlanTypeVo.setTypeCd(mst.getTypeCd());
            orderPlanTypeVo.setPoNo(mst.getPoNo());
            orderPlanTypeVo.setUserId(userId);

            if ( orderPlanTypeVo.getOrderPlanId() == null ) {
                if ( contractMapper.insertOrderPlanType(orderPlanTypeVo) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                if ( contractMapper.updateOrderPlanType(orderPlanTypeVo) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }

        }
        return "저장하였습니다.";
    }

    public String updateOrderPlanYn(OrderPlanVo vo){
        String userId = UserUtil.getUserId();

        vo.setUserId(userId);

        if (contractMapper.updateOrderPlanYn(vo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return "저장되었습니다.";
    }

    public List<WorkOrderInfoVo> getMatWorkOrder(WorkOrderInfoVo vo){
        return contractMapper.getMatWorkOrder(vo);
    }

    public Map<String, Object> getRequiredQuantityList(OrderPlanVo vo){
        Map<String, Object> map = new HashMap<>();
        map.put("orderStockList", contractMapper.getRequiredQuantityList(vo));

        return map;
    }


}
