package com.zerobase.storeReservation.member.controller;

import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.service.PartnerService;
import com.zerobase.storeReservation.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class SignInController {
    private final UserService userService;
    private final PartnerService partnerService;

    @PostMapping("/user")
    public ResponseEntity<String> signInUser(@RequestBody SignInForm form) {
        return ResponseEntity.ok(userService.userLoginToken(form));
    }

    @PostMapping("/partner")
    public ResponseEntity<String> signInPartner(@RequestBody SignInForm form) {
        return ResponseEntity.ok(partnerService.partnerLoginToken(form));
    }
}
