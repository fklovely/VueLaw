package com.legal.platform.config;

import com.legal.platform.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/upload",
                        "/upload/**",
                        "/uploads/**",
                        "/lawyer/list",
                        "/lawyer/intelligent-match",
                        "/lawyer/detail/**",
                        "/article/list",
                        "/article/detail/**",
                        "/course/list",
                        "/course/detail/**",
                        "/course-chapter/list/**",
                        "/case/list",
                        "/case/detail/**",
                        "/regulation/list",
                        "/regulation/detail/**",
                        "/question/list",
                        "/question/detail/**",
                        "/document/list",
                        "/document/detail/**",
                        "/category/list",
                        "/notice/list",
                        "/notice/detail/**",
                        "/config/**",
                        "/ws/**"
                );
    }
}
