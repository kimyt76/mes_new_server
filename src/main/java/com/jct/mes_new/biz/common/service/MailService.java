package com.jct.mes_new.biz.common.service;

import com.jct.mes_new.biz.common.vo.MailVo;
import org.springframework.web.multipart.MultipartFile;
import jakarta.activation.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface MailService {

    String sendMail(MailVo vo,  Map<String, DataSource> uploadFiles);
    String sendMail(MailVo vo,  Map<String, DataSource> uploadFiles, Map<String, Object> model);


}
