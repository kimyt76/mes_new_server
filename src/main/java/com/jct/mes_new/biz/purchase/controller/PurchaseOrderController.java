package com.jct.mes_new.biz.purchase.controller;

import com.jct.mes_new.biz.base.service.CustomerService;
import com.jct.mes_new.biz.base.vo.CustomerVo;
import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.service.MailService;
import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.common.vo.MailVo;
import com.jct.mes_new.biz.common.vo.PoSheetMailVo;
import com.jct.mes_new.biz.purchase.mapper.PurchaseOrderMapper;
import com.jct.mes_new.biz.purchase.service.PurchaseOrderService;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderMailVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderSheet;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import com.jct.mes_new.config.mail.MailTemplates;
import com.jct.mes_new.config.util.AmountUtil;
import com.jct.mes_new.config.util.JasperUtil;
import com.jct.mes_new.config.util.RestResponse;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/purchaseOrder")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final MailService mailService;
    private final MessageUtil messageUtil;
    private final CommonService commonService;
    private final CustomerService customerService;

    /**
     * 발주 조회
     * @param vo
     * @return
     */
    @PostMapping("/getPurchaseOrderList")
    public List<PurchaseOrderVo.PurchaseOrderListVo> getPurchaseOrderList(@RequestBody PurchaseOrderVo vo) {
        return purchaseOrderService.getPurchaseOrderList(vo);
    }

    /**
     * 발주상세 조회
     * @param map
     * @return
     */
    @PostMapping("/getPurchaseOrderInfo")
    public PurchaseOrderRequestVo getPurchaseOrderInfo(@RequestBody Map<String, Object> map) {
        return purchaseOrderService.getPurchaseOrderInfo(map);
    }

    /**
     * 발주 저장
     * @param vo
     * @return
     */
    @PostMapping("/savePurchaseOrder")
    public ResponseEntity<ApiResponse<Void>> savePurchaseOrder(@RequestBody PurchaseOrderRequestVo vo) {
        String result = purchaseOrderService.savePurchaseOrder(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    /**
     * 발주 수정
     * @param vo
     * @return
     */
    @PostMapping("/updatePurchaseOrder")
    public ResponseEntity<ApiResponse<Void>> updatePurchaseOrder(@RequestBody PurchaseOrderRequestVo vo) {
        String result = purchaseOrderService.updatePurchaseOrder(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.updated")));
    }





    @PostMapping("/orderMail")
    public RestResponse<T> orderMail(@RequestBody MailVo vo) throws Exception {
        final int PO_ITEM_MAX_ROWS = 11;

        Long purOrderId = vo.getId();

        Map<String, Object> map = new HashMap<>();
        map.put("purOrderId", purOrderId);
        map.put("itemTypeCd", vo.getItemTypeCd());

        List<PurchaseOrderSheet> poSheetList = new ArrayList<>();
        PurchaseOrderRequestVo reqVo = purchaseOrderService.getPurchaseOrderInfo(map);
        List<CommonVo> commonList = commonService.getCodeList("appr_line");
        String line1 = commonList.get(0).getCodeNm();
        String line2 = commonList.get(1).getCodeNm();
        String line3 = commonList.get(2).getCodeNm();
        String line4 = commonList.get(3).getCodeNm();

        PurchaseOrderSheet poSheet = new PurchaseOrderSheet();
        PurchaseOrderVo poVo = reqVo.getPurchaseOrderInfo();
        PurchaseOrderVo.PurchaseOrderItemVo poItemVo = new PurchaseOrderVo.PurchaseOrderItemVo();
        poSheet.setMemberName(poVo.getManagerName());
        poSheet.setOrderDate(poVo.getPurOrderDate());
        poSheet.setDeliveryDate(poVo.getDeliveryDate());

        CustomerVo customer = customerService.getCustomerInfo(poVo.getCustomerCd() );
        poSheet.setCustomerManager(customer.getCustomerManager());
        poSheet.setCustomerName(customer.getCustomerName());
        poSheet.setEmail(customer.getEmail());
        poSheet.setTel(customer.getTel());
        poSheet.setFax(customer.getFax());
        poSheet.setAddress(customer.getAddress());
        poSheet.setLine1(line1);
        poSheet.setLine2(line2);
        poSheet.setLine3(line3);
        poSheet.setLine4(line4);

        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo = purchaseOrderService.getPurchaseOrderMailInfo(map);
        DecimalFormat df = new DecimalFormat("#,##0");
        BigDecimal amount = new BigDecimal(orderInfo.get("totAmt").toString());
        poSheet.setHanAmount(AmountUtil.amtToKor(orderInfo.get("totAmt").toString()) + "원 정");
        poSheet.setNumAmount("(\\ " + df.format(amount.doubleValue()) + " )");
        poSheet.setTotOrderQty((BigDecimal)orderInfo.get("totOrderQty"));
        poSheet.setTotSupplyAmt((BigDecimal)orderInfo.get("totSupplyAmt"));
        poSheet.setTotVat((BigDecimal)orderInfo.get("totVat"));
        poSheet.setTotAmt(amount);

        int rowCount = 0;
        List<PurchaseOrderMailVo> poItemList = new ArrayList<>();
        for (PurchaseOrderVo.PurchaseOrderItemVo moItem: reqVo.getPurchaseOrderItemList()) {
            rowCount++;

            PurchaseOrderMailVo poItem = new PurchaseOrderMailVo();

            poItem.setItemCd(moItem.getItemCd());
            poItem.setItemName(moItem.getItemName());
            poItem.setSpec(moItem.getSpec());
            poItem.setOrderQty(moItem.getQty());
            poItem.setPrice(moItem.getInPrice());
            poItem.setSupplyAmt(moItem.getSupplyPrice());
            poItem.setVat(moItem.getVatPrice());
            poItem.setMemo(moItem.getEtc());
            poItemList.add(poItem);
        }
        while(rowCount < PO_ITEM_MAX_ROWS) {
            rowCount++;
            PurchaseOrderMailVo poItem = new PurchaseOrderMailVo();
            poItemList.add(poItem);
        }
        poSheet.setOrderItems(poItemList);
        poSheetList.add(poSheet);

        try {
            Map<String, Object> parameters = new HashMap<>();

            InputStream logoStream = getClass().getResourceAsStream("/static/images/logo1.png");
            parameters.put("logo", logoStream);

            InputStream subStream = getClass().getResourceAsStream("/report/purchase_order_sub.jrxml");
            JasperReport subReport = JasperCompileManager.compileReport(subStream);
            parameters.put("subReport", subReport);

            InputStream reportStream = getClass().getResourceAsStream("/report/purchase_order_sheet_v2.jrxml");
            byte[] pdfContent = JasperUtil.getPdfBinary(reportStream, parameters, poSheetList);

            ByteArrayDataSource bds = new ByteArrayDataSource(pdfContent, "application/pdf");
            Map<String, DataSource> files = new HashMap<>();
            files.put("발주서("+ poVo.getPurOrderDate()+"-"+poVo.getSeq()+ ").pdf", bds);

            PoSheetMailVo psVo = new PoSheetMailVo();
            psVo.setCustomerName(customer.getCustomerName());
            psVo.setOrderNo(poVo.getPurOrderDate()+"-"+poVo.getSeq());
            psVo.setOrderDate(poVo.getPurOrderDate());
            psVo.setSenderEmail(vo.getFrom());
            psVo.setMemo(vo.getMemo());

            vo.setMailId("1");
            vo.setSubject("(주)진코스텍으로부터 발주서(" + poVo.getPurOrderDate()+"-"+poVo.getSeq() + ")가 도착했습니다.");
            vo.setTemplatePath(MailTemplates.PO_SHEET);

            Map<String, Object> model = new HashMap<>();
            model.put("vo", psVo);
            String result = mailService.sendMail(vo, files, model);

            return RestResponse.okMessage(result, null);

        } catch (com.jct.mes_new.config.common.exception.MailSendException e) {
            // 메일 실패는 여기서 잡아서 실패 응답으로 내려줌
            log.error("메일 발송 실패: {}", e.getMessage(), e);
            // 프로젝트에 맞게 fail 응답 함수 사용
            return RestResponse.fail(e.getMessage(), null);
            // 만약 failMessage가 없으면 예:
            // return RestResponse.errorMessage(e.getMessage(), null);
            // 또는 throw e; (전역 예외처리로 500/400 만들기)
        } catch (Exception e) {
            log.error("메일발송 중 오류발생!", e);
            throw new Exception("메일발송 중 오류발생!", e);
        }
    }





}
