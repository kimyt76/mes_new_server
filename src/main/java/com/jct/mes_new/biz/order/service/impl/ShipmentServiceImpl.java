package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.base.service.CustomerService;
import com.jct.mes_new.biz.base.vo.CustomerVo;
import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.CommonVo;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.SaleMapper;
import com.jct.mes_new.biz.order.mapper.ShipmentMapper;
import com.jct.mes_new.biz.order.service.ShipmentService;
import com.jct.mes_new.biz.order.vo.*;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.biz.system.mapper.StorageMapper;
import com.jct.mes_new.biz.system.vo.StorageVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.Snowflake;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentMapper shipmentMapper;
    private final FileHandlerMapper fileHandlerMapper;
    private final TranMapper tranMapper;
    private final StorageMapper storageMapper;
    private final CustomerService customerService;

    public List<ShipmentVo> getShipmentList(ShipmentVo shipmentVo) {
        return shipmentMapper.getShipmentList(shipmentVo);
    }

    public Map<String, Object> getShipmentInfo(Long shipmentId){
        Map<String, Object> map = new HashMap<String, Object>();

        ShipmentVo shipmentInfo = shipmentMapper.getShipmentInfo(shipmentId);

        map.put("shipmentInfo", shipmentInfo);
        map.put("shipmentItemList", shipmentMapper.getShipmentItemList(shipmentId));
        map.put("attachFileInfo",  fileHandlerMapper.getAttachFileList(shipmentInfo.getAttachFileId()) );

        return map;
    }

    public List<ShipmentWorkOrderVo> getWorkOrderItemList(ShipmentWorkOrderVo vo){
        return shipmentMapper.getWorkOrderItemList(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveShipmentInfo(ShipmentRequestVo vo){
        String userId = UserUtil.getUserId();

        ShipmentVo mst = vo.getShipmentInfo();
        mst.setUserId(userId);

        //첨부파일
        if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());
            // 업로드 결과가 비정상인 경우 방어
            if (fileVoList == null || fileVoList.isEmpty() || fileVoList.get(0).getAttachFileId() == null) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
            mst.setAttachFileId(fileVoList.get(0).getAttachFileId());

            for (FileVo f : fileVoList) {
                f.setUserId(userId);
                if (!fileHandlerMapper.saveFile(f)) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }

        //출고지시마스터
        if( shipmentMapper.insertShipmentMst(mst) <=0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //출고지시상세
        for(ShipmentItemListVo itemListVo : vo.getShipmentItemList()){
            itemListVo.setUserId(userId);
            itemListVo.setShipmentId(mst.getShipmentId());

            if( shipmentMapper.insertShipmentItemList(itemListVo) <=0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        if ( "Y".equals(mst.getShipmentYn()) ){
            //완료일경우  재고에 넣어야 함
            processShipmentInventory(mst.getShipmentId(), userId);
        }

        return "저장되었습니다.";
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateShipmentInfo(ShipmentRequestVo vo)  {
        String userId = UserUtil.getUserId();
        ShipmentVo mst = vo.getShipmentInfo();

        // 1. 삭제 첨부파일 처리
        if (vo.getDeleteFiles() != null && !vo.getDeleteFiles().isEmpty()) {
            for (FileVo f : vo.getDeleteFiles()) {
                fileHandlerMapper.deleteFile(f.getAttachFileId(), f.getSeq());
            }
        }
        // 2. 신규 첨부파일 추가
        if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());

            if (fileVoList != null && !fileVoList.isEmpty()) {
                // attachFileId 없으면 새로 세팅
                if (mst.getAttachFileId() == null) {
                    mst.setAttachFileId(fileVoList.get(0).getAttachFileId());
                }

                int nextSeq = fileHandlerMapper.nextSeq(mst.getAttachFileId());

                for (FileVo f : fileVoList) {
                    f.setAttachFileId(mst.getAttachFileId());
                    f.setSeq(nextSeq++);
                    f.setUserId(userId);

                    if (!fileHandlerMapper.saveFile(f)) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
                // attachFileId가 새로 생긴 케이스면 mst에 반영 필요할 수 있음(선택)
                // (현재 updateContractInfo SQL에 attach_file_id가 업데이트에 없으면 아래 추가 필요)
                //shipmentMapper.updateAttachFileId(mst.getShipmentId(), mst.getAttachFileId());
            }
        }
        // 3. 마스터 수정
        if (shipmentMapper.updateShipmentMst(mst) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        // 4. 삭제 상세 처리
        if (vo.getDeleteShipmentItemIds() != null && !vo.getDeleteShipmentItemIds().isEmpty()) {
            for( Long id : vo.getDeleteShipmentItemIds() ){
                shipmentMapper.deleteShipmentItem(id);
            }
        }
        // 5. 상세 추가 / 수정
        if (vo.getShipmentItemList() != null) {
            for (ShipmentItemListVo item : vo.getShipmentItemList()) {
                item.setShipmentId(mst.getShipmentId());
                item.setUserId(userId);

                if (item.getShipmentItemId() == null) {
                    if (shipmentMapper.insertShipmentItemList(item) <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    if (shipmentMapper.updateShipmentItemList(item) <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }

        if ( "Y".equals(mst.getShipmentYn()) ){
            //완료일경우  재고에 넣어야 함
            processShipmentInventory(mst.getShipmentId(), userId);
        }
       return "수정되었습니다.";
    }

    private void processShipmentInventory(Long shipmentId, String userId) {

        ShipmentVo mst = shipmentMapper.getShipmentInfo(shipmentId);
        /*
         * 출고 상세 조회
         */
        List<ShipmentItemListVo> itemList = shipmentMapper.getShipmentItemList(shipmentId);

        if (itemList == null || itemList.isEmpty()) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        for ( ShipmentItemListVo item : itemList ) {
            //재고 마스터
            TranVo tranMst = new TranVo();

            String shipmentStatus = mst.getShipmentStatus();
            String tranTypeCd = shipmentStatus == null ? null :
                    switch (shipmentStatus) {
                        case "SA", "MS", "FC" -> "F";
                        case "MV", "FS"       -> "I";
                        case "PN"             -> "P";
                        default               -> null;
                    };
            String tarStorageCd = shipmentStatus == null ? null :
                switch (mst.getShipmentStatus()) {
                case "SA", "MS", "FC" -> "WC001";
                case "MV", "FS"       -> "WC002";
                default               -> null;
            };

            StorageVo storageVo =  storageMapper.getStorageInfo(item.getStorageCd());

            tranMst.setTranTypeCd(tranTypeCd); // 출고 코드
            tranMst.setTranDate(LocalDate.now());
            tranMst.setCustomerCd(mst.getClientId());
            tranMst.setSrcStorageCd(item.getStorageCd());
            tranMst.setTarStorageCd(tarStorageCd);
            tranMst.setAreaCd(storageVo.getAreaCd());
            tranMst.setTranStatus("C");
            tranMst.setEndYn("Y");
            tranMst.setUserId(userId);

            // 필요하면 출고번호 연결용 정보
//            // tranMst.setRemark("출고번호 : " + shipmentId);
            if (tranMapper.insertTranMst(tranMst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }

            //재고 품목
            Long tranId = tranMst.getTranId();
            /*
             * 재고 transaction 상세
             */
            TranItemVo tranItem = new TranItemVo();

            tranItem.setTranId(tranId);
            tranItem.setItemCd(item.getItemCd());
            tranItem.setItemName(item.getItemName());
            tranItem.setItemTypeCd(item.getItemTypeCd());
            tranItem.setQty(item.getQty());
            tranItem.setLotNo(item.getLotNo());
            tranItem.setMakeNo(item.getMakeNo());
            tranItem.setTestNo(item.getTestNo());
            tranItem.setQcStatus(item.getQcStatus());
            tranItem.setInYn("Y");
            tranItem.setUserId(userId);

            if (tranMapper.insertTranItem(tranItem) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }

            item.setTranId(tranId);
            item.setUserId(userId);
            shipmentMapper.updateShipmentItemByTranId(item);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String deleteShipment(ShipmentRequestVo vo) {
        List<Long> deleteShipmentIds = vo.getDeleteShipmentItemIds();

        //item
        shipmentMapper.deleteShipmentItemList(deleteShipmentIds);
        //mst
        shipmentMapper.deleteShipmentMst(deleteShipmentIds);

        return "삭제되었습니다.";
    }


    public byte[] printTransactionStatement(Long shipmentId) {
        // =========================================================
        // 1. DB 조회 - 본사정보
        // =========================================================
        String customerCd = "1348668063";
        CustomerVo master = customerService.getCustomerInfo(customerCd);

        // =========================================================
        // 2. DB 조회 - 출고 마스터
        // =========================================================
        ShipmentVo shipmentMst = shipmentMapper.getShipmentInfo(shipmentId);

        if (shipmentMst == null) {
            throw new IllegalArgumentException(
                    "거래명세서 데이터가 없습니다. shipmentId=" + shipmentId
            );
        }
        // =========================================================
        // 3. DB 조회 - 출고 품목
        // =========================================================
        List<ShipmentItemListVo> items = shipmentMapper.getShipmentItemList(shipmentId);

        if (items == null) {
            items = new ArrayList<>();
        }
        // =========================================================
        // 4. 수량 합계
        // =========================================================
        BigDecimal totalQty = items.stream()
                .map(ShipmentItemListVo::getQty)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================================================
        // 5. 파렛트 합계
        // =========================================================
        BigDecimal palletTotal = items.stream()
                .map(ShipmentItemListVo::getPallet)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================================================
        // 5. 출고 요청일자
        // =========================================================
        String shipmentReqDate = "";

        if (shipmentMst.getShipmentReqDate() != null) {
            shipmentReqDate =
                    shipmentMst.getShipmentReqDate().toString();
        }

        // 원본 리스트에 출고 요청일자 세팅
        for (ShipmentItemListVo item : items) {
            item.setShipmentReqDate(shipmentReqDate);
        }
        // =========================================================
        // 6. Jasper 출력용 리스트 생성
        //
        // 한 명세서 = 10행
        // 한 페이지 = 동일 명세서 2부
        //
        // copyNo = 1 : 상단
        // copyNo = 2 : 하단
        // pageNo = 실제 A4 페이지 번호
        // rowId  = 명세서 내부 순번 1 ~ 10
        // =========================================================
        final int ROWS_PER_COPY = 10;

        List<ShipmentItemListVo> printItems = new ArrayList<>();

        int totalPage =
                (int) Math.ceil(
                        (double) items.size() / ROWS_PER_COPY
                );

        // 데이터가 없더라도 거래명세서 1페이지 출력
        if (totalPage == 0) {
            totalPage = 1;
        }

        for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
            int startIndex =
                    (pageNo - 1) * ROWS_PER_COPY;

            int endIndex =
                    Math.min(
                            startIndex + ROWS_PER_COPY,
                            items.size()
                    );

            // =====================================================
            // 현재 페이지의 원본 10행 생성
            // =====================================================
            List<ShipmentItemListVo> pageItems =
                    new ArrayList<>();

            // 실제 데이터
            for (int i = startIndex; i < endIndex; i++) {

                ShipmentItemListVo source = items.get(i);

                ShipmentItemListVo row =
                        new ShipmentItemListVo();

                // -------------------------------------------------
                // 실제 VO 필드 복사
                // -------------------------------------------------
                row.setShipmentReqDate(
                        source.getShipmentReqDate()
                );

                row.setItemName(
                        source.getItemName()
                );

                row.setQty(
                        source.getQty()
                );

                row.setPallet(
                        source.getPallet()
                );

                row.setLotNo(
                        source.getLotNo()
                );

                // VO에 spec, etc 등이 있다면 필요시 추가
                // row.setSpec(source.getSpec());
                // row.setEtc(source.getEtc());

                pageItems.add(row);
            }

            // =====================================================
            // 10행보다 부족하면 빈행 추가
            // =====================================================
            while (pageItems.size() < ROWS_PER_COPY) {

                ShipmentItemListVo emptyRow =
                        new ShipmentItemListVo();

                pageItems.add(emptyRow);
            }

            // =====================================================
            // 상단 명세서
            // copyNo = 1
            // =====================================================
            for (int i = 0; i < ROWS_PER_COPY; i++) {

                ShipmentItemListVo source =
                        pageItems.get(i);

                ShipmentItemListVo row =
                        new ShipmentItemListVo();

                row.setRowId(i + 1);
                row.setPageNo(pageNo);
                row.setCopyNo(1);

                row.setShipmentReqDate(
                        source.getShipmentReqDate()
                );

                row.setItemName(
                        source.getItemName()
                );

                row.setQty(
                        source.getQty()
                );

                row.setPallet(
                        source.getPallet()
                );

                row.setLotNo(
                        source.getLotNo()
                );

                // 필요시 추가
                // row.setSpec(source.getSpec());
                // row.setEtc(source.getEtc());
                printItems.add(row);
            }

            // =====================================================
            // 하단 명세서
            // copyNo = 2
            // =====================================================
            for (int i = 0; i < ROWS_PER_COPY; i++) {

                ShipmentItemListVo source =
                        pageItems.get(i);

                ShipmentItemListVo row =
                        new ShipmentItemListVo();

                row.setRowId(i + 1);
                row.setPageNo(pageNo);
                row.setCopyNo(2);

                row.setShipmentReqDate(
                        source.getShipmentReqDate()
                );

                row.setItemName(
                        source.getItemName()
                );

                row.setQty(
                        source.getQty()
                );

                row.setPallet(
                        source.getPallet()
                );

                row.setLotNo(
                        source.getLotNo()
                );

                // 필요시 추가
                // row.setSpec(source.getSpec());
                // row.setEtc(source.getEtc());

                printItems.add(row);
            }
        }

        // =========================================================
        // 7. JRXML 파라미터
        // =========================================================
        Map<String, Object> params = new HashMap<>();

        // ---------------------------------------------------------
        // 상단 - 거래정보
        // ---------------------------------------------------------
        params.put(
                "poNo",
                nvl(shipmentMst.getShipmentDateSeq())
        );

        // ---------------------------------------------------------
        // 상단 - 공급자(본사) 정보
        // ---------------------------------------------------------
        params.put("companyName", nvl(master.getCustomerName()));
        params.put("bizNo", nvl(master.getBusinessNo()));
        params.put("ceoName", nvl(master.getPresident()));
        params.put("bizType", "");
        params.put("bizItem", "");
        params.put("address", nvl(master.getAddress()));
        params.put("tel", nvl(master.getTel()));

        // ---------------------------------------------------------
        // 고객사명
        // ---------------------------------------------------------
        params.put("customerName",nvl(shipmentMst.getClientName()));

        // =========================================================
        // 8. 하단 - 총수량
        // =========================================================
        params.put("totalQty", totalQty );
        params.put("totalPallet", palletTotal);
        params.put("totalLoadQty", shipmentMst.getLotCnt() );

        // =========================================================
        // 11. 하단 - 납품주소
        // =========================================================
        String deliveryAddress =(nvl(shipmentMst.getDeliveryLocation())+ " " + nvl(shipmentMst.getDeliveryAddress()) ).trim();

        params.put("deliveryAddress", nvl(deliveryAddress) );
        // =========================================================
        // 12. 하단 - 납품 담당자
        //
        // ShipmentVo 실제 필드명에 맞게 변경
        // 현재 필드가 없다면 빈값
        // =========================================================
        params.put("deliveryManager", nvl(shipmentMst.getDeliveryManagerName()) );
        // 예:
        // params.put(
        //         "deliveryManager",
        //         nvl(shipmentMst.getDeliveryManagerName())
        // );

        // =========================================================
        // 13. 하단 - 연락처
        // =========================================================
        params.put("deliveryContact",  nvl(shipmentMst.getDeliveryTelno()) );
        // 예:
        // params.put(
        //         "deliveryContact",
        //         nvl(shipmentMst.getDeliveryTel())
        // );

        // =========================================================
        // 14. 하단 - 특이사항
        // =========================================================
        params.put("etc", nvl(shipmentMst.getEtc()) );

        // =========================================================
        // 15. 회사 인장
        // src/main/resources/static/images/company_seal.png
        // =========================================================
        ClassPathResource sealResource = new ClassPathResource("static/images/company_seal.png");

        try {
            if (sealResource.exists()) {
                params.put("sealImagePath", sealResource.getURL().toString() );
            } else {
                params.put("sealImagePath", null);
            }
            // =====================================================
            // 16. JRXML 파일
            // =====================================================
            ClassPathResource jrxmlResource = new ClassPathResource("report/TransactionStatement.jrxml");

            try (
                    InputStream jrxml = jrxmlResource.getInputStream()
                ) {

                // =================================================
                // 17. JRXML 컴파일
                // =================================================
                JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);

                // =================================================
                // 18. Jasper Detail DataSource
                //
                // 중요:
                // 기존 items가 아니라 printItems를 넘김
                // =================================================
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(printItems);

                // =================================================
                // 19. Jasper Report 생성
                // =================================================
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

                // =================================================
                // 20. 출력 여부 업데이트
                // =================================================
                shipmentMapper.updatePrintYn(shipmentId);

                // =================================================
                // 21. PDF byte[] 반환
                // =================================================
                return JasperExportManager.exportReportToPdf(jasperPrint);
            }
        } catch (Exception e) {
            throw new RuntimeException("거래명세서 PDF 생성 중 오류가 발생했습니다.", e);
        }
    }




    private String nvl(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        return new BigDecimal(String.valueOf(value));
    }


    public byte[] downloadShipmentRecord(Long shipmentId) {

        // 마스터 데이터
        ShipmentVo mst = shipmentMapper.getShipmentInfo(shipmentId);

        // 리스트 데이터
        List<ShipmentItemListVo> shipmentItemList =
                shipmentMapper.getShipmentItemList(shipmentId);

        final int PAGE_ROW_COUNT = 30;  // 페이지당 30행
        final int LIST_START_ROW = 20;  // 엑셀 21행 (0부터 시작)
        final int LIST_MAX_COUNT = 5;   // 페이지당 5건

        int size = shipmentItemList.size();

        // 데이터가 없어도 기본 1페이지 출력
        int pageCount = Math.max(
                1, (size + LIST_MAX_COUNT - 1) / LIST_MAX_COUNT
        );

        try (
                InputStream excelStream = getClass().getResourceAsStream(
                        "/excel/shipment_report.xlsx"
                );
                Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream)
        ) {

            Sheet sheet = workbook.getSheet("Sheet1");

            // =====================================================
            // 1. 기본 템플릿 복사
            // =====================================================
            for (int page = 1; page < pageCount; page++) {

                int rowOffset = page * PAGE_ROW_COUNT;

                // 행 복사
                for (int r = 0; r < PAGE_ROW_COUNT; r++) {

                    Row sourceRow = sheet.getRow(r);

                    if (sourceRow == null) {
                        continue;
                    }

                    Row targetRow = sheet.createRow(rowOffset + r);
                    targetRow.setHeight(sourceRow.getHeight());

                    for (int c = 0; c < sourceRow.getLastCellNum(); c++) {

                        Cell sourceCell = sourceRow.getCell(c);

                        if (sourceCell == null) {
                            continue;
                        }

                        Cell targetCell = targetRow.createCell(c);

                        // 기존 셀 스타일 유지
                        targetCell.setCellStyle(sourceCell.getCellStyle());

                        // 셀 타입별 값 복사
                        switch (sourceCell.getCellType()) {

                            case STRING:
                                targetCell.setCellValue(
                                        sourceCell.getStringCellValue()
                                );
                                break;

                            case NUMERIC:
                                targetCell.setCellValue(
                                        sourceCell.getNumericCellValue()
                                );
                                break;

                            case BOOLEAN:
                                targetCell.setCellValue(
                                        sourceCell.getBooleanCellValue()
                                );
                                break;

                            case FORMULA:
                                targetCell.setCellFormula(
                                        sourceCell.getCellFormula()
                                );
                                break;

                            default:
                                break;
                        }
                    }
                }

                // =====================================================
                // 2. 병합 셀 복사
                // =====================================================
                int mergedCount = sheet.getNumMergedRegions();

                List<CellRangeAddress> mergedRegions = new ArrayList<>();

                for (int i = 0; i < mergedCount; i++) {

                    CellRangeAddress region = sheet.getMergedRegion(i);

                    if (region.getFirstRow() >= PAGE_ROW_COUNT) {
                        continue;
                    }

                    mergedRegions.add(
                            new CellRangeAddress(
                                    region.getFirstRow() + rowOffset,
                                    region.getLastRow() + rowOffset,
                                    region.getFirstColumn(),
                                    region.getLastColumn()
                            )
                    );
                }

                for (CellRangeAddress region : mergedRegions) {
                    sheet.addMergedRegion(region);
                }

                // 페이지 나누기
                sheet.setRowBreak(rowOffset - 1);
            }

            // =====================================================
            // 3. 셀 스타일 생성
            // =====================================================
            DataFormat dataFormat = workbook.createDataFormat();

            // 납품지 주소 : 왼쪽 정렬
            CellStyle addressStyle = createAlignmentStyle(
                    workbook,
                    getOrCreateCell(sheet, 7, 4).getCellStyle(),
                    HorizontalAlignment.LEFT
            );

            // 제품명 : 왼쪽 정렬
            CellStyle itemNameStyle = createAlignmentStyle(
                    workbook,
                    getOrCreateCell(sheet, LIST_START_ROW, 0).getCellStyle(),
                    HorizontalAlignment.LEFT
            );

            // 로트번호 : 가운데 정렬
            CellStyle lotNoStyle = createAlignmentStyle(
                    workbook,
                    getOrCreateCell(sheet, LIST_START_ROW, 10).getCellStyle(),
                    HorizontalAlignment.CENTER
            );

            // 수량 : 오른쪽 정렬
            CellStyle baseQtyStyle = createAlignmentStyle(
                    workbook,
                    getOrCreateCell(sheet, LIST_START_ROW, 16).getCellStyle(),
                    HorizontalAlignment.RIGHT
            );

            // 정수용 스타일
            CellStyle qtyIntStyle = workbook.createCellStyle();
            qtyIntStyle.cloneStyleFrom(baseQtyStyle);
            qtyIntStyle.setDataFormat(
                    dataFormat.getFormat("#,##0")
            );

            // 소수 자릿수별 스타일 캐시
            Map<Integer, CellStyle> qtyDecimalStyles = new HashMap<>();

            // =====================================================
            // 4. 페이지별 데이터 입력
            // =====================================================
            for (int page = 0; page < pageCount; page++) {

                int rowOffset = page * PAGE_ROW_COUNT;

                // -------------------------------------------------
                // 4-1. 마스터 데이터 (모든 페이지 동일)
                // -------------------------------------------------

                // 거래처명
                setCellValue(
                        sheet, rowOffset + 6, 4,
                        mst.getClientName()
                );

                // 출하요청일
                setCellValue(
                        sheet, rowOffset + 6, 15,
                        mst.getShipmentReqDate()
                );

                // 납품지 주소 - 왼쪽 정렬
                Cell addressCell = getOrCreateCell(
                        sheet, rowOffset + 7, 4
                );

                addressCell.setCellValue(
                        mst.getDeliveryAddress() == null
                                ? ""
                                : mst.getDeliveryAddress().toString()
                );

                addressCell.setCellStyle(addressStyle);

                // -------------------------------------------------
                // 4-2. 제품 리스트 (페이지당 최대 5건)
                // -------------------------------------------------
                int startIndex = page * LIST_MAX_COUNT;

                int endIndex = Math.min(
                        startIndex + LIST_MAX_COUNT,
                        size
                );

                for (int i = startIndex; i < endIndex; i++) {

                    ShipmentItemListVo item = shipmentItemList.get(i);

                    int listRow = rowOffset
                            + LIST_START_ROW
                            + (i - startIndex);

                    // =============================================
                    // 제품명 A열 - 왼쪽 정렬
                    // =============================================
                    Cell itemNameCell = getOrCreateCell(
                            sheet, listRow, 0
                    );

                    itemNameCell.setCellValue(
                            item.getItemName() == null
                                    ? ""
                                    : item.getItemName().toString()
                    );

                    itemNameCell.setCellStyle(itemNameStyle);

                    // =============================================
                    // 로트번호 K열 - 가운데 정렬
                    // 특수문자는 공백 처리 (_ 제외)
                    // =============================================
                    Cell lotNoCell = getOrCreateCell(
                            sheet, listRow, 10
                    );

                    String lotNo = item.getLotNo() == null
                            ? ""
                            : item.getLotNo().toString();

                    // 한글, 영문, 숫자, 언더바, 공백 유지
                    // 나머지 특수문자는 공백으로 변경
                    lotNo = lotNo.replaceAll(
                            "[^가-힣a-zA-Z0-9_\\s]", " "
                    );

                    lotNoCell.setCellValue(lotNo);
                    lotNoCell.setCellStyle(lotNoStyle);

                    // =============================================
                    // 수량 Q열 - 오른쪽 정렬
                    // 정수 끝에 . 표시 방지
                    // 숫자 타입 유지
                    // =============================================
                    Cell qtyCell = getOrCreateCell(
                            sheet, listRow, 16
                    );

                    if (item.getQty() != null) {

                        BigDecimal qty = new BigDecimal(
                                item.getQty().toString()
                        );

                        // 불필요한 소수점 이하 0 제거
                        BigDecimal normalizedQty =
                                qty.stripTrailingZeros();

                        // 실제 숫자 값으로 저장
                        qtyCell.setCellValue(qty.doubleValue());

                        int scale = Math.max(
                                0, normalizedQty.scale()
                        );

                        if (scale == 0) {

                            // 정수: 1,000 / 8,576
                            qtyCell.setCellStyle(qtyIntStyle);

                        } else {

                            // 소수 자릿수에 맞는 서식 생성
                            CellStyle decimalStyle =
                                    qtyDecimalStyles.get(scale);

                            if (decimalStyle == null) {

                                decimalStyle = workbook.createCellStyle();
                                decimalStyle.cloneStyleFrom(baseQtyStyle);

                                String format =
                                        "#,##0." + "0".repeat(scale);

                                decimalStyle.setDataFormat(
                                        dataFormat.getFormat(format)
                                );

                                qtyDecimalStyles.put(
                                        scale, decimalStyle
                                );
                            }

                            qtyCell.setCellStyle(decimalStyle);
                        }

                    } else {

                        qtyCell.setBlank();
                        qtyCell.setCellStyle(qtyIntStyle);
                    }

                    // =============================================
                    // 출하검사결과 T열 - 기존 유지
                    // =============================================
                    setCellValue(
                            sheet, listRow, 19,
                            item.getQcStatusName()
                    );
                }
            }

            // =====================================================
            // 5. 인쇄 설정
            // =====================================================
            sheet.setFitToPage(true);

            PrintSetup printSetup = sheet.getPrintSetup();

            printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
            printSetup.setFitWidth((short) 1);
            printSetup.setFitHeight((short) 0);

            sheet.setHorizontallyCenter(true);

            // 인쇄 영역 A1:W(마지막 페이지)
            workbook.setPrintArea(
                    workbook.getSheetIndex(sheet),
                    0,
                    22,
                    0,
                    pageCount * PAGE_ROW_COUNT - 1
            );

            // =====================================================
            // 6. byte[] 변환
            // =====================================================
            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception ex) {

            ex.printStackTrace();

            throw new BusinessException(
                    "엑셀파일 생성중 에러발생!"
            );
        }
    }


    // =====================================================
    // 셀 스타일 생성 (기존 서식 유지 + 정렬 변경)
    // =====================================================
    private CellStyle createAlignmentStyle(Workbook workbook, CellStyle baseStyle, HorizontalAlignment alignment) {
        CellStyle style = workbook.createCellStyle();
        // 기존 글꼴, 테두리, 배경색 등 유지
        style.cloneStyleFrom(baseStyle);
        // 가로 정렬만 변경
        style.setAlignment(alignment);

        return style;
    }

    // =====================================================
    // 셀 생성 및 값 설정
    // =====================================================
    private void setCellValue(Sheet sheet, int rowIndex, int colIndex, Object value) {
        Cell cell = getOrCreateCell(
                sheet,
                rowIndex,
                colIndex
        );

        cell.setCellValue(value == null ? "" : value.toString());
    }

    // =====================================================
    // 셀 가져오기 (없으면 생성)
    // =====================================================
    private Cell getOrCreateCell(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);

        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        Cell cell = row.getCell(colIndex);

        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        return cell;
    }





}


