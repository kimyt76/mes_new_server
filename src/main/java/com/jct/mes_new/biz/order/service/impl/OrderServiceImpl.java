package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.OrderMapper;
import com.jct.mes_new.biz.order.service.OrderService;
import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.BoardVo;
import com.jct.mes_new.biz.order.vo.OrderVo;
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
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final FileHandlerMapper fileHandlerMapper;

    /**
     * 발주서 리스트
     * @param orderVo
     * @return List<OrderVo>
     */
    public List<OrderVo> getOrderList(OrderVo orderVo) {
        return orderMapper.getOrderList(orderVo);
    }

    /**
     * 발주서 정보 등록
     * @param orderVo  발주정보
     * @param approvalVo  결재정보
     * @param orderFile  첨부파일
     * @param prodFile  첨부파일
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveOrderInfo(OrderVo orderVo,
                                ApprovalVo approvalVo,
                                MultipartFile orderFile,
                                MultipartFile prodFile) throws Exception {

        String msg = "저장되었습니다.";
        orderVo.setUserId(UserUtil.getUserId());

        try {
            if (orderVo.getOrderId() == null) {
                orderVo.setOrderId(CommonUtil.createUUId());
                orderVo.setApprovalId(CommonUtil.createUUId());

                //결재정보 저장
                if (!orderMapper.saveApprovalInfo(orderVo.getApprovalId(), approvalVo.getLabUserId())) {
                    throw new Exception("결재정보 등록 오류 발생");
                }
            }

            if (orderFile != null) {
                FileVo order = FileUpload.singleFileUpload(orderFile);
                orderVo.setOrderAttachFileId(order.getAttachFileId());

                // 첨부파일 저장
                if (!fileHandlerMapper.saveFile(order)) {
                    throw new Exception("발주서 파일 저장 실패");
                }
            }
            if (prodFile != null) {
                FileVo prod = FileUpload.singleFileUpload(prodFile);
                orderVo.setProdAttachFileId(prod.getAttachFileId());

                if (!fileHandlerMapper.saveFile(prod)) {
                    throw new Exception("제품사양서 파일 저장 실패");
                }
            }
            //        if ( boardVo != null ) {
            //            if ( boardVo.getBoardId().isEmpty() ) {
            //                boardVo.setBoardId(CommonUtil.createUUId());
            //                orderVo.setBoardId(boardVo.getBoardId());
            //            }
            //            //게시글 저장
            //            if ( !orderMapper.saveBoardInfo(boardVo) ) {
            //                throw new Exception("게시글 등록 시 오류 발생");
            //            }
            //        }

            // 발주서 저장
            if (!orderMapper.saveOrderInfo(orderVo)) {
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
        return orderMapper.getSeq();
    }

    /**
     * 결재정보 조회
     * @param type
     * @return
     */
    public ApprovalVo getApprovalInfo(String type){
        return orderMapper.getApprovalInfo(type);
    }

    /**
     * 발주정보 조회
     * @param orderId
     * @return
     */
    public Map<String, Object> getOrderInfo(String orderId){
        Map<String, Object> map = new HashMap<>();
        OrderVo orderVo = orderMapper.getOrderInfo(orderId);
        List<BoardVo> boardList = orderMapper.getBoardInfo(orderVo.getBoardId());
        int seq = 1;
        //발주정보
        map.put("orderInfo", orderVo);
        //첨부파일 정보
        map.put("orderAttachFileInfo",  fileHandlerMapper.getAttachFileInfo(orderVo.getOrderAttachFileId() , seq) );
        map.put("prodAttachFileInfo", fileHandlerMapper.getAttachFileInfo(orderVo.getProdAttachFileId(), seq) );
        //결재정보
        map.put("approvalInfo", orderMapper.getApprovalInfo(orderVo.getApprovalId()) );

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
            String orderId = info.get("orderId");
            BoardVo boardVo = new BoardVo();

            if ( info.get("boardId") == null ) {
                boardVo.setBoardId(CommonUtil.createUUId());
                orderMapper.saveBoardId(boardVo.getBoardId(), orderId);
            }else{
                boardVo.setBoardId(info.get("boardId"));
            }
            boardVo.setBoardTxt(info.get("boardTxt"));
            boardVo.setBoardUserId(info.get("userId"));
            boardVo.setUserId(info.get("userId"));

            String approvalId = info.get("approvalId");

            if ( info.get("appDate") != null ) {
                //결재자 정보 업데이트
                if (!orderMapper.updateApproval(info.get("field") , info.get("appDate"), approvalId) ){
                    throw new Exception("결재자 정보 업데이트중 오류가 발생했습니다.");
                }
            }

            //게시판 업데이트
            if ( !orderMapper.saveBoardInfo(boardVo)){
                throw new Exception("게시판 정보 업데이트중 오류가 발생했습니다.");
            }
        }catch(Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }

        return msg;
    }
}


