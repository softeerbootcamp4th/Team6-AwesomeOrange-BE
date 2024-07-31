package hyundai.softeer.orange.admin.component;

import hyundai.softeer.orange.admin.entity.Admin;
import hyundai.softeer.orange.core.auth.AuthNameUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AdminArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(AdminAnnotation.class);
        boolean hasType = Admin.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && hasType;
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        return request.getSession().getAttribute(AuthNameUtil.authName(Admin.class));
    }
}