package com.aistore.module.packagingrelation;

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

/** 包装关系接口集成测试（真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PackagingRelationControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    private int createSku() throws Exception {
        String spu = mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"PRSPU-1\",\"spuName\":\"PRSPU名\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int spuId = JsonPath.read(spu, "$.id");
        String sku = mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"PRSKU-1\",\"skuName\":\"PRSKU名\",\"itemType\":\"RAW\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(sku, "$.id");
    }

    private int createLevel(int skuId, String name, int seq) throws Exception {
        String body = mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"levelName\":\"" + name + "\",\"levelSeq\":" + seq + ",\"unitCode\":\"U" + seq + "\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(body, "$.id");
    }

    @Test
    void create_returns201_withLevelNames() throws Exception {
        int skuId = createSku();
        int parent = createLevel(skuId, "托", 3);
        int child = createLevel(skuId, "箱", 2);
        mockMvc.perform(post("/api/packaging-relations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"parentLevelId\":" + parent + ",\"childLevelId\":" + child + ",\"childQty\":500,\"isFixedQty\":1}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parentLevelName").value("托"))
                .andExpect(jsonPath("$.childLevelName").value("箱"))
                .andExpect(jsonPath("$.childQty").value(500))
                .andExpect(jsonPath("$.isFixedQty").value(1));
    }

    @Test
    void create_duplicate_returns409() throws Exception {
        int skuId = createSku();
        int parent = createLevel(skuId, "托", 3);
        int child = createLevel(skuId, "箱", 2);
        String body = "{\"parentLevelId\":" + parent + ",\"childLevelId\":" + child + ",\"childQty\":500}";
        mockMvc.perform(post("/api/packaging-relations").contentType(MediaType.APPLICATION_JSON).content(utf8(body))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/packaging-relations").contentType(MediaType.APPLICATION_JSON).content(utf8(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_PACKAGING_RELATION"));
    }

    @Test
    void create_badLevel_returns404() throws Exception {
        mockMvc.perform(post("/api/packaging-relations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"parentLevelId\":999998,\"childLevelId\":999999,\"childQty\":10}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PACKAGING_LEVEL_NOT_FOUND"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/packaging-relations/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PACKAGING_RELATION_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int skuId = createSku();
        int parent = createLevel(skuId, "托", 3);
        int child = createLevel(skuId, "箱", 2);
        String body = mockMvc.perform(post("/api/packaging-relations").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"parentLevelId\":" + parent + ",\"childLevelId\":" + child + ",\"childQty\":500}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/packaging-relations/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/packaging-relations/" + id)).andExpect(status().isNotFound());
    }
}
