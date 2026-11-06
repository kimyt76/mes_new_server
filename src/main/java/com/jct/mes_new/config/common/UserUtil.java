
package com.jct.mes_new.config.common;

import com.jct.mes_new.auth.vo.CustomUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class UserUtil {

    public static CustomUserDetails getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 익명 사용자인 경우
        if (authentication == null ||
                authentication instanceof AnonymousAuthenticationToken ||
                !authentication.isAuthenticated()) {

            throw new AuthenticationCredentialsNotFoundException(
                    "로그인 인증정보가 없습니다."
            );
        }

        Object principal = authentication.getPrincipal();

        // CustomUserDetails 타입 확인
        if (!(principal instanceof CustomUserDetails)) {

            throw new AuthenticationCredentialsNotFoundException(
                    "올바른 사용자 인증정보가 아닙니다."
            );
        }

        return (CustomUserDetails) principal;
    }

    public static String getUserId() {

        return getCurrentUser().getUsername();
    }
}
