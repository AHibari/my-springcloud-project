package com.demo.common.api.client;

import com.demo.common.api.dto.UserDTO;
import com.demo.common.core.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users")
    Result<List<UserDTO>> getUsers();
}
