package com.jct.mes_new.auth.service;

import com.jct.mes_new.auth.mapper.UserMapper;
import com.jct.mes_new.auth.vo.CustomUserDetails;
import com.jct.mes_new.auth.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVo user = userMapper.getUserInfo(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자 없음: " + username);
        }
        return new CustomUserDetails(user);
    }
}
