package com.zerobase.storeReservation.member.config.filter;

import com.zerobase.storeReservation.common.type.MemberVo;
import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.member.service.PartnerService;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@WebFilter(urlPatterns = "/partner/*")
@RequiredArgsConstructor
public class PartnerFilter implements Filter {

    private final JwtAuthenticationProvider provider;
    private final PartnerService partnerService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("X-AUTH-TOKEN");

        if (!provider.validateToken(token)) {
            throw new ServletException("Invalid Access");
        }
        MemberVo memberVo = provider.getMemberVo(token);
        partnerService.findByIdAndEmail(memberVo.getId(), memberVo.getEmail())
            .orElseThrow(() -> new ServletException("Invalid Access"));

        chain.doFilter(request, response);
    }
}
