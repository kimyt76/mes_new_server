package com.jct.mes_new.config.util;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JasperUtil {

    public static byte[] getPdfBinary(String template, Collection<?> dataList ) throws Exception {
        return getPdfBinary(template, new HashMap<String, Object>(), dataList);
    }

    public static byte[] getPdfBinary(String template, Map<String, Object> parameters, Collection<?> dataList ) throws Exception {
        File file = ResourceUtils.getFile(template);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public static byte[] getPdfBinary(InputStream template, Collection<?> dataList ) throws Exception {

        return getPdfBinary(template, new HashMap<String, Object>(), dataList);
    }

    public static byte[] getPdfBinary(InputStream templateStream, Map<String, Object> parameters, Collection<?> dataList ) throws Exception {
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        //JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        JRDataSource dataSource;
        if (dataList == null || dataList.isEmpty()) {
            // 데이터가 없어도 1페이지짜리 “빈 리포트”를 만들기 위한 datasource
            dataSource = new JREmptyDataSource(1);
        } else {
            dataSource = new JRBeanCollectionDataSource(dataList);
        }

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public static JasperPrint getPrint(InputStream templateStream, Map<String, Object> parameters, Collection<?> dataList ) throws Exception {
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }

    public static HttpHeaders getHeader(String fileName, int contentsLength) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".pdf");
        header.setContentLength(contentsLength);
        return header;
    }
}
