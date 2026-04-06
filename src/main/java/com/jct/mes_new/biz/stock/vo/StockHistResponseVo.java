package com.jct.mes_new.biz.stock.vo;

import java.util.List;
import java.util.Map;

public class StockHistResponseVo {

    private List<Map<String, Object>> storageList;
    private List<Map<String, Object>> stockItemHistList;

    public List<Map<String, Object>> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<Map<String, Object>> storageList) {
        this.storageList = storageList;
    }

    public List<Map<String, Object>> getStockItemHistList() {
        return stockItemHistList;
    }

    public void setStockItemHistList(List<Map<String, Object>> stockItemHistList) {
        this.stockItemHistList = stockItemHistList;
    }
}
