package com.aistore.module.zone;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 库区接口集成测试（隶属仓库；真实 PostgreSQL：dev 容器 ai_store_test 库）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ZoneControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    /** 创建一个仓库并返回其 id（库区必须隶属仓库）。 */
    private int createWarehouse(String code, String name) throws Exception {
        String body = mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"" + code + "\",\"name\":\"" + name + "\",\"type\":\"RAW\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(body, "$.id");
    }

    @Test
    void create_returns201_withWarehouseName() throws Exception {
        int whId = createWarehouse("WH-Z1", "库区测试仓A");
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"Z-A\",\"name\":\"收货区\",\"type\":\"收货\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.warehouseId").value(whId))
                .andExpect(jsonPath("$.warehouseName").value("库区测试仓A"))
                .andExpect(jsonPath("$.code").value("Z-A"))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateCodeSameWarehouse_returns409() throws Exception {
        int whId = createWarehouse("WH-Z2", "库区测试仓B");
        String z = "{\"warehouseId\":" + whId + ",\"code\":\"Z-A\",\"name\":\"区一\"}";
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON).content(utf8(z)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"Z-A\",\"name\":\"区二\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_ZONE_CODE"));
    }

    @Test
    void create_sameCodeDifferentWarehouse_returns201() throws Exception {
        int wh1 = createWarehouse("WH-Z3", "库区测试仓C");
        int wh2 = createWarehouse("WH-Z4", "库区测试仓D");
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + wh1 + ",\"code\":\"Z-A\",\"name\":\"区一\"}")))
                .andExpect(status().isCreated());
        // 不同仓库下相同编码允许
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + wh2 + ",\"code\":\"Z-A\",\"name\":\"区一\"}")))
                .andExpect(status().isCreated());
    }

    @Test
    void create_missingWarehouseId_returns400() throws Exception {
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"Z-A\",\"name\":\"无仓库\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void list_filterByWarehouse_returnsItems() throws Exception {
        int whId = createWarehouse("WH-Z5", "库区测试仓E");
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"Z-A\",\"name\":\"区一\"}")))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/zones").param("warehouseId", String.valueOf(whId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].warehouseName").value("库区测试仓E"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/zones/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ZONE_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int whId = createWarehouse("WH-Z6", "库区测试仓F");
        String body = mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"Z-A\",\"name\":\"区一\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/zones/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/zones/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void update_changesCode() throws Exception {
        int whId = createWarehouse("WH-Z7", "库区测试仓G");
        String body = mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"Z-A\",\"name\":\"区一\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(put("/api/zones/" + id).contentType(MediaType.APPLICATION_JSON).content(utf8("{\"code\":\"Z-A2\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("Z-A2"));
    }

    @Test
    void update_duplicateCodeSameWarehouse_returns409() throws Exception {
        int whId = createWarehouse("WH-Z8", "库区测试仓H");
        mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"Z-A\",\"name\":\"区一\"}")))
                .andExpect(status().isCreated());
        String body2 = mockMvc.perform(post("/api/zones").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"Z-B\",\"name\":\"区二\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id2 = JsonPath.read(body2, "$.id");
        // 把 Z-B 改成已存在的 Z-A → 409
        mockMvc.perform(put("/api/zones/" + id2).contentType(MediaType.APPLICATION_JSON).content(utf8("{\"code\":\"Z-A\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_ZONE_CODE"));
    }
}
