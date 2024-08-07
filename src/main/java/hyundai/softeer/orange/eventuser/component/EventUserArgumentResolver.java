package hyundai.softeer.orange.eventuser.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.core.jwt.JWTConst;
import hyundai.softeer.orange.eventuser.dto.EventUserInfo;
import hyundai.softeer.orange.eventuser.exception.EventUserException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(EventUserAnnotation.class);
        boolean hasType = EventUserInfo.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && hasType;
    }

    @Override
    public EventUserInfo resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                         NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        try {
            Jws<Claims> claims = (Jws<Claims>) request.getAttribute(JWTConst.Token);
            Object userId = claims.getPayload().get(ConstantUtil.CLAIMS_USER_KEY);
            Object role = claims.getPayload().get(ConstantUtil.CLAIMS_ROLE_KEY);
            return new EventUserInfo((String) userId, (String) role);
        } catch (Exception e) {
            throw new EventUserException(ErrorCode.UNAUTHORIZED);
        }
    }
}
