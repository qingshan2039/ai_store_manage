package com.aistore.module.supplier;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 供应商接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SupplierControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static final String SUPPLIER_JSON =
            "{\"code\":\"SUP-T1\",\"name\":\"测试供应商一\",\"address\":\"上海市金山区石化大道1号\","
            + "\"contact\":\"王经理\",\"phone\":\"021-58880001\",\"email\":\"a@b.com\"}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void create_returns201_withDefaultsAndNoDeleted() throws Exception {
        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content(utf8(SUPPLIER_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("SUP-T1"))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content(utf8(SUPPLIER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"SUP-T2\",\"name\":\"测试供应商一\",\"address\":\"另一地址\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_SUPPLIER_NAME"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content(utf8(SUPPLIER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"SUP-T1\",\"name\":\"全新供应商\",\"address\":\"地址3\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_SUPPLIER_CODE"));
    }

    @Test
    void create_missingRequired_returns400() throws Exception {
        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"SUP-T3\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void list_returnsItemsArray() throws Exception {
        mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content(utf8(SUPPLIER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/suppliers").param("keyword", "测试供应商"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    void updateStatus_returns200_withNewStatus() throws Exception {
        String body = mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content(utf8(SUPPLIER_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(patch("/api/suppliers/" + id + "/status").contentType(MediaType.APPLICATION_JSON).content(utf8("{\"status\":0}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/suppliers/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SUPPLIER_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/suppliers").contentType(MediaType.APPLICATION_JSON).content(utf8(SUPPLIER_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/suppliers/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/suppliers/" + id)).andExpect(status().isNotFound());
    }
}
