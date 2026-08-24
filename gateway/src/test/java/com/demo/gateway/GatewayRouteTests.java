package com.demo.gateway;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.core.env.Environment;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.InetSocketAddress;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * 网关路由集成测试：
 * - 通过 mock DiscoveryClient 将服务实例指向本测试进程内的 stub HTTP server
 * - 验证 /api/user/**、/api/order/**、/api/users/**、/api/orders/** 四条路由的完整转发链路
 * - 不依赖真实服务，可独立运行
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRouteTests {

    private static HttpServer stubServer;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private GatewayProperties gatewayProperties;

    @Autowired
    private Environment environment;

    @MockBean
    private DiscoveryClient discoveryClient;

    @BeforeEach
    void printRoutes() {
        List<RouteDefinition> routes = gatewayProperties.getRoutes();
        System.out.println("=== GatewayProperties routes: " + routes.size());
        routes.forEach(r -> System.out.println("  route: " + r.getId() + " uri=" + r.getUri()));
        System.out.println("=== spring.application.name = " + environment.getProperty("spring.application.name"));
        System.out.println("=== eureka.client.enabled = " + environment.getProperty("eureka.client.enabled"));
        System.out.println("=== spring.cloud.gateway.enabled = " + environment.getProperty("spring.cloud.gateway.enabled"));
    }

    @BeforeAll
    static void startStubServer() throws Exception {
        stubServer = HttpServer.create(new InetSocketAddress(0), 0);

        stubServer.createContext("/users", exchange -> {
            byte[] body = "{\"code\":200,\"message\":\"success\",\"data\":[{\"id\":1,\"name\":\"Alice\"}]}".getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        stubServer.createContext("/orders", exchange -> {
            byte[] body = "{\"code\":200,\"message\":\"success\",\"data\":[{\"id\":101,\"userId\":1,\"product\":\"Laptop\"}]}".getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        stubServer.start();
    }

    @AfterAll
    static void stopStubServer() {
        stubServer.stop(0);
    }

    private void mockInstance(String serviceId) {
        int port = stubServer.getAddress().getPort();
        ServiceInstance instance = new DefaultServiceInstance("stub-" + serviceId, serviceId, "127.0.0.1", port, false);
        when(discoveryClient.getInstances(serviceId)).thenReturn(List.of(instance));
    }

    @Test
    void userRoute_forwardsToUserService() {
        mockInstance("user-service");

        webTestClient.get().uri("/api/user/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data[0].name").isEqualTo("Alice");
    }

    @Test
    void userCompatibilityRoute_forwardsToUserService() {
        mockInstance("user-service");

        webTestClient.get().uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].name").isEqualTo("Alice");
    }

    @Test
    void orderRoute_forwardsToOrderService() {
        mockInstance("order-service");

        webTestClient.get().uri("/api/order/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data[0].product").isEqualTo("Laptop");
    }

    @Test
    void orderCompatibilityRoute_forwardsToOrderService() {
        mockInstance("order-service");

        webTestClient.get().uri("/api/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].product").isEqualTo("Laptop");
    }

    @Test
    void unknownPath_returnsNotFound() {
        webTestClient.get().uri("/api/unknown")
                .exchange()
                .expectStatus().isNotFound();
    }
}
