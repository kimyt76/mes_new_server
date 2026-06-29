package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.DraftApprovalVo;
import com.jct.mes_new.biz.order.vo.DraftRequestVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface DraftService {
    List<DraftVo> getDraftList(DraftVo orderVo);

    DraftRequestVo getDraftInfo(Long draftId);

    String saveApprovalComment(DraftApprovalVo info);

    Long saveDraftInfo(DraftRequestVo draftRequest);
}
