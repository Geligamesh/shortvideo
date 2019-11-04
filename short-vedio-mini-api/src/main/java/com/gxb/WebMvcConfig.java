package com.gxb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:E:/short_video_library/");
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
        .addPathPatterns("/video/upload","/video/uploadCover","/video/userLike","/video/userUnLike","/video/saveComment")
        .addPathPatterns("/bgm/**")
        .excludePathPatterns("/user/queryPublisher");
    }

    @Bean
    public MiniInterceptor miniInterceptor(){
        return new MiniInterceptor();
    }
}
