package com.zerobase.storeReservation.member.controller;

import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.service.PartnerService;
import com.zerobase.storeReservation.member.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController { // 멤버 회원가입

    private final UserService userService;
    private final PartnerService partnerService;

    @PostMapping("/user")
    public ResponseEntity<String> userSignUp(@Valid @RequestBody SignUpForm form) {
        return ResponseEntity.ok(userService.signUp(form));
    }

    @PostMapping("/partner")
    public ResponseEntity<String> partnerSignUp(@Valid @RequestBody SignUpForm form) {
        return ResponseEntity.ok(partnerService.signUp(form));
    }
}
