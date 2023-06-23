package com.zerobase.storeReservation.member.domain.model;

import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email; // 이메일을 로그인 아이디로 사용

    private String name;

    private String password;

    private String phone;

    public static Partner of(SignUpForm form, String encodedPassword) {
        return Partner.builder()
            .email(form.getEmail())
            .name(form.getName())
            .password(encodedPassword)
            .phone(form.getPhone())
            .build();
    }
}
