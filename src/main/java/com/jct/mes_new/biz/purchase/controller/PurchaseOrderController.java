package com.jct.mes_new.biz.purchase.controller;

import com.jct.mes_new.biz.common.service.MailService;
import com.jct.mes_new.biz.common.vo.MailVo;
import com.jct.mes_new.biz.common.vo.PoSheetMailVo;
import com.jct.mes_new.biz.purchase.mapper.PurchaseOrderMapper;
import com.jct.mes_new.biz.purchase.service.PurchaseOrderService;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import com.jct.mes_new.config.mail.MailTemplates;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
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


    @PostMapping("/orderMail")
    public RestResponse<T> orderMail(@RequestBody MailVo vo) throws Exception {
        vo.setId("1");

        List<PurchaseOrderVo> poSheetList = purchaseOrderService.getPurchaseOrderList(vo.getId());

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
            files.put("발주서().pdf", bds);

            PoSheetMailVo psVo = new PoSheetMailVo();
            psVo.setCustomerName(vo.getToName());
            psVo.setOrderNo("123456");
            psVo.setOrderDate("2026/01/19");
            psVo.setSenderEmail(vo.getFrom());
            psVo.setMemo(vo.getMemo());

            vo.setMailId("1");
            vo.setSubject("(주)진코스텍으로부터 발주서가 도착했습니다.");
            vo.setTemplatePath(MailTemplates.PO_SHEET);

            Map<String, Object> model = new HashMap<>();
            model.put("vo", psVo);

            log.info("SENDMAIL start to={}, subject={}, mailId={}, template={}",
                    vo.getTo(), vo.getSubject(), vo.getMailId(), vo.getTemplatePath());

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
