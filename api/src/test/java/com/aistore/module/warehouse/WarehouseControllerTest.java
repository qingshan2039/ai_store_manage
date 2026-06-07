package com.aistore.module.warehouse;

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

/** 仓库接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WarehouseControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static final String WAREHOUSE_JSON =
            "{\"code\":\"WH-T1\",\"name\":\"测试原料仓\",\"type\":\"RAW\",\"remark\":\"存放原材料\"}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void create_returns201_withTypeAndDefaults() throws Exception {
        mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON).content(utf8(WAREHOUSE_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("WH-T1"))
                .andExpect(jsonPath("$.type").value("RAW"))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON).content(utf8(WAREHOUSE_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"WH-T2\",\"name\":\"测试原料仓\",\"type\":\"SEMI\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_WAREHOUSE_NAME"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON).content(utf8(WAREHOUSE_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"WH-T1\",\"name\":\"另一仓库\",\"type\":\"FINISHED\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_WAREHOUSE_CODE"));
    }

    @Test
    void create_missingType_returns400() throws Exception {
        mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"WH-T3\",\"name\":\"无类型仓库\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void list_filterByType_returnsItems() throws Exception {
        mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON).content(utf8(WAREHOUSE_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/warehouses").param("type", "RAW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/warehouses/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WAREHOUSE_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON).content(utf8(WAREHOUSE_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/warehouses/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/warehouses/" + id)).andExpect(status().isNotFound());
    }
}
