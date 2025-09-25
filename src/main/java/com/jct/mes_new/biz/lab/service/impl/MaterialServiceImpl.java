package com.jct.mes_new.biz.lab.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.lab.mapper.MaterialMapper;
import com.jct.mes_new.biz.lab.service.MaterialService;
import com.jct.mes_new.biz.lab.vo.HistoryVo;
import com.jct.mes_new.biz.lab.vo.IngredientVo;
import com.jct.mes_new.biz.lab.vo.MaterialRequestVo;
import com.jct.mes_new.biz.lab.vo.MaterialVo;
import com.jct.mes_new.config.common.FileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final ItemMapper itemMapper;
    private final FileHandlerMapper fileHandlerMapper;

    public List<ItemVo> getMaterialItemList(MaterialVo vo){
        return materialMapper.getMaterialItemList(vo);
    }

    public MaterialRequestVo getMaterialInfo(String itemCd) {
        MaterialRequestVo vo = new MaterialRequestVo();

        vo.setItemInfo( itemMapper.getItemInfo(itemCd) );
        vo.setMaterialInfo(materialMapper.getMaterialInfo(itemCd));
        vo.setMaterialList(materialMapper.getMaterialList(itemCd));
        vo.setHistoryList( materialMapper.getHistoryList(itemCd));

        if (vo.getMaterialInfo().getAttachFileId() != null ) {
            vo.setFileList( fileHandlerMapper.getAttachFileList(vo.getMaterialInfo().getAttachFileId()));
        }
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveMaterialInfo(MaterialRequestVo vo){
        String msg = "저장되었습니다.";
        String itemCd = vo.getMaterialInfo().getItemCd();
        String attachFileId = vo.getMaterialInfo().getAttachFileId();
        String historyId = vo.getMaterialInfo().getHistoryId();
        String userId = vo.getMaterialInfo().getUserId();

        try {
            //변경이력
            if ( !vo.getHistoryList().isEmpty() ) {
                for ( HistoryVo item : vo.getHistoryList()) {
                    item.setItemCd(itemCd);
                    item.setUserId(userId);

                    if ( item.getHistoryId() != null ) {
                        if (materialMapper.updateHistory(item) <= 0) {
                            throw new Exception("변경이력 저장에 실패했습니다.");
                        }
                    }else{
                        if (materialMapper.insertHistory(item) <= 0) {
                            throw new Exception("변경이력 저장에 실패했습니다.");
                        }
                    }
                }

            }
            if ( vo.getMaterialList() != null && !vo.getMaterialList().isEmpty() ) {
                materialMapper.deleteMaterialList(itemCd);
                for ( IngredientVo  item : vo.getMaterialList()) {
                    item.setItemCd(itemCd);
                    item.setUserId(userId);
                }
                if (materialMapper.saveMaterialList(vo.getMaterialList()) <= 0 ) {
                    throw new Exception("성분저장에 실패했습니다.");
                }
            }
            //첨부파일 삭제 건
            if ( vo.getDeleteFiles() != null && !vo.getDeleteFiles().isEmpty() ) {
                for (FileVo item : vo.getDeleteFiles()){
                    fileHandlerMapper.deleteFile(item.getAttachFileId(), item.getSeq() );
                }
            }
            //첨부파일 신규 등록건
            // 신규 파일이 있을 경우만 처리
            if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
                // 파일 업로드 수행
                List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());
                // attachFileId 가 없으면 새로 채번 (첫 번째 파일 기준)
                if (attachFileId == null || attachFileId.isEmpty()) {
                    attachFileId = fileVoList.get(0).getAttachFileId();
                }
                int nextSeq = fileHandlerMapper.nextSeq(attachFileId);
                // 모든 파일에 userId 세팅 후 DB 저장
                for (FileVo item : fileVoList) {
                    item.setAttachFileId(attachFileId); // 기존 id 이어서 묶이도록 강제 세팅
                    item.setSeq(nextSeq);
                    item.setUserId(userId);
                    if (!fileHandlerMapper.saveFile(item)) {
                        throw new Exception("첨부 파일 저장 실패");
                    }
                    nextSeq++;
                }
            }
            //mst
            if ( materialMapper.saveMaterialMst(itemCd, attachFileId, userId) <= 0 ) {
                throw new Exception("원료저장 실패");
            };
        }catch (Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return msg;
    }


}
