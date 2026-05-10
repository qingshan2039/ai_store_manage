package com.aistore.module.user.controller;

import com.aistore.module.user.dto.CreateUserRequest;
import com.aistore.module.user.dto.ResetUserPasswordRequest;
import com.aistore.module.user.dto.UpdateUserRequest;
import com.aistore.module.user.dto.UpdateUserStatusRequest;
import com.aistore.module.user.dto.UserQueryParam;
import com.aistore.module.user.service.UserService;
import com.aistore.module.user.vo.UserListResponse;
import com.aistore.module.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;

/**
 * 用户管理 Controller
 * 严格对齐 OpenAPI 契约定义的 7 个接口端点
 *
 * POST   /api/users              → createUser       → 201
 * GET    /api/users              → listUsers         → 200
 * GET    /api/users/{id}         → getUserById        → 200
 * PUT    /api/users/{id}         → updateUser         → 200
 * DELETE /api/users/{id}         → deleteUser         → 204
 * PATCH  /api/users/{id}/status  → updateUserStatus   → 200
 * PUT    /api/users/{id}/password → resetUserPassword → 204
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * POST /api/users — 创建用户
     * operationId: createUser
     *
     * @param request 创建用户请求体
     * @return 201 + 用户详情
     */
    @PostMapping
    public ResponseEntity<UserVO> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/users - 创建用户: username={}", request.getUsername());
        UserVO userVO = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userVO);
    }

    /**
     * GET /api/users — 查询用户列表
     * operationId: listUsers
     *
     * @param keyword        关键词搜索
     * @param employeeNo     工号筛选
     * @param name           姓名筛选
     * @param phoneNumber    手机号筛选
     * @param status         状态筛选
     * @param departmentId   部门 ID 筛选
     * @param jobTitle       职位筛选
     * @param gender         性别筛选
     * @param createdAtStart 创建时间起始
     * @param createdAtEnd   创建时间截止
     * @param page           页码（从 1 开始）
     * @param pageSize       每页条数
     * @return 200 + 分页用户列表
     */
    @GetMapping
    public ResponseEntity<UserListResponse> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String employeeNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtStart,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtEnd,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        log.info("GET /api/users - 查询用户列表: page={}, pageSize={}", page, pageSize);

        UserQueryParam param = UserQueryParam.builder()
                .keyword(keyword)
                .employeeNo(employeeNo)
                .name(name)
                .phoneNumber(phoneNumber)
                .status(status)
                .departmentId(departmentId)
                .jobTitle(jobTitle)
                .gender(gender)
                .createdAtStart(createdAtStart)
                .createdAtEnd(createdAtEnd)
                .page(page != null ? page : 1)
                .pageSize(pageSize != null ? pageSize : 20)
                .build();

        UserListResponse response = userService.listUsers(param);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users/{id} — 查询用户详情
     * operationId: getUserById
     *
     * @param id 用户 ID
     * @return 200 + 用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserVO> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} - 查询用户详情", id);
        UserVO userVO = userService.getUserById(id);
        return ResponseEntity.ok(userVO);
    }

    /**
     * PUT /api/users/{id} — 更新用户信息
     * operationId: updateUser
     *
     * @param id      用户 ID
     * @param request 更新用户请求体
     * @return 200 + 更新后的用户详情
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserVO> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT /api/users/{} - 更新用户信息", id);
        UserVO userVO = userService.updateUser(id, request);
        return ResponseEntity.ok(userVO);
    }

    /**
     * DELETE /api/users/{id} — 删除用户
     * operationId: deleteUser
     *
     * @param id 用户 ID
     * @return 204 无返回内容
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - 删除用户", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/users/{id}/status — 变更用户状态
     * operationId: updateUserStatus
     *
     * @param id      用户 ID
     * @param request 状态变更请求体
     * @return 200 + 变更后的用户详情
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserVO> updateUserStatus(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateUserStatusRequest request) {
        log.info("PATCH /api/users/{}/status - 变更用户状态: status={}", id, request.getStatus());
        UserVO userVO = userService.updateUserStatus(id, request);
        return ResponseEntity.ok(userVO);
    }

    /**
     * PUT /api/users/{id}/password — 重置用户密码
     * operationId: resetUserPassword
     *
     * @param id      用户 ID
     * @param request 密码重置请求体
     * @return 204 无返回内容
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> resetUserPassword(@PathVariable Long id,
                                                   @Valid @RequestBody ResetUserPasswordRequest request) {
        log.info("PUT /api/users/{}/password - 重置用户密码", id);
        userService.resetUserPassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
