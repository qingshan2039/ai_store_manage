package com.aistore.module.lpn;

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

/** 托盘实例接口集成测试（真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LpnControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    private int createWarehouse(String code) throws Exception {
        String body = mockMvc.perform(post("/api/warehouses").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"" + code + "\",\"name\":\"" + code + "名\",\"type\":\"FINISHED\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(body, "$.id");
    }

    private int createPalletType(String code) throws Exception {
        String body = mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"" + code + "\",\"name\":\"" + code + "托\",\"length\":1200,\"width\":1000}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(body, "$.id");
    }

    @Test
    void create_returns201_withDefaultStatusAndNames() throws Exception {
        int wh = createWarehouse("WH-P1");
        int pt = createPalletType("PT-P1");
        mockMvc.perform(post("/api/lpns").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"lpnCode\":\"SSCC-P0001\",\"palletTypeId\":" + pt + ",\"warehouseId\":" + wh + "}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lpnCode").value("SSCC-P0001"))
                .andExpect(jsonPath("$.status").value("IN_STOCK"))
                .andExpect(jsonPath("$.palletTypeName").value("PT-P1托"))
                .andExpect(jsonPath("$.warehouseName").value("WH-P1名"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        int wh = createWarehouse("WH-P2");
        int pt = createPalletType("PT-P2");
        String body = "{\"lpnCode\":\"SSCC-P0002\",\"palletTypeId\":" + pt + ",\"warehouseId\":" + wh + "}";
        mockMvc.perform(post("/api/lpns").contentType(MediaType.APPLICATION_JSON).content(utf8(body))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/lpns").contentType(MediaType.APPLICATION_JSON).content(utf8(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_LPN_CODE"));
    }

    @Test
    void create_badPalletType_returns404() throws Exception {
        int wh = createWarehouse("WH-P3");
        mockMvc.perform(post("/api/lpns").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"lpnCode\":\"SSCC-P0003\",\"palletTypeId\":999999,\"warehouseId\":" + wh + "}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PALLET_TYPE_NOT_FOUND"));
    }

    @Test
    void updateStatus_returns200_withNewStatus() throws Exception {
        int wh = createWarehouse("WH-P4");
        int pt = createPalletType("PT-P4");
        String body = mockMvc.perform(post("/api/lpns").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"lpnCode\":\"SSCC-P0004\",\"palletTypeId\":" + pt + ",\"warehouseId\":" + wh + "}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(patch("/api/lpns/" + id + "/status").contentType(MediaType.APPLICATION_JSON).content(utf8("{\"status\":\"EMPTY\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EMPTY"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/lpns/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("LPN_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int wh = createWarehouse("WH-P5");
        int pt = createPalletType("PT-P5");
        String body = mockMvc.perform(post("/api/lpns").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"lpnCode\":\"SSCC-P0005\",\"palletTypeId\":" + pt + ",\"warehouseId\":" + wh + "}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/lpns/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/lpns/" + id)).andExpect(status().isNotFound());
    }
}
