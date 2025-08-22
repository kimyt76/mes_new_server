package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface DraftService {
    public List<DraftVo> getDraftList(DraftVo orderVo);

    String saveDraftInfo(DraftVo draftVo, ApprovalVo approvalVo, MultipartFile orderFile, MultipartFile prodFile) throws Exception;;
    //String saveOrderInfo(OrderVo orderVo, ApprovalVo approvalVo, BoardVo boardVo, MultipartFile orderFile, MultipartFile prodFile) throws Exception;;

    int getSeq();

    ApprovalVo getApprovalInfo(String type);

    Map<String, Object> getDraftInfo(String draftId);

    String updateInfo(Map<String, String> info) throws Exception;

}
