package com.jct.mes_new.auth.vo;

import lombok.Data;

@Data
public class LoginRequest {

    private String userId;
    private String password;

    // Spring Security 적용하면서 username 대신에 user_id 사용하기위한 처리
    public String getUsername() {
        return this.userId;
    }
}
