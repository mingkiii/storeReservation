package com.zerobase.storeReservaion.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "member-api", url = "${feign.client.url.member-api}")
public interface UserClient {
    String TOKEN = "X-AUTH-TOKEN";

    @GetMapping("/user/getInfo")
    ResponseEntity<UserDto> getInfo(
        @RequestHeader(name = TOKEN) String token
    );
}
