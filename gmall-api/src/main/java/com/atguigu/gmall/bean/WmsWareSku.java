package com.atguigu.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;

public class WmsWareSku {

    @Id
    @Column
    private String id;

    @Column
    private String skuId;

    @Column
    private String warehouseId;

    @Column
    private int stock;

    @Column
    private String stockName;

    @Column
    private String stockLocked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockLocked() {
        return stockLocked;
    }

    public void setStockLocked(String stockLocked) {
        this.stockLocked = stockLocked;
    }
}
