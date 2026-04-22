package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.proc.mapper.ProcCoatingMapper;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.service.ProcCoatingService;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcProdInfoVo;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcCoatingServiceImpl implements ProcCoatingService {

    private final ProcCoatingMapper procCoatingMapper;
    private final ItemMapper itemMapper;
    private final WorkOrderMapper workOrderMapper;
    private final ProcCommonMapper procCommonMapper;

    public List<ProcCoatingVo> getCoatingList(ProcCoatingVo vo){
        return procCoatingMapper.getCoatingList(vo);
    }

    public ProcProdInfoVo getCoatingInfo(ProcCommonVo vo){
        ProcProdInfoVo info = new ProcProdInfoVo();

        info.setItemInfo(itemMapper.getItemInfo(vo.getItemCd()));
        info.setProdList(procCommonMapper.getProdList(vo.getProcCd(), vo.getWorkProcId()));
        info.setWorkOrderProcInfo(workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId()) );
        info.setWorkRecordList(procCommonMapper.getWorkRecordList(vo.getWorkProcId()) );

        return info;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String startProcCoating(ProcCommonVo vo) {
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if ( procCommonMapper.updateBatchStatus(vo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        if ( procCoatingMapper.startProcCoating(vo) <= 0  ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return "코팅작업을 시작할수 있습니다.";
    }


}
