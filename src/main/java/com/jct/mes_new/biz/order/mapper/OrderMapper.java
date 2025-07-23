package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.BoardVo;
import com.jct.mes_new.biz.order.vo.OrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    public List<OrderVo> getOrderList(OrderVo orderVo);

    boolean saveOrderInfo(OrderVo orderVo);

    int getSeq();

    ApprovalVo getApprovalInfo(@Param("approvalId") String approvalId);

    OrderVo getOrderInfo(@Param("orderId") String orderId);

    boolean saveApprovalInfo(@Param("approvalId") String approvalId, @Param("labUserId") String labUserId);

    boolean saveBoardInfo(BoardVo boardVo);

    List<BoardVo> getBoardInfo(@Param("boardId") String boardId);

    boolean updateApproval(String field, String appDate, String approvalId);

    void saveBoardId(String boardId, String orderId);
}
