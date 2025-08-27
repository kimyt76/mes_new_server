package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.DraftMapper;
import com.jct.mes_new.biz.order.service.DraftService;
import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.BoardVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.UserUtil;
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
    public List<DraftVo> getDraftList(DraftVo draftVo) {
        return draftMapper.getDraftList(draftVo);
    }

    /**
     * 발주서 정보 등록
     * @param draftVo  발주정보
     * @param approvalVo  결재정보
     * @param orderFile  첨부파일
     * @param prodFile  첨부파일
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveDraftInfo(DraftVo draftVo,
                                ApprovalVo approvalVo,
                                MultipartFile orderFile,
                                MultipartFile prodFile) throws Exception {

        String msg = "저장되었습니다.";
        draftVo.setUserId(UserUtil.getUserId());
        log.info("================draftVo.getDraftId()================== : " + draftVo.getDraftId());
        try {
            if (draftVo.getDraftId() == null) {
                draftVo.setDraftId(CommonUtil.createUUId());
                draftVo.setApprovalId(CommonUtil.createUUId());
                //결재정보 저장
                if (!draftMapper.saveApprovalInfo(draftVo.getApprovalId(), approvalVo.getLabUserId())) {
                    throw new Exception("결재정보 등록 오류 발생");
                }
            }else{
                if (!draftMapper.updateApprovalInfo(draftVo.getApprovalId(), approvalVo.getLabUserId())) {
                    throw new Exception("결재정보 수정 오류 발생");
                }
            }

            if (orderFile != null) {
                FileVo order = FileUpload.singleFileUpload(orderFile);
                draftVo.setOrderAttachFileId(order.getAttachFileId());

                // 첨부파일 저장
                if (!fileHandlerMapper.saveFile(order)) {
                    throw new Exception("발주서 파일 저장 실패");
                }
            }
            if (prodFile != null) {
                FileVo prod = FileUpload.singleFileUpload(prodFile);
                draftVo.setProdAttachFileId(prod.getAttachFileId());

                if (!fileHandlerMapper.saveFile(prod)) {
                    throw new Exception("제품사양서 파일 저장 실패");
                }
            }
            // 발주서 저장
            if (!draftMapper.saveDraftInfo(draftVo)) {
                throw new Exception("발주서 정보 저장 실패");
            }
        }catch (Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return msg;
    }

    /**
     * 문서번호 seq 추출
     * @return
     */
    public int getSeq() {
        return draftMapper.getSeq();
    }

    /**
     * 결재정보 조회
     * @param type
     * @return
     */
    public ApprovalVo getApprovalInfo(String type){
        return draftMapper.getApprovalInfo(type);
    }

    /**
     * 기안서정보 조회
     * @param draftId
     * @return
     */
    public Map<String, Object> getDraftInfo(String draftId){
        Map<String, Object> map = new HashMap<>();
        DraftVo draftVo = draftMapper.getDraftInfo(draftId);
        List<BoardVo> boardList = draftMapper.getBoardInfo(draftVo.getBoardId());
        int seq = 1;
        //발주정보
        map.put("draftInfo", draftVo);
        //첨부파일 정보
        map.put("orderAttachFileInfo",  fileHandlerMapper.getAttachFileInfo(draftVo.getOrderAttachFileId() , seq) );
        map.put("prodAttachFileInfo", fileHandlerMapper.getAttachFileInfo(draftVo.getProdAttachFileId(), seq) );
        //결재정보
        map.put("approvalInfo", draftMapper.getApprovalInfo(draftVo.getApprovalId()) );
        //댓글 정보
        map.put("boardInfo", boardList );

        return map;
    }

    /**
     * 발주 정보 수정정보 업데이트
     * @param info
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateInfo(Map<String, String> info) throws Exception{
        String msg = "저장되었습니다.";

        try{
            BoardVo boardVo = new BoardVo();

            if ( info.get("boardId") == null ) {
                boardVo.setBoardId(CommonUtil.createUUId());
                draftMapper.saveBoardId(boardVo.getBoardId(), info.get("draftId"));
            }else{
                boardVo.setBoardId(info.get("boardId"));
            }
            boardVo.setBoardTxt(info.get("boardTxt"));
            boardVo.setBoardUserId(info.get("userId"));
            boardVo.setUserId(info.get("userId"));

            String approvalId = info.get("approvalId");

            if ( info.get("appDate") != null ) {
                //결재자 정보 업데이트
                if (!draftMapper.updateApproval(info.get("field") , info.get("appDate"), approvalId) ){
                    throw new Exception("결재자 정보 업데이트중 오류가 발생했습니다.");
                }
            }

            //게시판 업데이트
            if ( !draftMapper.saveBoardInfo(boardVo)){
                throw new Exception("게시판 정보 업데이트중 오류가 발생했습니다.");
            }
        }catch(Exception e){
            throw new RuntimeException("저장에 실패했습니다.", e);
        }

        return msg;
    }
}


