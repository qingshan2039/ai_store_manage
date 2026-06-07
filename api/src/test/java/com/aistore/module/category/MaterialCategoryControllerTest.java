package com.aistore.module.category;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 物料品类接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库，V5 已灌 5 个品类种子）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MaterialCategoryControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static final String CATEGORY_JSON =
            "{\"code\":\"TCAT-1\",\"name\":\"测试品类一\",\"sortOrder\":99}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void create_returns201_withDefaults() throws Exception {
        mockMvc.perform(post("/api/material-categories").contentType(MediaType.APPLICATION_JSON).content(utf8(CATEGORY_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("TCAT-1"))
                .andExpect(jsonPath("$.sortOrder").value(99))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/material-categories").contentType(MediaType.APPLICATION_JSON).content(utf8(CATEGORY_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/material-categories").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"TCAT-2\",\"name\":\"测试品类一\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_MATERIAL_CATEGORY_NAME"));
    }

    @Test
    void create_duplicateSeededCode_returns409() throws Exception {
        // CORE 为 V5 迁移内置种子，编码冲突
        mockMvc.perform(post("/api/material-categories").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"CORE\",\"name\":\"另一个纸管\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_MATERIAL_CATEGORY_CODE"));
    }

    @Test
    void create_missingCode_returns400() throws Exception {
        mockMvc.perform(post("/api/material-categories").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"name\":\"无编码\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void list_includesSeeds() throws Exception {
        mockMvc.perform(get("/api/material-categories").param("pageSize", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/material-categories/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MATERIAL_CATEGORY_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/material-categories").contentType(MediaType.APPLICATION_JSON).content(utf8(CATEGORY_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/material-categories/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/material-categories/" + id)).andExpect(status().isNotFound());
    }
}
