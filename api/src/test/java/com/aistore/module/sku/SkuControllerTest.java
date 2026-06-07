package com.aistore.module.sku;

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

/** SKU 接口集成测试（含 jsonb spec 回读、同尺寸多规格、spuId 校验；真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SkuControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    /** 创建一个 SPU（品类 CORE 为种子）并返回 id。 */
    private int createSpu(String code, String name) throws Exception {
        String body = mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"" + code + "\",\"spuName\":\"" + name + "\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(body, "$.id");
    }

    @Test
    void create_returns201_withJsonbSpecAndSpuName() throws Exception {
        int spuId = createSpu("SPU-K1", "纸管SPU-K1");
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"SK-340480-A\",\"skuName\":\"纸管340x480规格A\",\"itemType\":\"RAW\","
                                + "\"lengthMm\":340,\"widthMm\":480,\"thicknessMm\":5,\"spec\":{\"material\":\"再生纸\",\"grade\":\"A\"}}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.spuName").value("纸管SPU-K1"))
                .andExpect(jsonPath("$.itemType").value("RAW"))
                .andExpect(jsonPath("$.lengthMm").value(340))
                .andExpect(jsonPath("$.spec.material").value("再生纸"))
                .andExpect(jsonPath("$.spec.grade").value("A"))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_sameSizeDifferentSpec_bothSucceed() throws Exception {
        int spuId = createSpu("SPU-K2", "纸管SPU-K2");
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"SK-A\",\"skuName\":\"规格A\",\"itemType\":\"RAW\",\"lengthMm\":340,\"widthMm\":480}")))
                .andExpect(status().isCreated());
        // 同尺寸不同规格 → 另一个 SKU（需求1）
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"SK-B\",\"skuName\":\"规格B\",\"itemType\":\"RAW\",\"lengthMm\":340,\"widthMm\":480}")))
                .andExpect(status().isCreated());
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        int spuId = createSpu("SPU-K3", "纸管SPU-K3");
        String sku = "{\"spuId\":" + spuId + ",\"skuCode\":\"SK-DUP\",\"skuName\":\"规格\",\"itemType\":\"RAW\"}";
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON).content(utf8(sku)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"SK-DUP\",\"skuName\":\"另一个\",\"itemType\":\"SEMI\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_SKU_CODE"));
    }

    @Test
    void create_badSpuId_returns404() throws Exception {
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":999999,\"skuCode\":\"SK-NOSPU\",\"skuName\":\"无SPU\",\"itemType\":\"RAW\"}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SPU_NOT_FOUND"));
    }

    @Test
    void create_missingItemType_returns400() throws Exception {
        int spuId = createSpu("SPU-K4", "纸管SPU-K4");
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"SK-NOTYPE\",\"skuName\":\"缺类型\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void list_filterBySpu_returnsItems() throws Exception {
        int spuId = createSpu("SPU-K5", "纸管SPU-K5");
        mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"SK-L1\",\"skuName\":\"规格\",\"itemType\":\"FINISHED\"}")))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/skus").param("spuId", String.valueOf(spuId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].spuName").value("纸管SPU-K5"))
                .andExpect(jsonPath("$.items[0].itemType").value("FINISHED"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/skus/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SKU_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int spuId = createSpu("SPU-K6", "纸管SPU-K6");
        String body = mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"SK-DEL\",\"skuName\":\"待删\",\"itemType\":\"RAW\"}")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/skus/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/skus/" + id)).andExpect(status().isNotFound());
    }
}
