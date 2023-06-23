package com.zerobase.storeReservation.member.domain.Form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpForm {
    @NotBlank(message = "필수 입력")
    @Email(message = "이메일 형식에 맞게 입력해 주세요.")
    private String email;

    @NotBlank(message = "필수 입력")
    private String name;

    @NotBlank(message = "필수 입력")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요. 특수문자는 반드시 포함 해주세요.")
    private String password;

    @NotBlank(message = "필수 입력")
    @Pattern(regexp = "^010\\d{8,9}$", message = "'-' 빼고 010으로 시작하는 10~11자리 숫자로 작성 해주세요.")
    private String phone;
}
