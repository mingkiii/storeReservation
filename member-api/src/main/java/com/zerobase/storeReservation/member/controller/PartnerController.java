package com.zerobase.storeReservation.member.controller;

import static com.zerobase.storeReservation.member.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberVo;
import com.zerobase.storeReservation.member.domain.dto.PartnerDto;
import com.zerobase.storeReservation.member.domain.model.Partner;
import com.zerobase.storeReservation.member.exception.CustomException;
import com.zerobase.storeReservation.member.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {
    private static final String TOKEN = "X-AUTH-TOKEN";

    private final PartnerService partnerService;
    private final JwtAuthenticationProvider provider;

    @GetMapping("/getInfo")
    public ResponseEntity<PartnerDto> getInfo(
        @RequestHeader(name = TOKEN) String token
    ) {
        MemberVo memberVo = provider.getMemberVo(token);
        Partner partner = partnerService.findByIdAndEmail(memberVo.getId(), memberVo.getEmail())
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

        return ResponseEntity.ok(PartnerDto.from(partner));
    }
}
