package com.zerobase.storeReservation.member.config.filter;

import com.zerobase.storeReservation.domain.common.MemberVo;
import com.zerobase.storeReservation.domain.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.member.service.UserService;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@WebFilter(urlPatterns = "/user/*")
@RequiredArgsConstructor
public class UserFilter implements Filter {

    private final JwtAuthenticationProvider provider;
    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("X-AUTH-TOKEN");

        if (!provider.validateToken(token)) {
            throw new ServletException("Invalid Access");
        }
        MemberVo memberVo = provider.getMemberVo(token);
        userService.findByIdAndEmail(memberVo.getId(), memberVo.getEmail())
            .orElseThrow(() -> new ServletException("Invalid Access"));

        chain.doFilter(request, response);
    }
}
