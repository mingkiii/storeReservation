package com.zerobase.storeReservation.user.controller;

import com.zerobase.storeReservation.user.domain.Form.SignUpForm;
import com.zerobase.storeReservation.user.service.PartnerService;
import com.zerobase.storeReservation.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final UserService userService;
    private final PartnerService partnerService;

    @PostMapping("/user")
    public ResponseEntity<String> userSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(userService.signUp(form));
    }

    @PostMapping("/partner")
    public ResponseEntity<String> partnerSignUp(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(partnerService.signUp(form));
    }
}
