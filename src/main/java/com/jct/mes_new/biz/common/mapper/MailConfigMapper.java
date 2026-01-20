package com.jct.mes_new.biz.common.mapper;

import com.jct.mes_new.biz.common.vo.MailConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MailConfigMapper {

    MailConfigVo selectMailConfig(@Param("mailId") String id);
}
