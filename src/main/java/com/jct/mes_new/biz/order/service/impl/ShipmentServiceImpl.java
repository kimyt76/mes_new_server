package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.SaleMapper;
import com.jct.mes_new.biz.order.mapper.ShipmentMapper;
import com.jct.mes_new.biz.order.service.ShipmentService;
import com.jct.mes_new.biz.order.vo.*;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentMapper shipmentMapper;
    private final SaleMapper saleMapper;
    private final FileHandlerMapper fileHandlerMapper;

    public List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo) {
        return shipmentMapper.getShipmentList(shipmentVo);
    }

    public List<ShipmentItemListVo> getSalesItemList(String saleIds){
        List<String> saleIdList = Arrays.asList(saleIds.split(","));

        return saleMapper.getSalesItemList(saleIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveShipmentInfo(ShipmentVo shipmentInfo, List<ShipmentItemListVo> itemList, List<MultipartFile> attachFileList) throws Exception {
        Snowflake snowflake = new Snowflake(1, 1);
        String msg ="저장되었습니다.";
        int distOrder = 0;
        try{
            /* 출하지시서 id 채번*/
            if ( shipmentInfo.getShipmentId() == null || shipmentInfo.getShipmentId().isEmpty()   ){
                shipmentInfo.setShipmentId(CommonUtil.generateUUID());
            }

            /* 첨부파일 정보 저장 및 ID 채번*/
            if ( attachFileList != null ){
                List<FileVo> fileVoList = FileUpload.multiFileUpload(attachFileList);
                shipmentInfo.setAttachFileId(fileVoList.get(0).getAttachFileId());

                for (FileVo item : fileVoList) {
                    item.setUserId(shipmentInfo.getUserId());
                    if (!fileHandlerMapper.saveFile(item)) {
                        throw new Exception("첨부 파일 저장 실패");
                    }
                }
            }

            if ( shipmentMapper.saveShipmentInfo(shipmentInfo) <= 0 ) {
                throw new Exception("주문서 저장에 실패했습니다.");
            }else{
                shipmentMapper.deleteShipmentItemList(shipmentInfo.getShipmentId());
                for (ShipmentItemListVo item : itemList) {
                    item.setShipmentItemId(String.valueOf(snowflake.nextId()));
                    item.setShipmentId(shipmentInfo.getShipmentId());
                    item.setId(distOrder++);
                    item.setUserId(shipmentInfo.getUserId());

                    if ( shipmentMapper.saveItemList(item)  <= 0 ){
                        throw new Exception("품목리스트 저장 실패");
                    }
                }
            }
        }catch(Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return msg;
    }

    public Map<String, Object> getShipmentInfo(String shipmentId){
        Map<String, Object> map = new HashMap<String, Object>();

        ShipmentVo shipmentInfo = shipmentMapper.getShipmentInfo(shipmentId);
        List<ShipmentItemListVo> itemList = shipmentMapper.getItemList(shipmentId);

        map.put("shipmentInfo", shipmentInfo);
        map.put("itemList", itemList);
        map.put("attachFileInfo",  fileHandlerMapper.getAttachFileList(shipmentInfo.getAttachFileId()) );

        return map;
    }

    public List<ShipmentItemListVo> getShipmentItemList(String shipmentId){
        return shipmentMapper.getShipmentItemList(shipmentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateShipmentInfo(ShipmentSaveRequestVo vo) throws Exception {
        Snowflake snowflake = new Snowflake(1, 1);
        String msg = "저장되었습니다";

        try {

            if ( shipmentMapper.saveShipmentInfo(vo.getShipmentInfo()) <= 0 ) {
                throw new Exception("출하지시서 저장에 실패했습니다.");
            }

            String shipmentId = vo.getShipmentInfo().getShipmentId();
            String userId = vo.getShipmentInfo().getUserId();

            shipmentMapper.deleteShipmentItemList(shipmentId);

            for(ShipmentItemListVo item : vo.getItemList()){
                item.setShipmentItemId(String.valueOf(snowflake.nextId()));
                item.setShipmentId(shipmentId);
                item.setUserId(userId);

                shipmentMapper.saveItemList(item);
            }

            log.info("--------------vo.getDeleteFiles()---------------------------------- : " + vo.getDeleteFiles());

            if ( vo.getDeleteFiles() != null && !vo.getDeleteFiles().isEmpty() ) {
                for (FileVo item : vo.getDeleteFiles()){
                    fileHandlerMapper.deleteFile(item.getAttachFileId(), item.getSeq() );
                }
            }

            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());
            int nextSeq = fileHandlerMapper.nextSeq( vo.getShipmentInfo().getAttachFileId());
            log.info("--------------nextSeq---------------------------------- : " + nextSeq);
            log.info("--------------getAttachFileId---------------------------------- : " + vo.getShipmentInfo().getAttachFileId());
            if ( vo.getShipmentInfo().getAttachFileId()  == null ){
                vo.getShipmentInfo().setAttachFileId(fileVoList.get(0).getAttachFileId());
            }

            for (FileVo item : fileVoList) {
                item.setAttachFileId(vo.getShipmentInfo().getAttachFileId());
                item.setSeq(nextSeq);
                item.setUserId(userId);
                if (!fileHandlerMapper.saveFile(item)) {
                    throw new Exception("첨부 파일 저장 실패");
                }
                nextSeq++;
            }

        }catch (Exception e) {
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }

        return msg;
    }
}
