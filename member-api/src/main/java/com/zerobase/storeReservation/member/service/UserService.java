package com.zerobase.storeReservation.member.service;

import static com.zerobase.storeReservation.member.exception.ErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.storeReservation.member.exception.ErrorCode.EMAIL_CHECK_FAIL;
import static com.zerobase.storeReservation.member.exception.ErrorCode.PASSWORD_CHECK_FAIL;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.domain.model.User;
import com.zerobase.storeReservation.member.domain.repository.UserRepository;
import com.zerobase.storeReservation.member.exception.CustomException;
import com.zerobase.storeReservation.member.util.PasswordEncoderUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtAuthenticationProvider provider;
    private final PasswordEncoderUtil encoderUtil;
    private final BCryptPasswordEncoder encoder;

    // 회원가입 기능
    public String signUp(SignUpForm form) {
        // 이미 회원 가입된 이메일인지 검사
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            String encodePassword = encoderUtil.encodePassword(form.getPassword(), encoder);
            userRepository.save(User.of(form, encodePassword));
            return "회원 가입에 성공하였습니다.";
        }
    }

    // 로그인 성공 시 토큰 생성
    public String userLoginToken(SignInForm form) {
        // 이메일이 존재하지 않으면 예외 발생
        User user = userRepository.findByEmail(form.getEmail())
            .orElseThrow(() -> new CustomException(EMAIL_CHECK_FAIL));
        // 비밀번호 일치하지 않으면 예외 발생
        if (!encoderUtil.matchPassword(form.getPassword(), user.getPassword(), encoder)) {
            throw new CustomException(PASSWORD_CHECK_FAIL);
        }

        return provider.createToken(user.getEmail(), user.getId(), MemberType.USER);
    }

    public Optional<User> findByIdAndEmail(Long id, String email) {
        return userRepository.findByIdAndEmail(id, email);
    }
}
