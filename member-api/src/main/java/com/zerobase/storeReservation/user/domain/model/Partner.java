package com.zerobase.storeReservation.user.domain.model;

import com.zerobase.storeReservation.user.domain.Form.SignUpForm;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Partner extends BaseEntity{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "필수 입력")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "필수 입력")
    private String name;

    @NotBlank(message = "필수 입력")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요. 특수문자는 반드시 포함 해주세요.")
    private String password;

    @NotBlank(message = "필수 입력")
    @Pattern(regexp = "^010\\d{8,9}$", message = "'-' 빼고 010으로 시작하는 10~11자리 숫자로 작성 해주세요.")
    private String phone;

    public static Partner from(SignUpForm form) {
        return Partner.builder()
            .email(form.getEmail())
            .name(form.getName())
            .password(form.getPassword())
            .phone(form.getPhone())
            .build();
    }
}
