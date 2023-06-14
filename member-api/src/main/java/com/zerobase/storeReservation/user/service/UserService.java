package com.zerobase.storeReservation.user.service;

import static com.zerobase.storeReservation.user.exception.ErrorCode.ALREADY_REGISTER_USER;

import com.zerobase.storeReservation.user.domain.Form.SignUpForm;
import com.zerobase.storeReservation.user.domain.model.User;
import com.zerobase.storeReservation.user.domain.repository.UserRepository;
import com.zerobase.storeReservation.user.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String signUp(SignUpForm form) {
        if (isEmailExist(form.getEmail())) {
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            userRepository.save(User.from(form));
            return "회원 가입에 성공하였습니다.";
        }
    }

    private boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
