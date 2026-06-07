package com.aistore.module.fuel;

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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 打油记录接口集成测试（含 images jsonb 多图、车牌回填）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FuelRecordControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private int createVehicle(String plate) throws Exception {
        String body = mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"" + plate + "\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(body, "$.id");
    }

    @Test
    void create_returns201_withImagesAndPlate() throws Exception {
        int vId = createVehicle("FUEL-V1");
        mockMvc.perform(post("/api/fuel-records").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"vehicleId\":" + vId + ",\"fuelDate\":\"2026-06-01\",\"liters\":50.50,"
                                + "\"amount\":400.00,\"images\":[\"/api/files/2026/06/a.jpg\",\"/api/files/2026/06/b.jpg\"]}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.vehicleId").value(vId))
                .andExpect(jsonPath("$.vehiclePlateNo").value("FUEL-V1"))
                .andExpect(jsonPath("$.fuelDate").value("2026-06-01"))
                .andExpect(jsonPath("$.images", hasSize(2)))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_missingVehicle_returns400() throws Exception {
        mockMvc.perform(post("/api/fuel-records").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"fuelDate\":\"2026-06-01\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void list_filterByVehicle_returnsItemWithImageCount() throws Exception {
        int vId = createVehicle("FUEL-V2");
        mockMvc.perform(post("/api/fuel-records").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"vehicleId\":" + vId + ",\"fuelDate\":\"2026-06-02\",\"images\":[\"/api/files/x.jpg\"]}")))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/fuel-records").param("vehicleId", String.valueOf(vId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].vehiclePlateNo").value("FUEL-V2"))
                .andExpect(jsonPath("$.items[0].imageCount").value(1));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/fuel-records/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FUEL_RECORD_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int vId = createVehicle("FUEL-V3");
        String body = mockMvc.perform(post("/api/fuel-records").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"vehicleId\":" + vId + ",\"fuelDate\":\"2026-06-03\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/fuel-records/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/fuel-records/" + id)).andExpect(status().isNotFound());
    }
}
