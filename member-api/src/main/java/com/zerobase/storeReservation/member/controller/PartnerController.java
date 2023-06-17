package com.zerobase.storeReservation.member.controller;

import static com.zerobase.storeReservation.member.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.zerobase.storeReservation.member.domain.dto.PartnerDto;
import com.zerobase.storeReservation.member.domain.model.Partner;
import com.zerobase.storeReservation.member.exception.CustomException;
import com.zerobase.storeReservation.member.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping("/getInfo")
    public ResponseEntity<PartnerDto> getInfo(
        @RequestParam("id")
        Long id
    ) {
        Partner partner = partnerService.findById(id)
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

        return ResponseEntity.ok(PartnerDto.from(partner));
    }
}
