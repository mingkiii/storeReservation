package com.zerobase.storeReservation.member.domain.dto;

import com.zerobase.storeReservation.member.domain.model.Partner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PartnerDto {

    private Long id;
    private String name;
    private String email;
    private String phone;

    public static PartnerDto from(Partner partner) {
        return PartnerDto.builder()
            .id(partner.getId())
            .name(partner.getName())
            .email(partner.getEmail())
            .phone(partner.getPhone())
            .build();
    }
}
