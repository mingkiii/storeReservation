package com.zerobase.storeReservation.domain.config;

import com.zerobase.storeReservation.domain.common.MemberType;
import com.zerobase.storeReservation.domain.common.MemberVo;
import com.zerobase.storeReservation.domain.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Objects;

public class JwtAuthenticationProvider {

    private final String secretKey = "secretKey";

    private final long tokenValidTime = 1000L * 60 * 60 * 24; // 하루

    public String createToken(String userPk, Long id, MemberType memberType) {
        Claims claims = Jwts.claims()
                        .setSubject(Aes256Util.encrypt(userPk))
                        .setId(Aes256Util.encrypt(id.toString()));
        claims.put("roles", memberType);
        Date now = new Date();

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + tokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claimsJws =
                Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public MemberVo getMemberVo(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey)
                                    .parseClaimsJws(token).getBody();
        return new MemberVo(
            Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(claims.getId()))),
            Aes256Util.decrypt(claims.getSubject())
        );
    }

}
