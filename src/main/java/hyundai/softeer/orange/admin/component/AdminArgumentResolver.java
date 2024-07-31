package hyundai.softeer.orange.admin.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.admin.entity.Admin;
import hyundai.softeer.orange.core.auth.AuthNameUtil;
import hyundai.softeer.orange.core.jwt.JWTConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class AdminArgumentResolver implements HandlerMethodArgumentResolver {
    private ObjectMapper objectMapper;

    public AdminArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(AdminAnnotation.class);
        boolean hasType = Admin.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && hasType;
    }

    // @Auth에 등록한 클래스라면 아래 코드 정도로 값을 가져올 수 있음
    @Override
    public Admin resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        try {
            Jws<Claims> claims = (Jws<Claims>) request.getAttribute(JWTConst.Token);
            Object data = claims.getPayload().get("admin");
            return objectMapper.convertValue(data, Admin.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}