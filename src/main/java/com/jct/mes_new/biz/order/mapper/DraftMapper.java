package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.DraftApprovalVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DraftMapper {

    public List<DraftVo> getDraftList(DraftVo draftVo);

    /**
     * 사양서 전체 조회
     * @param draftId
     * @return
     */
    DraftVo getDraftInfo(@Param("draftId") Long draftId);
    List<DraftApprovalVo> getDraftApprovalList(Long draftId);

    /**
     * 사양서 신규
     * @param mst
     * @return
     */
    int insertDraftInfo(DraftVo mst);
    int insertDraftApprovalInfo(DraftApprovalVo approvalVo);

    String getDraftAttachFileId(Long draftId);

    /**
     * 사양서 수정
     * @param mst
     * @return
     */
    int updateDraftInfo(DraftVo mst);



}
