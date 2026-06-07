package com.aistore.module.department;

import com.aistore.AbstractPostgresTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 部门接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库）。
 * 每个用例在事务中执行并回滚，互不影响；断言只用 ASCII 字段，规避响应字符集差异；
 * 请求体含中文时以 UTF-8 字节发送。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DepartmentControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void createDepartment_returns201_withGeneratedId() throws Exception {
        mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"name\":\"测试技术部\",\"code\":\"TECH\",\"type\":\"OFFICE\",\"sort\":50}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("TECH"))
                .andExpect(jsonPath("$.type").value("OFFICE"))
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    void listDepartments_returnsSeededEight() throws Exception {
        mockMvc.perform(get("/api/departments").param("pageSize", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(8))
                .andExpect(jsonPath("$.items.length()").value(8));
    }

    @Test
    void listDepartments_filterByType() throws Exception {
        mockMvc.perform(get("/api/departments").param("type", "WAREHOUSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].code").value("WH"));
    }

    @Test
    void getById_thenUpdate_thenStatus() throws Exception {
        // 详情（种子 id=1）
        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("WH"));
        // 更新 sort / type
        mockMvc.perform(put("/api/departments/1").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"sort\":99,\"type\":\"PRODUCTION\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sort").value(99))
                .andExpect(jsonPath("$.type").value("PRODUCTION"));
        // 禁用
        mockMvc.perform(patch("/api/departments/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"status\":0}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"name\":\"运输部\",\"code\":\"NEWCODE\",\"type\":\"TRANSPORT\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_DEPARTMENT_NAME"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"name\":\"全新部门\",\"code\":\"WH\",\"type\":\"WAREHOUSE\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_DEPARTMENT_CODE"));
    }

    @Test
    void create_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/departments/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("DEPARTMENT_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/departments").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"name\":\"待删除部门\",\"code\":\"DELME\",\"type\":\"OFFICE\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/departments/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/departments/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_departmentInUse_returns409() throws Exception {
        // 在种子部门 id=2（运输部）下创建一个用户
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"employeeNo\":\"IT-1\",\"username\":\"ituser1\",\"password\":\"Passw0rd\","
                                + "\"name\":\"集成测试用户\",\"phoneNumber\":\"13800002222\",\"departmentId\":2}")))
                .andExpect(status().isCreated());
        // 删除被占用的部门 → 409
        mockMvc.perform(delete("/api/departments/2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEPARTMENT_IN_USE"));
    }
}
