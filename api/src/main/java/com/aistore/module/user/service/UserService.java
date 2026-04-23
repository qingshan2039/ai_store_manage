package com.aistore.module.user.service;

import com.aistore.module.user.dto.CreateUserRequest;
import com.aistore.module.user.dto.ResetUserPasswordRequest;
import com.aistore.module.user.dto.UpdateUserRequest;
import com.aistore.module.user.dto.UpdateUserStatusRequest;
import com.aistore.module.user.dto.UserQueryParam;
import com.aistore.module.user.vo.UserListResponse;
import com.aistore.module.user.vo.UserVO;

/**
 * 用户服务接口
 * 定义用户模块的全部业务操作
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param request 创建用户请求
     * @return 创建成功的用户详情
     */
    UserVO createUser(CreateUserRequest request);

    /**
     * 根据 ID 查询用户详情
     *
     * @param id 用户 ID
     * @return 用户详情
     */
    UserVO getUserById(Long id);

    /**
     * 分页查询用户列表
     *
     * @param param 查询参数
     * @return 分页用户列表
     */
    UserListResponse listUsers(UserQueryParam param);

    /**
     * 更新用户信息
     *
     * @param id      用户 ID
     * @param request 更新用户请求
     * @return 更新后的用户详情
     */
    UserVO updateUser(Long id, UpdateUserRequest request);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户 ID
     */
    void deleteUser(Long id);

    /**
     * 变更用户状态
     *
     * @param id      用户 ID
     * @param request 状态变更请求
     * @return 变更后的用户详情
     */
    UserVO updateUserStatus(Long id, UpdateUserStatusRequest request);

    /**
     * 重置用户密码
     *
     * @param id      用户 ID
     * @param request 密码重置请求
     */
    void resetUserPassword(Long id, ResetUserPasswordRequest request);
}
