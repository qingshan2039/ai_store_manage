package com.aistore.module.vehicle;

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

/** 车辆接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void create_returns201_withOtherCrewName() throws Exception {
        // 常态司机/跟车员用 OTHER 替补：显示名取替补文本
        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"TEST-A1\",\"defaultDriverOther\":\"临时老王\",\"defaultEscortOther\":\"临时小李\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.plateNo").value("TEST-A1"))
                .andExpect(jsonPath("$.defaultDriverName").value("临时老王"))
                .andExpect(jsonPath("$.defaultEscortName").value("临时小李"))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_withSeededDriverUserId_resolvesUserName() throws Exception {
        String users = mockMvc.perform(get("/api/users").param("departmentType", "TRANSPORT").param("pageSize", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Number driverId = JsonPath.read(users, "$.items[0].id");
        String driverName = JsonPath.read(users, "$.items[0].name");

        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"TEST-U1\",\"defaultDriverUserId\":" + driverId + "}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.defaultDriverUserId").value(driverId))
                .andExpect(jsonPath("$.defaultDriverName").value(driverName));
    }

    @Test
    void create_duplicatePlate_returns409() throws Exception {
        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"TEST-DUP\"}")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"TEST-DUP\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_VEHICLE_PLATE"));
    }

    @Test
    void create_missingPlate_returns400() throws Exception {
        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON).content(utf8("{}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/vehicles/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("VEHICLE_NOT_FOUND"));
    }

    @Test
    void updateStatus_returns200() throws Exception {
        String body = mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"TEST-ST\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(patch("/api/vehicles/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"status\":0}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));
    }

    @Test
    void update_switchDriverFromOtherToNull_clearsField() throws Exception {
        String body = mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"TEST-SW\",\"defaultDriverOther\":\"老王\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        // 更新时不带常态司机 → 应清空（IGNORED 策略允许写 null）
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON).content(utf8("{\"plateNo\":\"TEST-SW\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defaultDriverName").doesNotExist());
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"plateNo\":\"TEST-DEL\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/vehicles/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/vehicles/" + id)).andExpect(status().isNotFound());
    }
}
