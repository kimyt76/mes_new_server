package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.ApprovalVo;
import com.jct.mes_new.biz.order.vo.BoardVo;
import com.jct.mes_new.biz.order.vo.OrderVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface OrderService {
    public List<OrderVo> getOrderList(OrderVo orderVo);

    String saveOrderInfo(OrderVo orderVo, ApprovalVo approvalVo, MultipartFile orderFile, MultipartFile prodFile) throws Exception;;

    int getSeq();

    ApprovalVo getApprovalInfo(String type);

    Map<String, Object> getOrderInfo(String orderId);
}
