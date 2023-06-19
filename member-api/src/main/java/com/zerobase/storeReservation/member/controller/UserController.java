package com.zerobase.storeReservation.member.controller;

import static com.zerobase.storeReservation.member.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberVo;
import com.zerobase.storeReservation.member.domain.dto.UserDto;
import com.zerobase.storeReservation.member.domain.model.User;
import com.zerobase.storeReservation.member.exception.CustomException;
import com.zerobase.storeReservation.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController { // 유저 상세 정보 조회
    private static final String TOKEN = "X-AUTH-TOKEN";

    private final UserService userService;
    private final JwtAuthenticationProvider provider;

    @GetMapping("/getInfo")
    public ResponseEntity<UserDto> getInfo(
        @RequestHeader(name = TOKEN) String token
    ) {
        MemberVo memberVo = provider.getMemberVo(token);
        User user = userService.findByIdAndEmail(memberVo.getId(), memberVo.getEmail())
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

        return ResponseEntity.ok(UserDto.from(user));
    }
}
