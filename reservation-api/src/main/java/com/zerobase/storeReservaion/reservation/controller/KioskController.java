package com.zerobase.storeReservaion.reservation.controller;

import com.zerobase.storeReservaion.reservation.domain.form.CheckinForm;
import com.zerobase.storeReservaion.reservation.service.KioskService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kiosk/checkin")
@RequiredArgsConstructor
public class KioskController {

    private final KioskService kioskService;

    // 키오스크 기능 - 방문 가능한 예약 정보인지 검사 후 체크인, 체크인 시 서버에 알림
    @PutMapping
    public ResponseEntity<String> processCheckin(@RequestBody CheckinForm form) {
        LocalDateTime currentTime = LocalDateTime.now();
        return ResponseEntity.ok(kioskService.checkValid(
            form.getReservationId(), form.getStoreId(), currentTime));
    }
}
