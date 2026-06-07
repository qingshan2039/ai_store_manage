package com.aistore.module.pallet;

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

/** 托盘类型接口集成测试（ISO 规格；真实 PostgreSQL：dev 容器 ai_store_test 库）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PalletTypeControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    /** 大托盘 1200×1000（ISO6780） */
    private static final String PALLET_JSON =
            "{\"code\":\"PLT-TL\",\"name\":\"大托盘 1200x1000\",\"length\":1200,\"width\":1000,"
            + "\"tareWeight\":25,\"maxLoad\":1500,\"maxStack\":5,\"remark\":\"ISO6780\"}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void create_returns201_withDimensionsAndDefaults() throws Exception {
        mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON).content(utf8(PALLET_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("PLT-TL"))
                .andExpect(jsonPath("$.length").value(1200))
                .andExpect(jsonPath("$.width").value(1000))
                .andExpect(jsonPath("$.maxStack").value(5))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON).content(utf8(PALLET_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"PLT-TX\",\"name\":\"大托盘 1200x1000\",\"length\":1200,\"width\":1000}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_PALLET_TYPE_NAME"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON).content(utf8(PALLET_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"PLT-TL\",\"name\":\"全新托盘\",\"length\":800,\"width\":600}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_PALLET_TYPE_CODE"));
    }

    @Test
    void create_missingLength_returns400() throws Exception {
        mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"PLT-TN\",\"name\":\"缺长度托盘\",\"width\":600}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void list_returnsItemsArray() throws Exception {
        mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON).content(utf8(PALLET_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/pallet-types").param("keyword", "大托盘"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/pallet-types/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PALLET_TYPE_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/pallet-types").contentType(MediaType.APPLICATION_JSON).content(utf8(PALLET_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/pallet-types/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/pallet-types/" + id)).andExpect(status().isNotFound());
    }
}
