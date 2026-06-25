package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.stock.mapper.StockMapper;
import com.jct.mes_new.biz.stock.service.StockService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.biz.system.mapper.StorageMapper;
import com.jct.mes_new.biz.system.vo.StorageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

        StorageVo storageVo = new StorageVo();
        storageVo.setAreaCd(vo.getAreaCd());

        // 창고 목록 조회
        List<StorageVo> storageList = storageMapper.getStorageList(storageVo);

        // 창고별 재고 수량 조회
        List<StockVo> stockList;

        if (vo.getType() == null || vo.getType().isEmpty() || "ITEM".equals(vo.getType())) {
            stockList = stockMapper.getStockItemList(vo);
        } else {
            stockList = stockMapper.getStockTestList(vo);
        }

        // 품목별 / 시험번호별 피벗 데이터 생성
        for (StockVo row : stockList) {
            String mapKey = row.getItemCd();

            if ("TEST".equals(vo.getType())) {
                mapKey = row.getItemCd() + "_" + row.getTestNo();
            }

            StockVo pivot = pivotMap.computeIfAbsent(mapKey, key -> {
                StockVo stockVo = new StockVo();

                stockVo.setItemCd(row.getItemCd());
                stockVo.setItemName(row.getItemName());
                stockVo.setItemTypeCd(row.getItemTypeCd());
                stockVo.setTestNo(row.getTestNo());

                stockVo.setInReQty(nvl(row.getInReQty()));
                stockVo.setSaftQty(nvl(row.getSaftQty()));
                stockVo.setTotQty(BigDecimal.ZERO);
                stockVo.setStorageQtyMap(new LinkedHashMap<>());

                return stockVo;
            });

            BigDecimal qty = nvl(row.getQty());

            pivot.getStorageQtyMap().put(row.getStorageCd(), qty);
            pivot.setTotQty(nvl(pivot.getTotQty()).add(qty));
        }
        // 창고별 세로 합계 계산
        Map<String, BigDecimal> storageTotalMap = new LinkedHashMap<>();

        for (StorageVo storage : storageList) {
            storageTotalMap.put(storage.getStorageCd(), BigDecimal.ZERO);
        }

        for (StockVo stockVo : pivotMap.values()) {
            for (Map.Entry<String, BigDecimal> entry : stockVo.getStorageQtyMap().entrySet()) {
                String storageCd = entry.getKey();
                BigDecimal qty = (BigDecimal) nvl(entry.getValue());

                storageTotalMap.put(
                        storageCd,
                        nvl(storageTotalMap.get(storageCd)).add(qty)
                );
            }
        }
        // 세로 합계가 0이 아닌 창고만 남김
        List<StorageVo> filteredStorageList = storageList.stream()
                .filter(storage -> {
                    BigDecimal total = nvl(storageTotalMap.get(storage.getStorageCd()));
                    return total.compareTo(BigDecimal.ZERO) != 0;
                })
                .toList();

        // 프론트 동적 컬럼 생성
        List<Map<String, String>> dynamicColumns = new ArrayList<>();

        for (StorageVo storage : filteredStorageList) {
            Map<String, String> col = new LinkedHashMap<>();
            col.put("field", storage.getStorageCd());
            col.put("header", storage.getStorageName());
            dynamicColumns.add(col);
        }
        // 프론트에서 data[col.field]로 바로 접근 가능하도록 rows 펼치기
        List<Map<String, Object>> rows = new ArrayList<>();

        for (StockVo stockVo : pivotMap.values()) {
            Map<String, Object> rowMap = new LinkedHashMap<>();

            rowMap.put("itemCd", stockVo.getItemCd());
            rowMap.put("itemName", stockVo.getItemName());
            rowMap.put("itemTypeCd", stockVo.getItemTypeCd());
            rowMap.put("testNo", stockVo.getTestNo());

            rowMap.put("inReQty", nvl(stockVo.getInReQty()));
            rowMap.put("totQty", nvl(stockVo.getTotQty()));
            rowMap.put("saftQty", nvl(stockVo.getSaftQty()));

            for (StorageVo storage : filteredStorageList) {
                String storageCd = storage.getStorageCd();
                BigDecimal qty = (BigDecimal) stockVo.getStorageQtyMap().get(storageCd);

                rowMap.put(storageCd, nvl(qty));
            }

            rows.add(rowMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dynamicColumns", dynamicColumns);
        result.put("rows", rows);

        return result;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }




    public StockHistResponseVo getStockItemHistList(StockVo vo) {

        StockHistResponseVo response = new StockHistResponseVo();

        // 1. area별 창고 조회
        List<Map<String, Object>> storageList = storageMapper.getAreaStorageList(vo.getAreaCd());

        if (storageList == null || storageList.isEmpty()) {
            response.setStorageList(Collections.emptyList());
            response.setStockItemHistList(Collections.emptyList());
            return response;
        }

        // 2. 창고코드 목록 추출
        List<String> storageCodes = storageList.stream()
                .map(s -> String.valueOf(s.get("storageCd")))
                .collect(Collectors.toList());

        // 3. 재고 조회 파라미터 구성
        Map<String, Object> param = new HashMap<>();
        param.put("itemCd", vo.getItemCd());
        param.put("areaCd", vo.getAreaCd());
        param.put("storageCodes", storageCodes);

        // 4. 창고별 재고 조회
        // 동적 컬럼이므로 Map 형태가 가장 안정적
        List<Map<String, Object>> stockList = stockMapper.getStockList(param);

        if (stockList == null || stockList.isEmpty()) {
            response.setStorageList(Collections.emptyList());
            response.setStockItemHistList(Collections.emptyList());
            return response;
        }

        // 5. 창고코드 대문자 기준으로 합계 누적
        Map<String, BigDecimal> storageSumMap = new HashMap<>();
        for (String storageCd : storageCodes) {
            storageSumMap.put(normalizeKey(storageCd), BigDecimal.ZERO);
        }

        for (Map<String, Object> row : stockList) {
            for (String storageCd : storageCodes) {
                String normalizedCd = normalizeKey(storageCd);
                BigDecimal qty = toBigDecimal(findValueIgnoreCase(row, storageCd));
                storageSumMap.put(normalizedCd, storageSumMap.get(normalizedCd).add(qty));
            }
        }
        // 6. 세로 전체 합계가 0이 아닌 창고만 남김
        List<Map<String, Object>> filteredStorageList = storageList.stream()
                .filter(storage -> {
                    String storageCd = normalizeKey(storage.get("storageCd"));
                    BigDecimal sum = storageSumMap.getOrDefault(storageCd, BigDecimal.ZERO);
                    return sum.compareTo(BigDecimal.ZERO) != 0;
                })
                .collect(Collectors.toList());
        // 7. 남길 창고코드 Set
        Set<String> remainStorageSet = filteredStorageList.stream()
                .map(s -> normalizeKey(s.get("storageCd")))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        // 8. row 가공
        List<Map<String, Object>> filteredStockList = new ArrayList<>();

        for (int i = 0; i < stockList.size(); i++) {
            Map<String, Object> src = stockList.get(i);
            Map<String, Object> dest = new LinkedHashMap<>();

            dest.put("no", i + 1);
            dest.put("itemCd", findValueIgnoreCase(src, "itemCd"));
            dest.put("testNo", findValueIgnoreCase(src, "testNo"));
            dest.put("inDate", findValueIgnoreCase(src, "inDate"));
            dest.put("expiryDate", findValueIgnoreCase(src, "expiryDate"));
            dest.put("totQty", nvl(findValueIgnoreCase(src, "totQty"), BigDecimal.ZERO));

            for (String storageCd : remainStorageSet) {
                dest.put(storageCd, toBigDecimal(findValueIgnoreCase(src, storageCd)));
            }

            filteredStockList.add(dest);
        }

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




}
