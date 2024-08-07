package hyundai.softeer.orange.config;

import hyundai.softeer.orange.admin.component.AdminArgumentResolver;
import hyundai.softeer.orange.core.auth.AuthInterceptor;
import hyundai.softeer.orange.eventuser.component.EventUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminArgumentResolver adminArgumentResolver;
    private final EventUserArgumentResolver eventUserArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/swagger-ui/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(adminArgumentResolver);
        resolvers.add(eventUserArgumentResolver);
    }
}
