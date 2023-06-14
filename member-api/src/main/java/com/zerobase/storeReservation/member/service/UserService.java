package com.zerobase.storeReservation.member.service;

import static com.zerobase.storeReservation.member.exception.ErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.storeReservation.member.exception.ErrorCode.LOGIN_CHECK_FAIL;

import com.zerobase.storeReservation.domain.common.MemberType;
import com.zerobase.storeReservation.domain.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.domain.model.User;
import com.zerobase.storeReservation.member.domain.repository.UserRepository;
import com.zerobase.storeReservation.member.exception.CustomException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtAuthenticationProvider provider;

    public String signUp(SignUpForm form) {
        if (isEmailExist(form.getEmail())) {
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            userRepository.save(User.from(form));
            return "회원 가입에 성공하였습니다.";
        }
    }

    public String userLoginToken(SignInForm form) {
        User user = findValidUser(form.getEmail(), form.getPassword())
            .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

        return provider.createToken(user.getEmail(), user.getId(), MemberType.USER);
    }

    public Optional<User> findByIdAndEmail(Long id, String email) {
        return userRepository.findByIdAndEmail(id, email);
    }

    private Optional<User> findValidUser(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    private boolean isEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
