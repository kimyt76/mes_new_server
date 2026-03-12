package com.jct.mes_new.biz.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.awt.image.BufferedImage;
import java.time.LocalDate;

@Data
public class QrCodeInfo {
    private String itemCd;

    private String itemName;

    private String itemTypeCd;

    private String itemCondition;

    private String testNo;

    private String lotNo;

    private String prodNo;

    private String strQty;

    private String customerName;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate createDate;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate expiryDate;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate shelfLife;

    private String passStateName;

    private String chargingTestNo;

    private BufferedImage barcodeImage;

    private String supplyGb;
}
