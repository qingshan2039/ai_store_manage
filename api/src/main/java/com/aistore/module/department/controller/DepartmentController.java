package com.aistore.module.department.controller;

import com.aistore.module.department.dto.CreateDepartmentRequest;
import com.aistore.module.department.dto.DepartmentQueryParam;
import com.aistore.module.department.dto.UpdateDepartmentRequest;
import com.aistore.module.department.dto.UpdateDepartmentStatusRequest;
import com.aistore.module.department.enums.DepartmentType;
import com.aistore.module.department.service.DepartmentService;
import com.aistore.module.department.vo.DepartmentListResponse;
import com.aistore.module.department.vo.DepartmentVO;
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
 * 部门管理 Controller
 * 严格对齐 OpenAPI 契约定义的 6 个接口端点
 *
 * POST   /api/departments              → createDepartment       → 201
 * GET    /api/departments              → listDepartments         → 200
 * GET    /api/departments/{id}         → getDepartmentById        → 200
 * PUT    /api/departments/{id}         → updateDepartment         → 200
 * DELETE /api/departments/{id}         → deleteDepartment         → 204
 * PATCH  /api/departments/{id}/status  → updateDepartmentStatus   → 200
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private static final Logger log = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    /**
     * POST /api/departments — 创建部门
     */
    @PostMapping
    public ResponseEntity<DepartmentVO> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        log.info("POST /api/departments - 创建部门: name={}, code={}", request.getName(), request.getCode());
        DepartmentVO vo = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    /**
     * GET /api/departments — 查询部门列表
     */
    @GetMapping
    public ResponseEntity<DepartmentListResponse> listDepartments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) DepartmentType type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("GET /api/departments - 查询部门列表: page={}, pageSize={}", page, pageSize);

        DepartmentQueryParam param = DepartmentQueryParam.builder()
                .keyword(keyword)
                .type(type)
                .status(status)
                .page(page != null ? page : 1)
                .pageSize(pageSize != null ? pageSize : 20)
                .build();

        DepartmentListResponse response = departmentService.listDepartments(param);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/departments/{id} — 查询部门详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentVO> getDepartmentById(@PathVariable Long id) {
        log.info("GET /api/departments/{} - 查询部门详情", id);
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    /**
     * PUT /api/departments/{id} — 更新部门信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentVO> updateDepartment(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateDepartmentRequest request) {
        log.info("PUT /api/departments/{} - 更新部门信息", id);
        return ResponseEntity.ok(departmentService.updateDepartment(id, request));
    }

    /**
     * DELETE /api/departments/{id} — 删除部门
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        log.info("DELETE /api/departments/{} - 删除部门", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/departments/{id}/status — 变更部门状态
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<DepartmentVO> updateDepartmentStatus(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateDepartmentStatusRequest request) {
        log.info("PATCH /api/departments/{}/status - 变更部门状态: status={}", id, request.getStatus());
        return ResponseEntity.ok(departmentService.updateDepartmentStatus(id, request));
    }
}
