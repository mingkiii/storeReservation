package com.zerobase.storeReservation.member.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncoderUtil {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public String encodePassword(String password, BCryptPasswordEncoder encoder) {
        return encoder.encode(password);
    }

    public boolean matchPassword(String rawPassword, String encodedPassword, BCryptPasswordEncoder encoder) {
        return encoder.matches(rawPassword, encodedPassword);
    }

}
