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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 顾客接口集成测试（真实 PostgreSQL：dev 容器 ai_store_test 库）。
 * 覆盖一对多送货地址（含 remark）：创建多地址、整列表替换、必填校验等。
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerControllerTest extends AbstractPostgresTest {

    @Autowired
    MockMvc mockMvc;

    /** 单个送货地址 */
    private static final String CUSTOMER_JSON =
            "{\"code\":\"CUST-001\",\"name\":\"上海示例贸易有限公司\",\"address\":\"上海市浦东新区世纪大道100号\","
            + "\"shipAddresses\":[{\"address\":\"上海市嘉定区物流园5号库\"}],\"contact\":\"王经理\"}";

    /** 连锁客户：3 个送货地址，其中一个带 remark */
    private static final String CHAIN_JSON =
            "{\"code\":\"CHAIN-001\",\"name\":\"全国连锁超市股份有限公司\",\"address\":\"北京市朝阳区总部\","
            + "\"shipAddresses\":[{\"address\":\"上海市徐汇店仓库\"},"
            + "{\"address\":\"北京市朝阳店仓库\",\"remark\":\"客户报错地址已更正\"},"
            + "{\"address\":\"广州市天河店仓库\"}],\"contact\":\"采购部\"}";

    private static byte[] utf8(String json) {
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void createCustomer_returns201_withSingleShipAddress() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.code").value("CUST-001"))
                .andExpect(jsonPath("$.shipAddresses").isArray())
                .andExpect(jsonPath("$.shipAddresses.length()").value(1))
                .andExpect(jsonPath("$.shipAddresses[0].id").isNumber())
                .andExpect(jsonPath("$.shipAddresses[0].address").isString())
                .andExpect(jsonPath("$.status").value(1))
                .andExpect(jsonPath("$.deleted").doesNotExist());
    }

    @Test
    void createChainCustomer_returns201_withMultipleShipAddresses() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CHAIN_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CHAIN-001"))
                .andExpect(jsonPath("$.shipAddresses.length()").value(3))
                // 第二个地址带 remark
                .andExpect(jsonPath("$.shipAddresses[1].remark").isString());
    }

    @Test
    void update_replacesShipAddressList() throws Exception {
        String body = mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CHAIN_JSON)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipAddresses.length()").value(3))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        int id = JsonPath.read(body, "$.id");

        // 整列表替换为 2 个（其一带 remark）
        mockMvc.perform(put("/api/customers/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"shipAddresses\":[{\"address\":\"上海市徐汇店新仓库\",\"remark\":\"搬迁后新地址\"},"
                                + "{\"address\":\"深圳市南山店仓库\"}]}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shipAddresses.length()").value(2));
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"CUST-002\",\"name\":\"上海示例贸易有限公司\",\"address\":\"地址2\","
                                + "\"shipAddresses\":[{\"address\":\"收货地址2\"}]}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_CUSTOMER_NAME"));
    }

    @Test
    void create_duplicateCode_returns409() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8(CUSTOMER_JSON)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"CUST-001\",\"name\":\"全新公司\",\"address\":\"地址3\","
                                + "\"shipAddresses\":[{\"address\":\"收货地址3\"}]}")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_CUSTOMER_CODE"));
    }

    @Test
    void create_missingShipAddresses_returns400() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                        .content(utf8("{\"code\":\"NOADDR\",\"name\":\"无地址公司\",\"address\":\"某地址\"}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void create_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content(utf8("{}")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/customers/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CUSTOMER_NOT_FOUND"));
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
