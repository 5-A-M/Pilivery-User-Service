package com.fiveam.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("ITEM-SERVICE")
public interface ItemServiceClient {

    @PostMapping("/carts")
    Long createCart(Long userId);

}