package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.SaleMapper;
import com.jct.mes_new.biz.order.mapper.ShipmentMapper;
import com.jct.mes_new.biz.order.service.ShipmentService;
import com.jct.mes_new.biz.order.vo.*;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.system.mapper.StorageMapper;
import com.jct.mes_new.biz.system.vo.StorageVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.Snowflake;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentMapper shipmentMapper;
    private final FileHandlerMapper fileHandlerMapper;
    private final TranMapper tranMapper;
    private final StorageMapper storageMapper;

    public List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo) {
        return shipmentMapper.getShipmentList(shipmentVo);
    }

    public Map<String, Object> getShipmentInfo(Long shipmentId){
        Map<String, Object> map = new HashMap<String, Object>();

        ShipmentVo shipmentInfo = shipmentMapper.getShipmentInfo(shipmentId);

        map.put("shipmentInfo", shipmentInfo);
        map.put("shipmentItemList", shipmentMapper.getShipmentItemList(shipmentId));
        map.put("attachFileInfo",  fileHandlerMapper.getAttachFileList(shipmentInfo.getAttachFileId()) );

        return map;
    }

    public List<ShipmentWorkOrderVo> getWorkOrderItemList(ShipmentWorkOrderVo vo){
        return shipmentMapper.getWorkOrderItemList(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveShipmentInfo(ShipmentRequestVo vo){
        String userId = UserUtil.getUserId();

        ShipmentVo mst = vo.getShipmentInfo();
        mst.setUserId(userId);

        //첨부파일
        if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());
            // 업로드 결과가 비정상인 경우 방어
            if (fileVoList == null || fileVoList.isEmpty() || fileVoList.get(0).getAttachFileId() == null) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
            mst.setAttachFileId(fileVoList.get(0).getAttachFileId());

            for (FileVo f : fileVoList) {
                f.setUserId(userId);
                if (!fileHandlerMapper.saveFile(f)) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }

        //출고지시마스터
        if( shipmentMapper.insertShipmentMst(mst) <=0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //출고지시상세
        for(ShipmentItemListVo itemListVo : vo.getShipmentItemList()){
            itemListVo.setUserId(userId);
            itemListVo.setShipmentId(mst.getShipmentId());

            if( shipmentMapper.insertShipmentItemList(itemListVo) <=0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        if ( "Y".equals(mst.getShipmentYn()) ){
            //완료일경우  재고에 넣어야 함
            processShipmentInventory(mst.getShipmentId(), userId);
        }

        return "저장되었습니다.";
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateShipmentInfo(ShipmentRequestVo vo)  {
        String userId = UserUtil.getUserId();
        ShipmentVo mst = vo.getShipmentInfo();

        // 1. 삭제 첨부파일 처리
        if (vo.getDeleteFiles() != null && !vo.getDeleteFiles().isEmpty()) {
            for (FileVo f : vo.getDeleteFiles()) {
                fileHandlerMapper.deleteFile(f.getAttachFileId(), f.getSeq());
            }
        }
        // 2. 신규 첨부파일 추가
        if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());

            if (fileVoList != null && !fileVoList.isEmpty()) {
                // attachFileId 없으면 새로 세팅
                if (mst.getAttachFileId() == null) {
                    mst.setAttachFileId(fileVoList.get(0).getAttachFileId());
                }

                int nextSeq = fileHandlerMapper.nextSeq(mst.getAttachFileId());

                for (FileVo f : fileVoList) {
                    f.setAttachFileId(mst.getAttachFileId());
                    f.setSeq(nextSeq++);
                    f.setUserId(userId);

                    if (!fileHandlerMapper.saveFile(f)) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
                // attachFileId가 새로 생긴 케이스면 mst에 반영 필요할 수 있음(선택)
                // (현재 updateContractInfo SQL에 attach_file_id가 업데이트에 없으면 아래 추가 필요)
                //shipmentMapper.updateAttachFileId(mst.getShipmentId(), mst.getAttachFileId());
            }
        }
        // 3. 마스터 수정
        if (shipmentMapper.updateShipmentMst(mst) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        // 4. 삭제 상세 처리
        if (vo.getDeleteShipmentItemIds() != null && !vo.getDeleteShipmentItemIds().isEmpty()) {
            for( Long id : vo.getDeleteShipmentItemIds() ){
                shipmentMapper.deleteShipmentItemList(id);
            }
        }
        // 5. 상세 추가 / 수정
        if (vo.getShipmentItemList() != null) {
            for (ShipmentItemListVo item : vo.getShipmentItemList()) {
                item.setShipmentId(mst.getShipmentId());
                item.setUserId(userId);

                if (item.getShipmentItemId() == null) {
                    if (shipmentMapper.insertShipmentItemList(item) <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    if (shipmentMapper.updateShipmentItemList(item) <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }

        if ( "Y".equals(mst.getShipmentYn()) ){
            //완료일경우  재고에 넣어야 함
            processShipmentInventory(mst.getShipmentId(), userId);
        }
       return "수정되었습니다.";
    }

    private void processShipmentInventory(Long shipmentId, String userId) {
        /*
         * 이미 재고처리된 출고인지 확인
         *
         * 수정에서도 이 메서드를 호출할 것이기 때문에
         * 반드시 중복방지가 필요함
         */
//        int exists = shipmentMapper.countShipmentInventoryTran(shipmentId);
//
//        if (exists > 0) {
//            return;
//        }
        ShipmentVo mst = shipmentMapper.getShipmentInfo(shipmentId);
        /*
         * 출고 상세 조회
         */
        List<ShipmentItemListVo> itemList = shipmentMapper.getShipmentItemList(shipmentId);

        if (itemList == null || itemList.isEmpty()) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        /*
         * storage_cd 기준 그룹핑
         *
         * WH01
         *   ITEM-A
         *   ITEM-B
         *
         * WH02
         *   ITEM-C

        for (ShipmentItemListVo item : itemList) {
            log.info(
                    "shipmentItemId={}, itemCd={}, storageCd={}",
                    item.getShipmentItemId(),
                    item.getItemCd(),
                    item.getStorageCd()
            );
        }
         */
        Map<String, List<ShipmentItemListVo>> storageGroup = itemList.stream()
                        .collect(Collectors.groupingBy(
                                ShipmentItemListVo::getStorageCd
                        ));
        /*
         * 창고별 재고 트랜잭션 생성
         */
        for (Map.Entry<String, List<ShipmentItemListVo>> entry : storageGroup.entrySet()) {
            String storageCd = entry.getKey();
            List<ShipmentItemListVo> storageItems =entry.getValue();
            /*
             * 1. 재고 transaction master 생성
             */
            TranVo tranMst = new TranVo();

            StorageVo storageVo =  storageMapper.getStorageInfo(storageCd);

            String shipmentStatus = mst.getShipmentStatus();

            String tranTypeCd = shipmentStatus == null ? null :
                    switch (shipmentStatus) {
                        case "SA", "MS", "FC" -> "F";
                        case "MV", "FS"       -> "I";
                        case "PN"             -> "P";
                        default               -> null;
                    };

            String tarStorageCd = shipmentStatus == null ? null :
                switch (mst.getShipmentStatus()) {
                case "SA", "MS", "FC" -> "WC001";
                case "MV", "FS"       -> "WC002";
                default               -> null;
            };
            tranMst.setTranTypeCd(tranTypeCd); // 출고 코드
            tranMst.setTranDate(LocalDate.now());
            tranMst.setCustomerCd(mst.getClientId());
            tranMst.setSrcStorageCd(storageCd);
            tranMst.setTarStorageCd(tarStorageCd);
            tranMst.setAreaCd(storageVo.getAreaCd());
            tranMst.setTranStatus("C");
            tranMst.setEndYn("Y");
            tranMst.setUserId(userId);

            // 필요하면 출고번호 연결용 정보
            // tranMst.setRemark("출고번호 : " + shipmentId);
            if (tranMapper.insertTranMst(tranMst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }

            Long tranId = tranMst.getTranId();
            /*
             * 2. 해당 창고의 상세 재고처리
             */
            for (ShipmentItemListVo shipmentItem : storageItems) {
                /*
                 * 재고 transaction 상세
                 */
                TranItemVo tranItem = new TranItemVo();

                tranItem.setTranId(tranId);
                tranItem.setItemCd(shipmentItem.getItemCd());
                tranItem.setItemName(shipmentItem.getItemName());
                tranItem.setItemTypeCd(shipmentItem.getItemTypeCd());
                tranItem.setQty(shipmentItem.getQty());
                tranItem.setLotNo(shipmentItem.getLotNo());
                tranItem.setMakeNo(shipmentItem.getMakeNo());
                tranItem.setTestNo(shipmentItem.getTestNo());
                tranItem.setQcStatus(shipmentItem.getQcStatus());
                tranItem.setInYn("Y");
                /*
                 * 출고이므로 - 수량
                 *
                 * 현재 재고 VIEW가
                 * SUM(qty) 방식이라면 음수로 저장하는 구조가 편함

                tranItem.setQty(shipmentItem.getQty().negate());
                 */
                tranItem.setUserId(userId);

                if (tranMapper.insertTranItem(tranItem) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }

                /*
                 * 3. shipment_item ↔ tran_id 연결
                 */
                ShipmentInvTranVo relation = new ShipmentInvTranVo();

                relation.setShipmentItemId(shipmentItem.getShipmentItemId());
                relation.setTranId(tranId);
                relation.setUserId(userId);

                if (shipmentMapper.insertShipmentInvTran(relation) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }
    }

}


