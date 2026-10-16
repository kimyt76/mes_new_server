
package com.jct.mes_new.config.security;

import com.jct.mes_new.auth.service.CustomUserDetailsService;
import com.jct.mes_new.auth.vo.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        // 모바일 로그인 API는 JWT 검사 제외
        if (path.equals("/mobile/auth/login")) {
            return true;
        }

        // 모바일 API만 JWT 검사
        return !path.startsWith("/mobile/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null &&
                authorization.startsWith("Bearer ")) {

            String token = authorization.substring(7);

            if (jwtTokenProvider.validateToken(token)) {

                String userId = jwtTokenProvider.getUserId(token);

                try {
                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(userId);

                    if (userDetails instanceof CustomUserDetails &&
                            userDetails.isEnabled()) {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContext context =
                                SecurityContextHolder.createEmptyContext();

                        context.setAuthentication(authentication);

                        SecurityContextHolder.setContext(context);
                    }

                } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
