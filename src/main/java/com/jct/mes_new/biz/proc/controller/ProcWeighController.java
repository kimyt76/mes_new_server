package com.jct.mes_new.biz.proc.controller;


import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.config.common.MessageUtil;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.util.BarcodeUtil;
import com.jct.mes_new.config.util.JasperUtil;
import com.jct.mes_new.config.util.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.geom.Area;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/procWeigh")
public class ProcWeighController {

    private final ProcWeighService procWeighService;
    private final ProcCommonService procCommonService;
    private final MessageUtil messageUtil;

    @PostMapping("/getWeighList")
    public List<ProcWeighVo> getWeighList(@RequestBody ProcWeighVo vo) {
        return procWeighService.getWeighList(vo);
    }

    @PostMapping("/getWeighInfo")
    public WeighInfoVo getWeighInfo(@RequestBody ProcWeighVo vo){
        return procWeighService.getWeighInfo(vo);
    }

    /**
     * 칭량 시작
     * @param vo
     * @return
     */
    @PostMapping("/startProcWeigh")
    public String startProcWeigh(@RequestBody ProcWeighVo vo){
        return procWeighService.startProcWeigh(vo);
    }

    /**
     * 칭량량 조회
     * @param vo
     * @return
     */
    @PostMapping("/getStockTestNoList")
    public List<ProcWeighVo> getStockTestNoList(@RequestBody ProcWeighVo vo){
        return procWeighService.getStockTestNoList(vo);
    }

    @PostMapping("/saveWeighInfo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveWeighInfo(@RequestBody WeighInfoVo vo){
        Long workProcId = procWeighService.saveWeighInfo(vo);
        Map<String, Object> result =  new HashMap<>();
        result.put("workProcId", workProcId);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), result));
    }
    @PostMapping("/completeWeight")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completeWeight(@RequestBody ProcWeighVo vo){
        Long workProcId = procWeighService.completeWeight(vo);
        Map<String, Object> result =  new HashMap<>();
        result.put("workProcId", workProcId);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), result));
    }

    @PostMapping("/saveWeighList")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveWeighList(@RequestBody WeighInvInfo vo){
        Map<String, Object> result =  new HashMap<>();
        result.put("msg", procWeighService.saveWeighList(vo));
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), result));
    }


    @PostMapping("/printWeighLabel")
    public ResponseEntity<Resource> printWeighLabel(@RequestBody ProcWeighBomVo[] labelItems) throws Exception {
        List<WeighLabelPrint> labelPrintList = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("#,##0.000");
        DecimalFormat df2 = new DecimalFormat("#,##0.00000");
        String unit = " kg";

        ProcWeighVo workOrderItem = procWeighService.getWeighHeadInfo(labelItems[0].getWorkProcId());
        String prodItemCd = workOrderItem.getItemCd();
        String prodItemName = workOrderItem.getItemName();
        String prodQty = df.format(workOrderItem.getOrderQty()) + unit;
        String lotNo = workOrderItem.getLotNo();
        String prodNo = workOrderItem.getMakeNo();
        // WorkOrder 조회하여  areaCd 확인, 외주인 경우 itemName 표시하지 않음  QQQQ
        String itemName = "";
        for (ProcWeighBomVo labelItem : labelItems ) {
            WeighLabelPrint item = new WeighLabelPrint();
            item.setMatUseId( String.valueOf(labelItem.getWeighId()));
            item.setProdItemCd(prodItemCd);
            item.setProdItemName(prodItemName);

            item.setItemName(labelItem.getItemName());
            item.setItemAlias(labelItem.getPhase() + "-" + labelItem.getOrderDist());
            item.setProdQty( prodQty);
            item.setBagWeight(df.format(labelItem.getBagWeight() == null ? 0 : labelItem.getBagWeight()) + unit);
            item.setWeighQty(df2.format(labelItem.getWeighQty() == null ? 0 : labelItem.getWeighQty()) + unit);
            item.setTotalQty(df.format(labelItem.getTotQty() == null ? 0 : labelItem.getTotQty()) + unit);
            // QR 생성
            try {
                item.setBarcodeImage(BarcodeUtil.generateQRCodeImage(item.getMatUseId()));
            } catch (Exception e) {
                item.setBarcodeImage(null);
            }
            item.setLotNo(lotNo);
            item.setProdNo(prodNo);

            labelPrintList.add(item);
        }

        try {
            InputStream reportStream = getClass().getResourceAsStream("/report/weigh_label_v2.jrxml");
            byte[] pdfContent = JasperUtil.getPdfBinary(reportStream, labelPrintList);
            ByteArrayResource resource = new ByteArrayResource(pdfContent);
            HttpHeaders header = JasperUtil.getHeader("mat_label_list", pdfContent.length);

            return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("리포트에서 에러발생");
        }
    }




}
