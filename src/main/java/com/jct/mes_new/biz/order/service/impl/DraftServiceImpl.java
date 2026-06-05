package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.DraftMapper;
import com.jct.mes_new.biz.order.service.DraftService;
import com.jct.mes_new.biz.order.vo.DraftApprovalVo;
import com.jct.mes_new.biz.order.vo.DraftRequestVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class DraftServiceImpl implements DraftService {

    private final DraftMapper draftMapper;
    private final FileHandlerMapper fileHandlerMapper;

    /**
     * 발주서 리스트
     * @param draftVo
     * @return List<OrderVo>
     */
    public List<DraftVo> getDraftList(DraftVo draftVo) {return draftMapper.getDraftList(draftVo);}

    /**
     * 기안서정보 조회
     * @param draftId
     * @return
     */
    public DraftRequestVo getDraftInfo(Long draftId){
        DraftRequestVo vo = new DraftRequestVo();

        vo.setDraftInfo( draftMapper.getDraftInfo(draftId) );
        vo.setDraftApprovalList( draftMapper.getDraftApprovalList(draftId) );
        String  attachFileId = draftMapper.getDraftAttachFileId(draftId);

        if (attachFileId != null && !attachFileId.isEmpty()) {
            vo.setAttachFileInfo(fileHandlerMapper.getAttachFileList(attachFileId)  );
        }

        return vo;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public Long saveDraftInfo(DraftRequestVo draftRequest){
        String userId = UserUtil.getUserId();
        DraftVo mst = draftRequest.getDraftInfo();
        List<DraftApprovalVo> approvalList = draftRequest.getDraftApprovalList();
        List<MultipartFile> newFiles = draftRequest.getNewFiles();
        List<FileVo> deleteFileIds = draftRequest.getDeleteFileList();

        mst.setUserId(userId);

        if ( mst.getDraftId() == null || mst.getDraftId() == 0 ) {
            //신규
            if (newFiles != null && !newFiles.isEmpty()) {
                List<FileVo> fileVoList = FileUpload.multiFileUpload(newFiles);
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
                //마스터 생성
                if( draftMapper.insertDraftInfo(mst) <= 0 ){
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
                //결재 생성
                for( DraftApprovalVo approvalVo: approvalList){
                    approvalVo.setUserId(userId);
                    approvalVo.setDraftId(mst.getDraftId());

                    if(draftMapper.insertDraftApprovalInfo(approvalVo) <= 0 ) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
            }
        }else{
            //수정
//            //첨부파일 삭제
//            if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
//                for (Long deleteFileId : deleteFileIds) {
//                    fileHandlerMapper.deleteFile(deleteFileId, seq);
//                }
//
//                draftRequest.getDeleteFileIds();
//
//            //신규파일등록
//
//
//            //마스터 업데이트
//            if( draftMapper.updateDraftInfo(mst) <= 0 ){
//                throw new BusinessException(ErrorCode.FAIL_UPDATED);
//            }
        }

        return mst.getDraftId();
    }


    /**
     * 발주 정보 수정정보 업데이트
     * @param info
     * @return
     * @throws Exception
    */
    @Transactional(rollbackFor = Exception.class)
    public String saveApprovalComment(Map<String, String> info){
//        log.info("================================================ : " + info );
//        BoardVo boardVo = new BoardVo();
//        String userId = UserUtil.getUserId();
//        String boardUserId = info.get("boardUserId");
//        String draftId = info.get("draftId");
//        int cnt = draftMapper.getDraftBoardCnt(boardUserId, draftId);
//
//        //의견
//        boardVo.setBoardUserId(info.get("boardUserId"));
//        boardVo.setBoardTxt(info.get("boardTxt"));
//        boardVo.setUserId(userId);
//        if( cnt == 0 ){
//            boardVo.setDraftBoardId(CommonUtil.createUUId());
//            if ( draftMapper.insertBoardInfo(boardVo) <=0 ){
//                throw new BusinessException(ErrorCode.FAIL_CREATED);
//            }
//        }else{
//            if ( draftMapper.updateBoardInfo(boardVo) <=0 ){
//                throw new BusinessException(ErrorCode.FAIL_CREATED);
//            }
//        }
//
//        //결재일자 업데이트
//        if ( draftMapper.updateApproval() <=0 ){
//            throw new BusinessException(ErrorCode.FAIL_CREATED);
//        }


        return "저장되었습니다.";
    }

}


