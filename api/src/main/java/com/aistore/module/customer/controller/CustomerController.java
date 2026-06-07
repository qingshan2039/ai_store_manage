package com.aistore.module.customer.controller;

import com.aistore.module.customer.dto.CreateCustomerRequest;
import com.aistore.module.customer.dto.CustomerQueryParam;
import com.aistore.module.customer.dto.UpdateCustomerRequest;
import com.aistore.module.customer.dto.UpdateCustomerStatusRequest;
import com.aistore.module.customer.service.CustomerService;
import com.aistore.module.customer.vo.CustomerListResponse;
import com.aistore.module.customer.vo.CustomerVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顾客管理 Controller
 * 严格对齐 OpenAPI 契约定义的 6 个接口端点
 *
 * POST   /api/customers              → createCustomer       → 201
 * GET    /api/customers              → listCustomers         → 200
 * GET    /api/customers/{id}         → getCustomerById        → 200
 * PUT    /api/customers/{id}         → updateCustomer         → 200
 * DELETE /api/customers/{id}         → deleteCustomer         → 204
 * PATCH  /api/customers/{id}/status  → updateCustomerStatus   → 200
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerVO> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        log.info("POST /api/customers - 创建顾客: name={}, code={}", request.getName(), request.getCode());
        CustomerVO vo = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    @GetMapping
    public ResponseEntity<CustomerListResponse> listCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("GET /api/customers - 查询顾客列表: page={}, pageSize={}", page, pageSize);

        CustomerQueryParam param = CustomerQueryParam.builder()
                .keyword(keyword)
                .status(status)
                .page(page != null ? page : 1)
                .pageSize(pageSize != null ? pageSize : 20)
                .build();

        CustomerListResponse response = customerService.listCustomers(param);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerVO> getCustomerById(@PathVariable Long id) {
        log.info("GET /api/customers/{} - 查询顾客详情", id);
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerVO> updateCustomer(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateCustomerRequest request) {
        log.info("PUT /api/customers/{} - 更新顾客信息", id);
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("DELETE /api/customers/{} - 删除顾客", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerVO> updateCustomerStatus(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateCustomerStatusRequest request) {
        log.info("PATCH /api/customers/{}/status - 变更顾客状态: status={}", id, request.getStatus());
        return ResponseEntity.ok(customerService.updateCustomerStatus(id, request));
    }
}
