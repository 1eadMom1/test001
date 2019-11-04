package com.atguigu.gmall.config;

import com.atguigu.gmall.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] pathPatterns = {"/**"};  //只进行拦截的路径
        String[] excludePath = {"/error","/js/**","/css/**","/success.html","/img/**"};
        registry.addInterceptor(authInterceptor).addPathPatterns(pathPatterns).excludePathPatterns(excludePath);
    }
}
