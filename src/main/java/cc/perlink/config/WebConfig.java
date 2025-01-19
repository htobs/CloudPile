package cc.perlink.config;

import cc.perlink.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 拦截器放行，不对该接口进行拦截
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(loginInterceptor)
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/user/register")
                .excludePathPatterns("/api/user/rePassword")
                .excludePathPatterns("/api/user/checkKey")
                .excludePathPatterns("/api/user/keyLogin")
                .excludePathPatterns("/api/file/preview")
                .excludePathPatterns("/api/file/secure_upload")
                .excludePathPatterns("/api/file/secure_delete")
                .excludePathPatterns("/api/code/create")
                .excludePathPatterns("/api/code/check")
                .excludePathPatterns("/api/email/**")
                .addPathPatterns("/api/**")
        ;
    }
}