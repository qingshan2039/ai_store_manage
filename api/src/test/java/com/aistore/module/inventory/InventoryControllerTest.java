package com.aistore.module.inventory;

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

/** 库存接口集成测试，含库存统计（需求②：库存数量/托盘数量/整托尾托）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InventoryControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    private int post201(String path, String body) throws Exception {
        String resp = mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(utf8(body)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return JsonPath.read(resp, "$.id");
    }

    private int createSku(String suffix) throws Exception {
        int spuId = post201("/api/spus", "{\"spuCode\":\"INVSPU-" + suffix + "\",\"spuName\":\"INVSPU" + suffix + "\",\"categoryCode\":\"CORE\",\"baseUnit\":\"PCS\"}");
        return post201("/api/skus", "{\"spuId\":" + spuId + ",\"skuCode\":\"INVSKU-" + suffix + "\",\"skuName\":\"INVSKU" + suffix + "\",\"itemType\":\"RAW\"}");
    }

    @Test
    void create_returns201_withAvailable() throws Exception {
        int skuId = createSku("A");
        mockMvc.perform(post("/api/inventory").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + ",\"qtyOnHand\":500,\"qtyReserved\":120}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.qtyOnHand").value(500))
                .andExpect(jsonPath("$.qtyReserved").value(120))
                .andExpect(jsonPath("$.qtyAvailable").value(380))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_badSku_returns404() throws Exception {
        mockMvc.perform(post("/api/inventory").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":999999,\"qtyOnHand\":10}")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SKU_NOT_FOUND"));
    }

    @Test
    void create_missingQty_returns400() throws Exception {
        int skuId = createSku("B");
        mockMvc.perform(post("/api/inventory").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":" + skuId + "}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/inventory/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INVENTORY_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        int skuId = createSku("C");
        int id = post201("/api/inventory", "{\"skuId\":" + skuId + ",\"qtyOnHand\":50}");
        mockMvc.perform(delete("/api/inventory/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/inventory/" + id)).andExpect(status().isNotFound());
    }

    /** 需求② 核心：两托 500 整托 + 480 尾托 → 总库存 980、托盘数 2、标准每托 500。 */
    @Test
    void summary_computesQtyPalletCountAndFullPartial() throws Exception {
        int skuId = createSku("S");
        // 包装链：托(seq3) → 箱(seq2)，定量 500；卷(seq1) 为基本单位
        int base = post201("/api/packaging-levels", "{\"skuId\":" + skuId + ",\"levelName\":\"卷\",\"levelSeq\":1,\"unitCode\":\"ROLL\",\"isBaseUnit\":1}");
        int box = post201("/api/packaging-levels", "{\"skuId\":" + skuId + ",\"levelName\":\"箱\",\"levelSeq\":2,\"unitCode\":\"CTN\"}");
        int plt = post201("/api/packaging-levels", "{\"skuId\":" + skuId + ",\"levelName\":\"托\",\"levelSeq\":3,\"unitCode\":\"PLT\"}");
        post201("/api/packaging-relations", "{\"parentLevelId\":" + plt + ",\"childLevelId\":" + box + ",\"childQty\":500,\"isFixedQty\":1}");
        // 仓库 + 托盘类型 + 两个托盘实例
        int wh = post201("/api/warehouses", "{\"code\":\"WH-INV\",\"name\":\"库存测试仓\",\"type\":\"FINISHED\"}");
        int pt = post201("/api/pallet-types", "{\"code\":\"PT-INV\",\"name\":\"库存测试托\",\"length\":1200,\"width\":1000}");
        int lpn1 = post201("/api/lpns", "{\"lpnCode\":\"SSCC-INV-1\",\"palletTypeId\":" + pt + ",\"warehouseId\":" + wh + "}");
        int lpn2 = post201("/api/lpns", "{\"lpnCode\":\"SSCC-INV-2\",\"palletTypeId\":" + pt + ",\"warehouseId\":" + wh + "}");
        // 库存：满托 500、尾托 480
        post201("/api/inventory", "{\"skuId\":" + skuId + ",\"lpnId\":" + lpn1 + ",\"qtyOnHand\":500}");
        post201("/api/inventory", "{\"skuId\":" + skuId + ",\"lpnId\":" + lpn2 + ",\"qtyOnHand\":480}");
        // ignore unused base level var
        assert base > 0;

        mockMvc.perform(get("/api/inventory/summary").param("skuId", String.valueOf(skuId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQty").value(980))
                .andExpect(jsonPath("$.palletCount").value(2))
                .andExpect(jsonPath("$.recordCount").value(2))
                .andExpect(jsonPath("$.standardPalletQty").value(500))
                .andExpect(jsonPath("$.pallets.length()").value(2));
    }

    @Test
    void summary_badSku_returns404() throws Exception {
        mockMvc.perform(get("/api/inventory/summary").param("skuId", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SKU_NOT_FOUND"));
    }
}
