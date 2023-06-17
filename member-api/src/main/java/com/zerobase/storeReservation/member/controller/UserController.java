package com.zerobase.storeReservation.member.controller;

import static com.zerobase.storeReservation.member.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.zerobase.storeReservation.member.domain.dto.UserDto;
import com.zerobase.storeReservation.member.domain.model.User;
import com.zerobase.storeReservation.member.exception.CustomException;
import com.zerobase.storeReservation.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getInfo")
    public ResponseEntity<UserDto> getInfo(
        @RequestParam("id")
        Long id
    ) {
        User user = userService.findById(id)
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

        return ResponseEntity.ok(UserDto.from(user));
    }
}
