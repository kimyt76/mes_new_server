package com.jct.mes_new.biz.mobile.controller;


import com.jct.mes_new.biz.mobile.service.MobileService;
import com.jct.mes_new.biz.monitoring.vo.MatMonitoringVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.biz.proc.vo.ProcUseRequestVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkerVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile")
public class MobileController {
    private final MobileService mobileService;
    private final MessageUtil messageUtil;
    //설비 조회
    @GetMapping("/getEquipmentList/{areaCd}")
    public List<MatMonitoringVo> getEquipmentList(@PathVariable("areaCd") String areaCd, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        log.info("===== 모바일 설비 조회 세션 =====");
        log.info("SESSION ID : {}", session != null ? session.getId() : "없음");
        log.info("USER INFO  : {}", session != null ? session.getAttribute("userInfo") : null);
        return  mobileService.getEquipmentList(areaCd);
    }

    //설비에에  없는 제조공정
    @PostMapping("/getWorkOrderItems")
    public List<WorkOrderInfoVo> getWorkOrderItems(@RequestBody WorkOrderInfoVo vo){
        return  mobileService.getWorkOrderItems(vo);
    }

    //제조공정 작업지시 조회
    @GetMapping("/getMatUseList/{workProcId}")
    public List<ProcWeighBomVo> getMatUseList(@PathVariable("workProcId") Long workProcId){
        return  mobileService.getMatUseList(workProcId);
    }

    //작업자 정보 조회
    @GetMapping("/getWorkerAllList")
    public List<WorkerVo> getWorkerAllList(){
        return  mobileService.getWorkerAllList();
    }
    //pda 체크

    @PostMapping("/investmentMatUse")
    public List<ProcWeighBomVo> investmentMatUse(@RequestBody ProcMakeVo vo){
        return  mobileService.investmentMatUse(vo);
    }

    //작업시작
    @PostMapping("/startWorkOrderItem")
    public ResponseEntity<ApiResponse<Long>> startWorkOrderItem(@RequestBody ProcMakeVo vo) {
        Long prodInfoId = mobileService.startWorkOrderItem(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), prodInfoId));
    }


    //작업종료
    @PostMapping("/finishWorkOrderItem")
    public ResponseEntity<ApiResponse<Long>> finishWorkOrderItem(@RequestBody ProcMakeVo vo) {
        Long prodInfoId = mobileService.finishWorkOrderItem(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), prodInfoId));
    }




}
