package com.jct.mes_new.biz.base.vo;

import lombok.Data;

@Data
public class ClientManagerVo {
    private int clientManagerId;
    private String clientId;
    private String deptName;
    private String managerName;
    private String jobPosition;
    private String tel;
    private String directTel;
    private String email;
    private int orderDist;
    private String useYn;
    private String userId;
}
