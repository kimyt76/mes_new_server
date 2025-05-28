package com.jct.mes_new.auth.vo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserVo {
    /**
     * Userid : 이메일 ID
     */
    private String userId;

    /**
     * 사용자 패스워드
     */
    private String password;

    /**
     * 사용자 이름  (유저명이 중복일 경우 멤버 이름으로 검색)
     */
    private String memberNm;

    /**
     * 부서명
     */
    private String deptNm;

    /**
     * 직위
     */
    private String jobPosition;

    /**
     * 이메일
     */
    private String email;

    /**
     * 전화번호
     */
    private String phone;

    /**
     *  비고
     */
    private String etc;

    /**
     * 사용여부 (퇴직여부)
     */
    private String useYn;

    private int id;
}
