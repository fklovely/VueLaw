package com.legal.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-path:uploads}")
    private String configuredUploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = configuredUploadPath;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.isAbsolute()) {
            String userDir = System.getProperty("user.dir");
            uploadPath = userDir + File.separator + configuredUploadPath;
        }
        
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String location = "file:" + uploadPath + File.separator;
        System.out.println("Static resource mapping: /uploads/** -> " + location);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
