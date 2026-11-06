package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.stock.mapper.StockMapper;
import com.jct.mes_new.biz.stock.service.StockService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.biz.system.mapper.StorageMapper;
import com.jct.mes_new.biz.system.vo.StorageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockServiceImpl implements StockService {

    private final StockMapper stockMapper;
    private final StorageMapper storageMapper;

    public List<UseByVo> getUseByM2List(UseByVo vo) { return stockMapper.getUseByM2List(vo); }

    public Map<String, Object> getUseByM1List(UseByVo vo){
        StorageVo storageVo = new StorageVo();
        storageVo.setAreaCd(vo.getAreaCd());
        // 창고 목록
        List<StorageVo> storageList = storageMapper.getStorageList(storageVo);
        // 사용기한 조회
        List<UseByVo> useByList = stockMapper.getUseByM1List(vo);
        Map<String, UseByVo> pivotMap = new LinkedHashMap<>();
        // 피벗 생성
        for (UseByVo row : useByList) {
            String mapKey = row.getItemCd()+ "_"+ row.getTestNo();
            UseByVo pivot = pivotMap.computeIfAbsent(mapKey, key -> {

                UseByVo newVo = new UseByVo();

                newVo.setItemCd(row.getItemCd());
                newVo.setItemName(row.getItemName());
                newVo.setTestNo(row.getTestNo());
                newVo.setExpiryDate(row.getExpiryDate());
                newVo.setRemainingDay(row.getRemainingDay());
                newVo.setTotQty(BigDecimal.ZERO);
                newVo.setStorageQtyMap(new LinkedHashMap<>());

                return newVo;
            });

            BigDecimal qty = nvl(row.getQty());
            pivot.getStorageQtyMap().put(row.getStorageCd(),qty);
            pivot.setTotQty(nvl(pivot.getTotQty()).add(qty));
        }
        // 창고별 세로 합계
        Map<String, BigDecimal> storageTotalMap = new LinkedHashMap<>();

        for (StorageVo storage : storageList) {
            storageTotalMap.put(storage.getStorageCd(),BigDecimal.ZERO);
        }
        for (UseByVo useByVo : pivotMap.values()) {
            for (Map.Entry<String, BigDecimal> entry :
                    useByVo.getStorageQtyMap().entrySet()) {
                String storageCd = entry.getKey();
                BigDecimal qty = nvl(entry.getValue());
                storageTotalMap.put(storageCd,nvl(storageTotalMap.get(storageCd)).add(qty));
            }
        }
        // 합계 0인 창고 제거
        List<StorageVo> filteredStorageList =
                storageList.stream()
                        .filter(storage -> {
                            BigDecimal total =
                                    nvl(storageTotalMap.get(storage.getStorageCd()));

                            return total.compareTo(BigDecimal.ZERO) != 0;
                        })
                        .toList();
        // 동적 컬럼
        List<Map<String, String>> dynamicColumns = new ArrayList<>();

        for (StorageVo storage : filteredStorageList) {
            Map<String, String> column = new LinkedHashMap<>();

            column.put("field", storage.getStorageCd());
            column.put("header", storage.getStorageName());

            dynamicColumns.add(column);
        }

        // 데이터 생성
        List<Map<String, Object>> rows = new ArrayList<>();

        for (UseByVo useByVo : pivotMap.values()) {
            // 총수량 0 제외
            if (nvl(useByVo.getTotQty())
                    .compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("itemCd", useByVo.getItemCd());
            row.put("itemName", useByVo.getItemName());
            row.put("testNo", useByVo.getTestNo());
            row.put("expiryDate", useByVo.getExpiryDate());
            row.put("remainingDay", useByVo.getRemainingDay());

            row.put("totQty", nvl(useByVo.getTotQty()));

            for (StorageVo storage : filteredStorageList) {
                String storageCd = storage.getStorageCd();
                row.put(storageCd, nvl(useByVo.getStorageQtyMap().get(storageCd)));
            }
            rows.add(row);
        }
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("dynamicColumns", dynamicColumns);
        result.put("rows", rows);

        return result;

    }


    public Map<String, Object> getStockItemList(StockVo vo) {

        Map<String, StockVo> pivotMap = new LinkedHashMap<>();

        // 1. 창고 목록 조회
        StorageVo storageVo = new StorageVo();

        // 전체 조회일 경우 공장 조건 제거
        String areaCd = vo.getAreaCd();

        storageVo.setAreaCd(
                areaCd == null || "ALL".equals(areaCd)
                        ? null
                        : areaCd
        );

        List<StorageVo> storageList =
                storageMapper.getStorageList(storageVo);

        // =====================================================
        // 2. 창고 표시 순서 정렬
        //    A001 : 시흥공장
        //    A002 : 안산공장
        //    기타 : 나머지 창고
        // =====================================================
        storageList.sort(
                Comparator
                        .comparingInt((StorageVo storage) -> {

                            String cd = storage.getAreaCd();

                            if ("A001".equals(cd)) {
                                return 1;
                            }

                            if ("A002".equals(cd)) {
                                return 2;
                            }

                            return 3;
                        })
                        .thenComparing(
                                StorageVo::getStorageCd,
                                Comparator.nullsLast(
                                        String::compareTo
                                )
                        )
        );

        // 3. 창고별 재고 수량 조회
        //    재고 + 발주입고예정 + 안전재고 통합 SQL
        List<StockVo> stockList =
                stockMapper.getStockItemList(vo);

        boolean isTest = "TEST".equals(vo.getType());

        // 4. 품목별 / 시험번호별 피벗 데이터 생성
        for (StockVo row : stockList) {

            String itemCd = row.getItemCd() == null
                    ? ""
                    : row.getItemCd().trim();

            String testNo = row.getTestNo() == null
                    ? ""
                    : row.getTestNo().trim();

            String storageCd = row.getStorageCd();

            // ITEM : 품목코드별
            // TEST : 품목코드 + 시험번호별
            String mapKey = isTest
                    ? itemCd + "\u001F" + testNo
                    : itemCd;

            StockVo pivot = pivotMap.computeIfAbsent(
                    mapKey,
                    key -> {

                        StockVo stockVo = new StockVo();

                        stockVo.setItemCd(row.getItemCd());
                        stockVo.setItemName(row.getItemName());
                        stockVo.setItemTypeCd(row.getItemTypeCd());

                        stockVo.setTestNo(
                                isTest ? row.getTestNo() : null
                        );

                        // 통합 SQL에서 조회한 값
                        stockVo.setInReQty(
                                nvl(row.getInReQty())
                        );

                        stockVo.setSafeStockQty(
                                nvl(row.getSafeStockQty())
                        );

                        stockVo.setTotQty(BigDecimal.ZERO);

                        stockVo.setStorageQtyMap(
                                new LinkedHashMap<>()
                        );

                        return stockVo;
                    }
            );

            // 5. 창고별 재고 수량 누적
            BigDecimal qty = nvl(row.getQty());

            if (storageCd != null
                    && !storageCd.trim().isEmpty()) {

                pivot.getStorageQtyMap().merge(
                        storageCd.trim(),
                        qty,
                        BigDecimal::add
                );
            }

            // 6. 전체 재고 합계
            pivot.setTotQty(
                    nvl(pivot.getTotQty()).add(qty)
            );
        }

        // =====================================================
        // 7. 실제 재고가 존재하는 창고만 표시
        //    정렬된 storageList를 사용하므로 순서 유지
        // =====================================================
        List<StorageVo> filteredStorageList =
                storageList.stream()
                        .filter(storage -> {

                            String storageCd = storage.getStorageCd();

                            if (storageCd == null) {
                                return false;
                            }

                            return pivotMap.values().stream()
                                    .anyMatch(stockVo -> {

                                        BigDecimal qty = nvl(
                                                stockVo.getStorageQtyMap()
                                                        .get(storageCd.trim())
                                        );

                                        return qty.compareTo(
                                                BigDecimal.ZERO
                                        ) != 0;
                                    });
                        })
                        .collect(Collectors.toList());

        // =====================================================
        // 8. 프론트 동적 창고 컬럼 생성
        //    시흥 -> 안산 -> 기타 순서 적용
        // =====================================================
        List<Map<String, String>> dynamicColumns =
                new ArrayList<>();

        for (StorageVo storage : filteredStorageList) {

            Map<String, String> col =
                    new LinkedHashMap<>();

            col.put(
                    "field",
                    storage.getStorageCd().trim()
            );

            col.put(
                    "header",
                    storage.getStorageName()
            );

            dynamicColumns.add(col);
        }

        // 9. Vue DataTable에서 사용할 rows 생성
        List<Map<String, Object>> rows =
                new ArrayList<>();

        for (StockVo stockVo : pivotMap.values()) {

            Map<String, Object> rowMap =
                    new LinkedHashMap<>();

            // 기본 품목 정보
            rowMap.put("itemCd", stockVo.getItemCd());
            rowMap.put("itemName", stockVo.getItemName());
            rowMap.put("itemTypeCd", stockVo.getItemTypeCd());
            rowMap.put("testNo", stockVo.getTestNo());

            // 발주 입고예정 / 안전재고
            rowMap.put(
                    "inReQty",
                    nvl(stockVo.getInReQty())
            );

            rowMap.put(
                    "safeStockQty",
                    nvl(stockVo.getSafeStockQty())
            );

            // 창고별 재고
            for (StorageVo storage : filteredStorageList) {

                String storageCd =
                        storage.getStorageCd().trim();

                BigDecimal qty = nvl(
                        stockVo.getStorageQtyMap()
                                .get(storageCd)
                );

                rowMap.put(storageCd, qty);
            }

            // 전체 재고 합계
            rowMap.put(
                    "totQty",
                    nvl(stockVo.getTotQty())
            );

            rows.add(rowMap);
        }

        // 10. 최종 반환
        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("dynamicColumns", dynamicColumns);
        result.put("rows", rows);

        return result;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }





    public StockHistResponseVo getStockItemHistList(StockVo vo) {

        StockHistResponseVo response = new StockHistResponseVo();

        // 1. 공장별 창고 조회
        List<Map<String, Object>> storageList =
                storageMapper.getAreaStorageList(vo.getAreaCd());

        if (storageList == null || storageList.isEmpty()) {
            response.setStorageList(Collections.emptyList());
            response.setStockItemHistList(Collections.emptyList());
            return response;
        }

        // 2. 창고코드 목록
        List<String> storageCodes = storageList.stream()
                .map(s -> normalizeKey(s.get("storageCd")))
                .collect(Collectors.toList());

        Set<String> storageCodeSet = new LinkedHashSet<>(storageCodes);

        // 3. 재고 조회 파라미터
        Map<String, Object> param = new HashMap<>();
        param.put("itemCd", vo.getItemCd());
        param.put("areaCd", vo.getAreaCd());
        param.put("storageCodes", storageCodes);

        // 4. 재고 조회 (세로 데이터)
        List<Map<String, Object>> stockList =
                stockMapper.getStockList(param);

        if (stockList == null || stockList.isEmpty()) {
            response.setStorageList(Collections.emptyList());
            response.setStockItemHistList(Collections.emptyList());
            return response;
        }

        // 5. 시험번호별 가로 데이터 변환
        Map<String, Map<String, Object>> stockMap = new LinkedHashMap<>();

        for (Map<String, Object> row : stockList) {

            String itemCd = String.valueOf(
                    findValueIgnoreCase(row, "itemCd")
            );

            String testNo = String.valueOf(
                    findValueIgnoreCase(row, "testNo")
            );

            String storageCd = normalizeKey(
                    findValueIgnoreCase(row, "storageCd")
            );

            BigDecimal qty = toBigDecimal(
                    findValueIgnoreCase(row, "qty")
            );

            // 창고 마스터에 없는 창고는 제외
            if (!storageCodeSet.contains(storageCd)) {
                continue;
            }

            // 품목 + 시험번호 기준으로 묶기
            String key = itemCd + "|" + testNo;

            Map<String, Object> dest = stockMap.get(key);

            if (dest == null) {

                dest = new LinkedHashMap<>();

                dest.put("itemCd", itemCd);
                dest.put("testNo", testNo);
                dest.put("inDate", findValueIgnoreCase(row, "inDate"));
                dest.put("expiryDate", findValueIgnoreCase(row, "expiryDate"));
                dest.put("totQty", BigDecimal.ZERO);

                // 창고별 수량 초기화
                for (String cd : storageCodeSet) {
                    dest.put(cd, BigDecimal.ZERO);
                }

                stockMap.put(key, dest);
            }

            // 6. 창고별 수량 누적
            BigDecimal oldQty = toBigDecimal(dest.get(storageCd));

            dest.put(storageCd, oldQty.add(qty));

            // 7. 시험번호별 총재고 누적
            BigDecimal oldTotal = toBigDecimal(dest.get("totQty"));

            dest.put("totQty", oldTotal.add(qty));
        }

        // 8. 시험번호별 총재고가 양수인 데이터만 유지
        List<Map<String, Object>> validRows = stockMap.values()
                .stream()
                .filter(row ->
                        toBigDecimal(row.get("totQty"))
                                .compareTo(BigDecimal.ZERO) > 0
                )
                .collect(Collectors.toList());

        // 9. 실제 재고가 존재하는 창고 추출
        // 서로 다른 시험번호의 양수/음수가 상쇄되어도
        // 개별 시험번호에 재고가 있으면 창고 표시
        Set<String> activeStorageSet = new HashSet<>();

        for (Map<String, Object> row : validRows) {

            for (String storageCd : storageCodeSet) {

                BigDecimal qty = toBigDecimal(row.get(storageCd));

                if (qty.compareTo(BigDecimal.ZERO) != 0) {
                    activeStorageSet.add(storageCd);
                }
            }
        }

        // 10. 표시할 창고만 유지
        List<Map<String, Object>> filteredStorageList = storageList.stream()
                .filter(storage ->
                        activeStorageSet.contains(
                                normalizeKey(storage.get("storageCd"))
                        )
                )
                .collect(Collectors.toList());

        // 11. 최종 가로 데이터 구성
        List<Map<String, Object>> filteredStockList = new ArrayList<>();

        int no = 1;

        for (Map<String, Object> src : validRows) {

            Map<String, Object> dest = new LinkedHashMap<>();

            dest.put("no", no++);
            dest.put("itemCd", src.get("itemCd"));
            dest.put("testNo", src.get("testNo"));
            dest.put("inDate", src.get("inDate"));
            dest.put("expiryDate", src.get("expiryDate"));
            dest.put("totQty", src.get("totQty"));

            for (Map<String, Object> storage : filteredStorageList) {

                String storageCd = normalizeKey(storage.get("storageCd"));

                dest.put(storageCd, src.get(storageCd));
            }

            filteredStockList.add(dest);
        }

        // 12. 응답
        response.setStorageList(filteredStorageList);
        response.setStockItemHistList(filteredStockList);

        return response;
    }


    private String normalizeKey(Object value) {
        return String.valueOf(value == null ? "" : value).toUpperCase();
    }

    private Object findValueIgnoreCase(Map<String, Object> map, String targetKey) {
        if (map == null || targetKey == null) {
            return null;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(targetKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) { return BigDecimal.ZERO;}
        if (value instanceof BigDecimal) {return (BigDecimal) value;}
        if (value instanceof Integer) {return BigDecimal.valueOf((Integer) value);}
        if (value instanceof Long) {return BigDecimal.valueOf((Long) value);}
        if (value instanceof Double) {return BigDecimal.valueOf((Double) value);}
        if (value instanceof Float) {return BigDecimal.valueOf(((Float) value).doubleValue());}

        try {
            return new BigDecimal(String.valueOf(value));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Object nvl(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    /**
     * 품목별 사용량
     */
    public List<ItemUseVo> getItemUseList(ItemUseVo vo){
        return stockMapper.getItemUseList(vo);
    }

    /**
     * 원재료, 부자재 수불부
     * @param vo
     * @return
     */
    public List<TranLedgerVo> getTranLedger(TranLedgerVo vo){return stockMapper.getTranLedger(vo);}


    /**
     * 시험번호별 사용현황
     * @param vo
     * @return
     */
    public List<TestNoProdVo> getTestUseList(TestNoProdVo vo){return stockMapper.getTestUseList(vo);}


}
