package hyundai.softeer.orange.core.auth;

import hyundai.softeer.orange.core.jwt.JWTManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.*;

public class AuthInterceptor implements HandlerInterceptor {
    private final JWTManager jwtManager;

    public AuthInterceptor(JWTManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth classAnnotation = handlerMethod.getClass().getAnnotation(Auth.class);
        Auth methodAnnotation = handlerMethod.getMethod().getAnnotation(Auth.class);

        // 인증 필요한지 검사. 어노테이션 없으면 인증 필요 없음
        if(classAnnotation == null && methodAnnotation == null) return true;

        // 변환 맵 생성
        Map<String, Class<?>> convertionMap = new HashMap<>();

        // conversion map 처리하기
        if(classAnnotation != null) Arrays.stream(classAnnotation.value())
                    .forEach(it -> convertionMap.put(AuthNameUtil.authName(it), it));
        if(methodAnnotation != null) Arrays.stream(methodAnnotation.value())
                    .forEach(it -> convertionMap.put(AuthNameUtil.authName(it), it));

        // 헤더 분석 과정
        String authorizationHeader = request.getHeader("Authorization");

        // 헤더가 없는 경우 => 인증 안됨
        if(authorizationHeader == null) return false;
        String[] tokenInfo = authorizationHeader.split("\\s+");

        // Bearer token 형식이 아님 => 인증 안됨
        if (tokenInfo.length < 2) return false;
        if(tokenInfo[0].equalsIgnoreCase("bearer")) return false;

        String token = tokenInfo[1];

        try {
            var parsedToken = jwtManager.parseToken(token, convertionMap);
            // 토큰 객체들을 request.attribute에 넣기.

            for(var entry : convertionMap.entrySet()) {
                String key = entry.getKey();
                Class<?> clazz = entry.getValue();

                Object target = parsedToken.getPayload().get(key, clazz);
                request.setAttribute(key, target);
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
