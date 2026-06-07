package com.aistore.module.spu;

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

/** SPU 接口集成测试（categoryCode 引用 V5 种子品类 CORE；真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SpuControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static final String SPU_JSON =
            "{\"spuCode\":\"SPU-T1\",\"spuName\":\"测试3寸纸管\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void create_returns201_withCategoryName() throws Exception {
        mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON).content(utf8(SPU_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.spuCode").value("SPU-T1"))
                .andExpect(jsonPath("$.categoryCode").value("CORE"))
                .andExpect(jsonPath("$.categoryName").value("纸管"))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON).content(utf8(SPU_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"SPU-T2\",\"spuName\":\"测试3寸纸管\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_SPU_NAME"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON).content(utf8(SPU_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"SPU-T1\",\"spuName\":\"另一个\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_SPU_CODE"));
    }

    @Test
    void create_badCategory_returns404() throws Exception {
        mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"SPU-T3\",\"spuName\":\"坏品类\",\"categoryCode\":\"NOPE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MATERIAL_CATEGORY_NOT_FOUND"));
    }

    @Test
    void create_missingBaseUnit_returns400() throws Exception {
        mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"SPU-T4\",\"spuName\":\"缺单位\",\"categoryCode\":\"CORE\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/spus/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SPU_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON).content(utf8(SPU_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/spus/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/spus/" + id)).andExpect(status().isNotFound());
    }
}
