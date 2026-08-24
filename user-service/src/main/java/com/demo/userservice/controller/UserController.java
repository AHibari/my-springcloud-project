package com.demo.userservice.controller;

import com.demo.common.api.dto.UserDTO;
import com.demo.common.core.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public Result<List<UserDTO>> getUsers() {
        return Result.ok(List.of(
                new UserDTO(1L, "Alice"),
                new UserDTO(2L, "Bob"),
                new UserDTO(3L, "Charlie")
        ));
    }
}
