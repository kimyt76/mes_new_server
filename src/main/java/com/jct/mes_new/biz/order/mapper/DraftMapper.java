package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.BoardVo;
import com.jct.mes_new.biz.order.vo.DraftVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DraftMapper {

    public List<DraftVo> getDraftList(DraftVo draftVo);

    boolean saveDraftInfo(DraftVo draftVo);

    int getSeq();

    ApprovalVo getApprovalInfo(@Param("approvalId") String approvalId);

    DraftVo getDraftInfo(@Param("draftId") String draftId);

    boolean saveApprovalInfo(@Param("approvalId") String approvalId, @Param("labUserId") String labUserId);

    boolean saveBoardInfo(BoardVo boardVo);

    List<BoardVo> getBoardInfo(@Param("boardId") String boardId);

    boolean updateApproval(String field, String appDate, String approvalId);

    void saveBoardId(String boardId, String orderId);
}
