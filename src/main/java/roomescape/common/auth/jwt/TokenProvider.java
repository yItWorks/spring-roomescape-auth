package roomescape.common.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    private final Key key;
    private final long validityInMilliseconds;

    // application.yml의 설정값을 주입받아 초기화
    public TokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-seconds}") long expirationSeconds) {

        // 시크릿 키를 기반으로 암호화 키 객체 생성 (순수 Java & jjwt 기능)
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validityInMilliseconds = expirationSeconds * 1000;
    }

    /**
     * 1. JWT 토큰 생성
     */
    public String createToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.validityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 식별자(PK) 저장
                .setIssuedAt(now)                   // 발행 시간
                .setExpiration(validity)            // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 암호화 서명
                .compact();
    }

    /**
     * 2. 토큰 유효성 검증 (정상 토큰이면 true, 아니면 false)
     */
    public boolean isValidToken(String token) {
        try {
            // 서명이 맞는지, 만료되지 않았는지 파싱을 시도합니다.
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 위변조 되었거나, 만료되었거나, 비어있는 경우 모두 여기서 잡힙니다.
            return false;
        }
    }

    /**
     * 3. 토큰에서 유저 ID(PK) 추출
     */
    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }
}
