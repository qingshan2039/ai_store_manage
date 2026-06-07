package com.aistore.module.barcode;

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

/** 条码接口集成测试（真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BarcodeControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    private int createLevel() throws Exception {
        String spu = mockMvc.perform(post("/api/spus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuCode\":\"BCSPU-1\",\"spuName\":\"BCSPU名\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int spuId = JsonPath.read(spu, "$.id");
        String sku = mockMvc.perform(post("/api/skus").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"spuId\":" + spuId + ",\"skuCode\":\"BCSKU-1\",\"skuName\":\"BCSKU名\",\"itemType\":\"RAW\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int skuId = JsonPath.read(sku, "$.id");
        String lvl = mockMvc.perform(post("/api/packaging-levels").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"levelName\":\"箱\",\"levelSeq\":2,\"unitCode\":\"CTN\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(lvl, "$.id");
    }

    @Test
    void create_returns201_withLevelName() throws Exception {
        int levelId = createLevel();
        mockMvc.perform(post("/api/barcodes").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"levelId\":" + levelId + ",\"barcode\":\"6901234500015\",\"barcodeType\":\"EAN13\",\"isPrimary\":1}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.barcode").value("6901234500015"))
                .andExpect(jsonPath("$.barcodeType").value("EAN13"))
                .andExpect(jsonPath("$.levelName").value("箱"));
    }

    @Test
    void create_duplicateBarcode_returns409() throws Exception {
        int levelId = createLevel();
        String body = "{\"levelId\":" + levelId + ",\"barcode\":\"6901234500099\",\"barcodeType\":\"EAN13\"}";
        mockMvc.perform(post("/api/barcodes").contentType(MediaType.APPLICATION_JSON).content(utf8(body))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/barcodes").contentType(MediaType.APPLICATION_JSON).content(utf8(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_BARCODE"));
    }

    @Test
    void create_badLevel_returns404() throws Exception {
        mockMvc.perform(post("/api/barcodes").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"levelId\":999999,\"barcode\":\"690NOPE\",\"barcodeType\":\"EAN13\"}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PACKAGING_LEVEL_NOT_FOUND"));
    }

    @Test
    void create_missingType_returns400() throws Exception {
        int levelId = createLevel();
        mockMvc.perform(post("/api/barcodes").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"levelId\":" + levelId + ",\"barcode\":\"690NOTYPE\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int levelId = createLevel();
        String body = mockMvc.perform(post("/api/barcodes").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"levelId\":" + levelId + ",\"barcode\":\"6901234500200\",\"barcodeType\":\"ITF14\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/barcodes/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/barcodes/" + id)).andExpect(status().isNotFound());
    }
}
