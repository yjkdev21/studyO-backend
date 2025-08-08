package com.ex.tjspring.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ThumbnailConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // uploads 경로를 static/images에 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/images/");
    }
}