package com.aistore.module.unitconversion;

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

/** 计量换算接口集成测试（真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UnitConversionControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    private int createSku() throws Exception {
        String spu = mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"UCSPU-1\",\"spuName\":\"UCSPU名\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int spuId = JsonPath.read(spu, "$.id");
        String sku = mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"UCSKU-1\",\"skuName\":\"UCSKU名\",\"itemType\":\"RAW\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(sku, "$.id");
    }

    @Test
    void create_returns201_withSkuName() throws Exception {
        int skuId = createSku();
        mockMvc.perform(post("/api/unit-conversions").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"fromUnit\":\"ROLL\",\"toUnit\":\"M2\",\"factor\":300}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fromUnit").value("ROLL"))
                .andExpect(jsonPath("$.toUnit").value("M2"))
                .andExpect(jsonPath("$.factor").value(300))
                .andExpect(jsonPath("$.skuName").value("UCSKU名"));
    }

    @Test
    void create_duplicate_returns409() throws Exception {
        int skuId = createSku();
        String body = "{\"skuId\":" + skuId + ",\"fromUnit\":\"ROLL\",\"toUnit\":\"M2\",\"factor\":300}";
        mockMvc.perform(post("/api/unit-conversions").contentType(MediaType.APPLICATION_JSON).content(utf8(body))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/unit-conversions").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"fromUnit\":\"ROLL\",\"toUnit\":\"M2\",\"factor\":999}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_UNIT_CONVERSION"));
    }

    @Test
    void create_badSku_returns404() throws Exception {
        mockMvc.perform(post("/api/unit-conversions").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":999999,\"fromUnit\":\"ROLL\",\"toUnit\":\"M2\",\"factor\":300}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SKU_NOT_FOUND"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/unit-conversions/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("UNIT_CONVERSION_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int skuId = createSku();
        String body = mockMvc.perform(post("/api/unit-conversions").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"fromUnit\":\"CTN\",\"toUnit\":\"ROLL\",\"factor\":12}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/unit-conversions/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/unit-conversions/" + id)).andExpect(status().isNotFound());
    }
}
