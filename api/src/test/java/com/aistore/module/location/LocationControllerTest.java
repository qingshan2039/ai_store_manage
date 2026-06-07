package com.aistore.module.location;

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

/** 库位接口集成测试（真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocationControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    private int createWarehouse(String code, String name) throws Exception {
        String body = mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"" + code + "\",\"name\":\"" + name + "\",\"type\":\"RAW\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(body, "$.id");
    }

    @Test
    void create_returns201_withWarehouseName() throws Exception {
        int whId = createWarehouse("WH-L1", "库位测试仓A");
        mockMvc.perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"A-01-01\",\"locType\":\"货架\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.warehouseName").value("库位测试仓A"))
                .andExpect(jsonPath("$.code").value("A-01-01"))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateCodeSameWarehouse_returns409() throws Exception {
        int whId = createWarehouse("WH-L2", "库位测试仓B");
        String body = "{\"warehouseId\":" + whId + ",\"code\":\"A-01-01\"}";
        mockMvc.perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON).content(utf8(body))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON).content(utf8(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_LOCATION_CODE"));
    }

    @Test
    void create_badWarehouse_returns404() throws Exception {
        mockMvc.perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":999999,\"code\":\"A-01-01\"}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WAREHOUSE_NOT_FOUND"));
    }

    @Test
    void create_missingCode_returns400() throws Exception {
        int whId = createWarehouse("WH-L3", "库位测试仓C");
        mockMvc.perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + "}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/locations/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("LOCATION_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int whId = createWarehouse("WH-L4", "库位测试仓D");
        String body = mockMvc.perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"B-02-02\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/locations/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/locations/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void update_changesCodeAndType() throws Exception {
        int whId = createWarehouse("WH-L5", "库位测试仓E");
        String body = mockMvc.perform(post("/api/locations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"warehouseId\":" + whId + ",\"code\":\"C-01-01\",\"locType\":\"货架\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(put("/api/locations/" + id).contentType(MediaType.APPLICATION_JSON).content(utf8("{\"code\":\"C-01-02\",\"locType\":\"地堆\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("C-01-02"))
                .andExpect(jsonPath("$.locType").value("地堆"));
    }
}
