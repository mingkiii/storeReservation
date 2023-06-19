package com.zerobase.storeReservaion.reservation.client;

import com.zerobase.storeReservaion.reservation.domain.form.CheckinForm;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KioskClient {
    // 키오스크에서 유저가 체크인 시 방문 알림을 보낼 주소
    private static final String KIOSK_URL = "http://localhost:8082/partner/notifications/checkin?reservationId={reservationId}&storeId={storeId}";

    // 클라이언트에서 매장 도착 정보를 서버로 전달
    public void sendCheckinNotification(Long reservationId, Long storeId) {
        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 바디 설정
        CheckinForm requestBody = new CheckinForm(reservationId, storeId);
        HttpEntity<CheckinForm> requestEntity = new HttpEntity<>(requestBody, headers);

        // 매개변수 설정
        Map<String, Long> uriVariables = new HashMap<>();
        uriVariables.put("reservationId", reservationId);
        uriVariables.put("storeId", storeId);
        // REST API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            KIOSK_URL,
            HttpMethod.POST,
            requestEntity,
            String.class,
            uriVariables
        );

        // 응답 처리
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("매장 도착 정보를 서버로 전달했습니다.");
        } else {
            System.out.println("매장 도착 정보 전달에 실패했습니다.");
        }
    }
}
