package com.banking.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.banking.shared.interceptors.RequestInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private RequestInterceptor requestInterceptor;

    InterceptorConfig(RequestInterceptor requestInterceptor){
        this.requestInterceptor = requestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor).addPathPatterns("/**");
    }
}