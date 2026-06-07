package com.aistore.module.checkin;

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

/**
 * 司机打卡接口集成测试。
 * 打卡日期用远期（2026-01-xx）避开 V6 种子的“最近 7 天”记录，避免唯一键冲突。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DriverCheckinControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    /** 取一名种子运输司机（id + 姓名）。 */
    private Object[] seededDriver() throws Exception {
        String body = mockMvc.perform(get("/api/users").param("departmentType", "TRANSPORT").param("pageSize", "1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Number id = JsonPath.read(body, "$.items[0].id");
        String name = JsonPath.read(body, "$.items[0].name");
        return new Object[]{id, name};
    }

    @Test
    void create_returns201_withDriverName() throws Exception {
        Object[] d = seededDriver();
        mockMvc.perform(post("/api/driver-checkins").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"driverUserId\":" + d[0] + ",\"checkinDate\":\"2026-01-10\",\"checkinStatus\":\"NORMAL\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.driverName").value(d[1]))
                .andExpect(jsonPath("$.checkinStatus").value("NORMAL"))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateSameDriverDate_returns409() throws Exception {
        Object[] d = seededDriver();
        String json = "{\"driverUserId\":" + d[0] + ",\"checkinDate\":\"2026-01-11\"}";
        mockMvc.perform(post("/api/driver-checkins").contentType(MediaType.APPLICATION_JSON).content(utf8(json)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/driver-checkins").contentType(MediaType.APPLICATION_JSON).content(utf8(json)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_DRIVER_CHECKIN"));
    }

    @Test
    void create_otherDriver_returns201_withOtherName() throws Exception {
        mockMvc.perform(post("/api/driver-checkins").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"driverOther\":\"临时工小马\",\"checkinDate\":\"2026-01-12\"}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverName").value("临时工小马"))
                .andExpect(jsonPath("$.checkinStatus").value("NORMAL"));
    }

    @Test
    void create_missingDate_returns400() throws Exception {
        Object[] d = seededDriver();
        mockMvc.perform(post("/api/driver-checkins").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"driverUserId\":" + d[0] + "}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void create_noDriver_returns400() throws Exception {
        mockMvc.perform(post("/api/driver-checkins").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"checkinDate\":\"2026-01-13\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DRIVER_REQUIRED"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/driver-checkins/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("DRIVER_CHECKIN_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/driver-checkins").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"driverOther\":\"临时替班\",\"checkinDate\":\"2026-01-14\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/driver-checkins/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/driver-checkins/" + id)).andExpect(status().isNotFound());
    }
}
