package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.stock.mapper.StockMapper;
import com.jct.mes_new.biz.stock.service.StockService;
import com.jct.mes_new.biz.stock.vo.StockHistResponseVo;
import com.jct.mes_new.biz.stock.vo.StockVo;
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


}
