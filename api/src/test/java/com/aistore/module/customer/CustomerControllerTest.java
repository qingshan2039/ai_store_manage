package com.aistore.module.customer;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 顾客接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库）。
 * 每个用例在事务中执行并回滚；请求体含中文以 UTF-8 字节发送，断言只用 ASCII 字段。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    private static final String CUSTOMER_JSON =
            "{\"code\":\"CUST-001\",\"name\":\"上海示例贸易有限公司\",\"address\":\"上海市浦东新区世纪大道100号\","
            + "\"shipAddress\":\"上海市嘉定区物流园5号库\",\"contact\":\"王经理\",\"phone\":\"021-66889900\"}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void createCustomer_returns201_withShipAddress() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("CUST-001"))
                .andExpect(jsonPath("$.shipAddress").isString())
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"CUST-002\",\"name\":\"上海示例贸易有限公司\",\"address\":\"地址2\",\"shipAddress\":\"收货2\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_CUSTOMER_NAME"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"CUST-001\",\"name\":\"全新公司\",\"address\":\"地址3\",\"shipAddress\":\"收货3\"}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_CUSTOMER_CODE"));
    }

    @Test
    void create_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8("{}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CUSTOMER_NOT_FOUND"));
    }

    @Test
    void create_get_update_status_list() throws Exception {
        String body = mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CUST-001"));

        mockMvc.perform(put("/api/customers/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"shipAddress\":\"上海市青浦区物流中心8号库\"}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipAddress").isString());

        mockMvc.perform(patch("/api/customers/" + id + "/status").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"status\":0}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(0));

        mockMvc.perform(get("/api/customers").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].code").value("CUST-001"));
    }

    @Test
    void delete_returns204_thenNotFound() throws Exception {
        String body = mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        mockMvc.perform(delete("/api/customers/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isNotFound());
    }
}
