package com.jct.mes_new.biz.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientManagerVo {
    private Long clientManagerId;
    private Long clientId;
    private String businessManagerName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate regDate;
    private String brandName;
    private String deptName;
    private String managerName;
    private String jobPosition;
    private String tel;
    private String directTel;
    private String email;
    private String homepage;
    private String workplace;
    private int orderDist;
    private String useYn;
    private String userId;
}
