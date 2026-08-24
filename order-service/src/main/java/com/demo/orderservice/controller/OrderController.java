package com.demo.orderservice.controller;

import com.demo.common.api.client.UserClient;
import com.demo.common.api.dto.UserDTO;
import com.demo.common.core.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final UserClient userClient;

    public OrderController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public Result<List<Order>> getOrders() {
        return Result.ok(List.of(
                new Order(101L, 1L, "Laptop"),
                new Order(102L, 2L, "Mouse"),
                new Order(103L, 3L, "Monitor")
        ));
    }

    @GetMapping("/users")
    public Result<List<UserDTO>> getUsersFromUserService() {
        return userClient.getUsers();
    }

    public record Order(Long id, Long userId, String product) {}
}
