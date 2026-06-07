package com.aistore.module.packaginglevel;

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

/** 包装层级接口集成测试（真实 PostgreSQL；品类 CORE 为 V5 种子）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PackagingLevelControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    private int createSku(String spuCode, String skuCode) throws Exception {
        String spu = mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"" + spuCode + "\",\"spuName\":\"" + spuCode + "名\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int spuId = JsonPath.read(spu, "$.id");
        String sku = mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"" + skuCode + "\",\"skuName\":\"" + skuCode + "名\",\"itemType\":\"RAW\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(sku, "$.id");
    }

    @Test
    void create_returns201_withSkuName() throws Exception {
        int skuId = createSku("PLSPU-1", "PLSKU-1");
        mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"levelName\":\"箱\",\"levelSeq\":2,\"unitCode\":\"CTN\",\"isBaseUnit\":0}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.skuName").value("PLSKU-1名"))
                .andExpect(jsonPath("$.levelName").value("箱"))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateSeq_returns409() throws Exception {
        int skuId = createSku("PLSPU-2", "PLSKU-2");
        String body = "{\"skuId\":" + skuId + ",\"levelName\":\"卷\",\"levelSeq\":1,\"unitCode\":\"ROLL\"}";
        mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON).content(utf8(body))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"levelName\":\"箱\",\"levelSeq\":1,\"unitCode\":\"CTN\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_PACKAGING_LEVEL_SEQ"));
    }

    @Test
    void create_badSku_returns404() throws Exception {
        mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":999999,\"levelName\":\"箱\",\"levelSeq\":1,\"unitCode\":\"CTN\"}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SKU_NOT_FOUND"));
    }

    @Test
    void create_missingRequired_returns400() throws Exception {
        mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"levelName\":\"箱\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/packaging-levels/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PACKAGING_LEVEL_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int skuId = createSku("PLSPU-3", "PLSKU-3");
        String body = mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"levelName\":\"托\",\"levelSeq\":3,\"unitCode\":\"PLT\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/packaging-levels/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/packaging-levels/" + id)).andExpect(status().isNotFound());
    }
}
