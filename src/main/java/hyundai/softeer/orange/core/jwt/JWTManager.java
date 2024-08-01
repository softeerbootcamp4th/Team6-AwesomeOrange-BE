package hyundai.softeer.orange.core.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Component
public class JWTManager {
    private SecretKey secretKey;

    // jwt.secret을 properties에 넣어야 함!
    @Value("${jwt.secret}")
    public void setSecretKey(String secretKey) {
        this.secretKey = getDecodedSecretKey(secretKey);
    }

    /**
     * jwt 토큰을 생성한다. 수명은 기본적으로 시간(HOUR) 단위로 정해진다.
     *
     * @param subject  토큰의 목적
     * @param claims   토큰에 담을 객체를 Map 형태로 정의한 것
     * @param lifeSpan 토큰의 수명. 단위는 시간(HOUR)
     * @return JWT 토큰 문자열
     */
    public String generateToken(String subject, Map<String, Object> claims, int lifeSpan) {
        return generateToken(subject, claims, lifeSpan, Calendar.HOUR);
    }

    /**
     * jwt 토큰을 생성한다.
     *
     * @param subject      토큰의 목적
     * @param claims       토큰에 담을 객체를 Map 형태로 정의한 것
     * @param lifeSpan     토큰의 수명.
     * @param calendarType 시간 타입. Calendar 클래스 이하에 정의된 상수.
     * @return JWT 토큰 문자열
     *
     */
    public String generateToken(String subject, Map<String, Object> claims, int lifeSpan, int calendarType) {
        // 시작 시간 설정
        Date issuedDate = new Date();
        // 이슈 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(issuedDate);
        calendar.add(calendarType, lifeSpan);

        Date expirationDate = calendar.getTime();

        JwtBuilder builder = Jwts.builder()
                .header()
                .type("JWT")
                .and()
                // 나중에 필요하면 별도의 프로퍼티로 분리
                .issuer("team-orange")
                .signWith(secretKey)
                .issuedAt(issuedDate)

                .expiration(expirationDate)
                .subject(subject);

        for (Map.Entry<String, Object> claim : claims.entrySet()) {
            builder.claim(claim.getKey(), claim.getValue());
        }

        return builder.compact();
    }

    public Jws<Claims> parseToken(String token) {
        return parseToken(token, null);
    }

    public Jws<Claims> parseToken(String token, Map<String, Class<?>> typeMap) {
        JwtParserBuilder parser = Jwts.parser()
                .verifyWith(secretKey);
        if (typeMap != null) {
            parser.json(new JacksonDeserializer<>(typeMap));
        }

        return parser.build().parseSignedClaims(token);
    }

    public static SecretKey getDecodedSecretKey(String secretKey) {
        byte[] rawKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(rawKey);
    }
}
