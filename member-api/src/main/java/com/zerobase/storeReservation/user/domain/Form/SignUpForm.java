package com.zerobase.storeReservation.user.domain.Form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpForm {
    private String email;
    private String name;
    private String password;
    private String phone;
}
