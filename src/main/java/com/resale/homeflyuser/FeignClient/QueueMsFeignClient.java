package com.resale.homeflyuser.FeignClient;

import com.resale.homeflyuser.components.userInternal.dto.SalesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "queue-ms", url = "${queue.url}")
public interface QueueMsFeignClient {

    @PostMapping("/internal/sales")
    void addSalesUser(@RequestBody SalesDTO dto);
}


