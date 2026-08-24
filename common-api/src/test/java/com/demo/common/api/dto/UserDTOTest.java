package com.demo.common.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDTOTest {

    @Test
    void constructor_andAccessors_work() {
        UserDTO dto = new UserDTO(1L, "Alice");
        assertEquals(1L, dto.id());
        assertEquals("Alice", dto.name());
    }

    @Test
    void records_areValueBased() {
        UserDTO a = new UserDTO(1L, "Alice");
        UserDTO b = new UserDTO(1L, "Alice");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("UserDTO[id=1, name=Alice]", a.toString());
    }
}
