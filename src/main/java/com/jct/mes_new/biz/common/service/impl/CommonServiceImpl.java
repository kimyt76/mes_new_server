package com.jct.mes_new.biz.common.service.impl;

import com.jct.mes_new.biz.base.service.impl.ItemServiceImpl;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.common.mapper.CommonMapper;
import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.vo.BarCodeVo;
import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.common.vo.QrCodeInfo;
import com.jct.mes_new.biz.common.vo.ReqPrinting;
import com.jct.mes_new.config.util.BarcodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {

    private final CommonMapper commonMapper;
    private final ItemServiceImpl itemServiceImpl;

    public List<CommonVo> getCommonList(CommonVo commonVo){
        return commonMapper.getCommonList(commonVo);
    }


    public List<CommonVo> getCodeList(String type){
        return commonMapper.getCodeList(type);
    }

    public String newSeq(String itemTypeCd, String cd, int seqLen){
        return commonMapper.newSeq(itemTypeCd , cd, seqLen);
    }

    public CommonVo getCommonInfo(String comId){
        return commonMapper.getCommonInfo(comId);
    }

    public String saveCommonInfo(CommonVo commonVo){
        String msg ="저장되었습니다.";

        try{
            if ( commonMapper.saveCommonInfo(commonVo) <= 0 ) {
                throw new Exception("저장에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("저장 실패 : " + e.getMessage(), e );
        }
        return msg;
    }

    public int getNextSeq(String tb, String cd, String date){
        return commonMapper.getNextSeq(tb, cd, date);
    }


    public byte[] createQrCodeLabels(ReqPrinting[] vo) throws Exception {
        List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
        for ( ReqPrinting item : vo ) {
            JasperPrint qrCodeLabel = this.createQrCodeInfo(item.getTestNo(), item.getQty());
            for ( int i = 0 ; i < item.getPrintCnt() ; i++ ) {
                jasperPrintList.add(qrCodeLabel);
            }
        }
        JRPdfExporter exporter = new JRPdfExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();
        return baos.toByteArray();
    }

    public JasperPrint createQrCodeInfo (String testNo, BigDecimal qty) throws Exception {
        QrCodeInfo qrCodeInfo = new QrCodeInfo();
        DecimalFormat df = new DecimalFormat("#,##0.000");
        DecimalFormat df2 = new DecimalFormat("#,##0");
        //ItemTestNoView itemTestNoView = itemTestNoViewService.getById(testNo);
        ItemVo itemMasterView = itemServiceImpl.getItemInfo("JRMSC00020");
        //ItemVo itemMasterView = itemServiceImpl.getItemInfo(itemTestNoView.getItemCd());
        String itemTypeCd = itemMasterView.getItemTypeCd();

        String itemCdWithBracket =  "(" + itemMasterView.getItemCd() + ")";
        qrCodeInfo.setItemCd(itemCdWithBracket);

        qrCodeInfo.setItemName(itemMasterView.getItemName());
        qrCodeInfo.setItemTypeCd(itemTypeCd);
        /* 사급 / 자급*/
        qrCodeInfo.setSupplyGb(itemMasterView.getOrderType());
        /* 보관 방법   (실온/*/
        qrCodeInfo.setItemCondition(itemMasterView.getItemConditionName());
        qrCodeInfo.setTestNo(testNo);

        //로트번호 치환 !! -> " "
        //String lotNo = (itemTestNoView.getLotNo() != null)?itemTestNoView.getLotNo().replaceAll("!!", " ") : "";
        String lotNo = "lot1";


        qrCodeInfo.setLotNo(lotNo);
        //qrCodeInfo.setProdNo(itemTestNoView.getProdNo());
        qrCodeInfo.setProdNo("제조1111");

        String unit = (itemMasterView.getUnit() == null)? "" : itemMasterView.getUnit();
        Boolean isKg = (unit.equals("kg") || unit.equals("KG") || unit.equals("Kg"));
        String strQty = (isKg) ? df.format(qty) : df2.format(qty);
        qrCodeInfo.setStrQty(strQty + " " + unit);

        qrCodeInfo.setCustomerName("거래처 테스트");
        qrCodeInfo.setCreateDate(LocalDate.parse( "2026-02-01" ));
        qrCodeInfo.setExpiryDate(LocalDate.parse( "2026-02-02" ));
        qrCodeInfo.setShelfLife(LocalDate.parse( "2026-02-03" ));
        qrCodeInfo.setPassStateName("시험대기");
        qrCodeInfo.setBarcodeImage(this.createQrCodeImage(testNo));//바코드 이미지 생성.

        if("M0".equals(itemTypeCd)) {
            qrCodeInfo.setChargingTestNo("222222222222");
        }
        List<QrCodeInfo> qrCodeInfoList = new ArrayList<>();
        qrCodeInfoList.add(qrCodeInfo);

        Map<String, Object> parameters = new HashMap<>();
        InputStream reportStream = getClass().getResourceAsStream("/report/prod_label_" + itemTypeCd + ".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(qrCodeInfoList);

        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }

    public BufferedImage createQrCodeImage (String testNo) throws Exception {
        BufferedImage result;
        try {
            result = BarcodeUtil.generateQRCodeImage(testNo);
        } catch(Exception ex) {
            result = null;
        }
        return result;
    }

}
