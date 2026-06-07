package com.aistore.module.user;

import com.aistore.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static final String USER_JSON =
            "{\"employeeNo\":\"WH-2026\",\"username\":\"zhangsan\",\"password\":\"Passw0rd\","
            + "\"name\":\"张三\",\"phoneNumber\":\"13800138000\",\"departmentId\":1}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void createUser_returns201_andPasswordNotInResponse() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(utf8(USER_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("zhangsan"))
                .andExpect(jsonPath("$.employeeNo").value("WH-2026"))
                .andExpect(jsonPath("$.status").value(1))
                // 密码与逻辑删除标记永不出现在响应中
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void createUser_duplicateUsername_returns409() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(utf8(USER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"employeeNo\":\"WH-2027\",\"username\":\"zhangsan\",\"password\":\"Passw0rd\","
                                + "\"name\":\"张三二\",\"phoneNumber\":\"13800138001\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_USERNAME"));
    }

    @Test
    void createUser_invalidPhone_returns400() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"employeeNo\":\"WH-9\",\"username\":\"baduser\",\"password\":\"Passw0rd\","
                                + "\"name\":\"测试\",\"phoneNumber\":\"123\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void listUsers_returns200() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(utf8(USER_JSON)))
                .andExpect(status().isCreated());
        // 列表按 created_at DESC，刚建的用户位列首位；V6 种子也在库中故 total 取 >=1
        mockMvc.perform(get("/api/users").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.items[0].employeeNo").value("WH-2026"));
    }

    @Test
    void listUsers_filterByDepartmentType_returnsMatchingUsers() throws Exception {
        // V6 种子在运输部灌入 4 名司机，按部门类型过滤应至少返回 4 条
        mockMvc.perform(get("/api/users").param("departmentType", "TRANSPORT").param("pageSize", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").value(greaterThanOrEqualTo(4)));
        // 无种子用户的部门类型返回空
        mockMvc.perform(get("/api/users").param("departmentType", "FINANCE").param("pageSize", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0));
    }
}
