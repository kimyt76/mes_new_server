
package com.jct.mes_new.biz.mobile.controller;

import com.jct.mes_new.auth.vo.LoginRequest;
import com.jct.mes_new.auth.vo.UserVo;
import com.jct.mes_new.auth.service.UserService;
import com.jct.mes_new.config.security.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mobile/auth")
public class MobileAuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public MobileAuthController(
            @Qualifier("authManager") AuthenticationManager authenticationManager,
            UserService userService,
            JwtTokenProvider jwtTokenProvider) {

        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {

            // 1. 사용자 ID / 비밀번호 인증
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUserId(),
                                    request.getPassword()
                            )
                    );

            // 2. 인증된 사용자 ID
            String userId = authentication.getName();

            // 3. 사용자 정보 조회
            UserVo userInfo = userService.getUserInfo(userId);

            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "message", "사용자 정보가 없습니다."
                        ));
            }

            // 4. JWT 발급
            String accessToken = jwtTokenProvider.createToken(userId);

            // 5. 모바일 로그인 응답
            Map<String, Object> result = new HashMap<>();

            result.put("accessToken", accessToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", 28800);
            result.put("userId", userInfo.getUserId());
            result.put("memberNm", userInfo.getMemberNm());

            log.info("모바일 로그인 성공 : {}", userId);

            return ResponseEntity.ok(result);

        } catch (BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "아이디 또는 비밀번호가 올바르지 않습니다."
                    ));

        } catch (DisabledException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "사용할 수 없는 계정입니다."
                    ));

        } catch (AuthenticationException e) {

            log.warn("모바일 로그인 인증 실패", e);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "로그인 인증에 실패했습니다."
                    ));

        } catch (Exception e) {

            log.error("모바일 로그인 오류", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "로그인 처리 중 오류가 발생했습니다."
                    ));
        }
    }
}
