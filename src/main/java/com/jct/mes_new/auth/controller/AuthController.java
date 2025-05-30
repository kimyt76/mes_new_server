package com.jct.mes_new.auth.controller;

import com.fasterxml.jackson.core.Base64Variant;
import com.jct.mes_new.auth.service.UserService;
import com.jct.mes_new.auth.vo.LoginRequest;
import com.jct.mes_new.auth.vo.UserVo;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    final AuthenticationManager authenticationManager;
    final UserService userService;
    //final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) throws Exception {

        try {
            UserVo userInfo = userService.getUserInfo(request.getUserId());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("userInfo", userInfo);

            return ResponseEntity.ok().body(userInfo);
        } catch (BadCredentialsException e) {
            throw new AuthException("자격 증명에 실패하였습니다" + e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException("로그인중 오류가 발생했습니다. " + e);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 서버 세션 무효화
        }

        // JSESSIONID 쿠키 즉시 만료
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/registe")
    public ResponseEntity<?> getLoginUser(HttpSession session) {
        UserVo user = (UserVo) session.getAttribute("userInfo");

        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
