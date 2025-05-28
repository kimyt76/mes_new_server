package com.jct.mes_new.config.common;

import com.jct.mes_new.auth.vo.CustomUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class UserUtil {

    public static CustomUserDetails getCurrentUser() {
        return (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getUserId() {
        return getCurrentUser().getUsername();
    }

}