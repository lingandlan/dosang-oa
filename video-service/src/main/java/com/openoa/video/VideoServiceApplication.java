package com.openoa.video;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.openoa.video.mapper")
public class VideoServiceApplication {
    public static void main(String[] args) {
        // Disable Nacos discovery auto-configuration
        System.setProperty("spring.cloud.nacos.discovery.enabled", "false");
        System.setProperty("spring.cloud.nacos.config.enabled", "false");
        SpringApplication.run(VideoServiceApplication.class, args);
    }
}
