package org.example.config;

import io.jsonwebtoken.*;
import org.example.domain.common.UserVo;
import org.example.domain.common.UserType;
import org.example.util.Aes256Util;

import java.util.Date;
import java.util.Objects;

public class JwtAuthenticationProvider {
    private String secretKey = "secretKey";
    private long tokenValidTime = 1000L * 60 * 60 * 24; // 1일짜리

    // 토큰 발급
    public String createToken(String userPk, Long id, UserType userType){
        Claims claims = Jwts.claims().setSubject(Aes256Util.encrypt(userPk)).setId(Aes256Util.encrypt(id.toString())); // userPk와 id를 암호화.
        claims.put("roles", userType);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명을 HS256알고리즘으로 진행.
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String jwtToken){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claimsJws.getBody().getExpiration().before(new Date()); // 토큰이 만료돼었는지 확인. 현재 시간 이전이면.
        }catch (Exception e){
            return false;
        }
    }

    /*
    토큰을 받아서, UserVo로 변경(아이디 + 이메일)
     */
    public UserVo getUserVo(String token){
        Claims c = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        return new UserVo(Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(c.getId()))), Aes256Util.decrypt(c.getSubject()));
    }


}
