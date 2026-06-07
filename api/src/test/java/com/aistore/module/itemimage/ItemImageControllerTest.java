package com.aistore.module.itemimage;

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

/** 物料图片接口集成测试（真实 PostgreSQL）。 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemImageControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static byte[] utf8(String s) { return s.getBytes(StandardCharsets.UTF_8); }

    @Test
    void create_returns201_withDefaults() throws Exception {
        mockMvc.perform(post("/api/item-images").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":1,\"imageUrl\":\"https://example.com/a.jpg\",\"imageType\":\"实体\",\"isPrimary\":1}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/a.jpg"))
                .andExpect(jsonPath("$.isPrimary").value(1))
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_missingUrl_returns400() throws Exception {
        mockMvc.perform(post("/api/item-images").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":1,\"imageType\":\"实体\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void list_filterBySku_returnsItems() throws Exception {
        mockMvc.perform(post("/api/item-images").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":77,\"imageUrl\":\"https://example.com/b.jpg\"}")))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/item-images").param("skuId", "77"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].imageUrl").value("https://example.com/b.jpg"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/item-images/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ITEM_IMAGE_NOT_FOUND"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/item-images").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"skuId\":1,\"imageUrl\":\"https://example.com/c.jpg\"}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");
        mockMvc.perform(delete("/api/item-images/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/item-images/" + id)).andExpect(status().isNotFound());
    }
}
