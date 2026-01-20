package com.jct.mes_new.biz.common.vo;

import lombok.Data;

@Data
public class MailConfigVo {

    private String mailId;
    private String smtpHost;
    private int smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private String smtpSsl;
    private String smtpEtc;
    private String fromEmail;
    private String fromName;


}
