package com.openoa.approval.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "notice-service", url = "${notice-service.url:http://localhost:8083}")
public interface NoticeClient {
    
    @PostMapping("/api/v1/notices")
    Object createNotice(@RequestBody Object notice);
}
