package com.demo.common.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResultTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void ok_returnsSuccessCodeAndData() {
        Result<String> result = Result.ok("hello");
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMessage());
        assertEquals("hello", result.getData());
    }

    @Test
    void fail_returnsErrorCodeAndNullData() {
        Result<String> result = Result.fail("bad request");
        assertEquals(500, result.getCode());
        assertEquals("bad request", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void serialization_roundTrip_preservesFields() throws Exception {
        Result<List<String>> original = Result.ok(List.of("a", "b"));
        String json = mapper.writeValueAsString(original);

        Result<List<String>> decoded = mapper.readValue(json, new TypeReference<Result<List<String>>>() {
        });
        assertEquals(200, decoded.getCode());
        assertEquals("success", decoded.getMessage());
        assertEquals(List.of("a", "b"), decoded.getData());
    }

    @Test
    void deserialization_worksWithoutDefaultConstructor() throws Exception {
        // 回归测试：Result 无默认构造器，依赖 @JsonCreator 完成反序列化
        String json = "{\"code\":200,\"message\":\"success\",\"data\":[1,2,3]}";
        Result<List<Integer>> decoded = mapper.readValue(json, new TypeReference<Result<List<Integer>>>() {
        });
        assertEquals(200, decoded.getCode());
        assertEquals(List.of(1, 2, 3), decoded.getData());
    }
}
