package com.aistore.module.user.service;

import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.user.converter.UserConverter;
import com.aistore.module.user.dto.CreateUserRequest;
import com.aistore.module.user.dto.ResetUserPasswordRequest;
import com.aistore.module.user.dto.UpdateUserRequest;
import com.aistore.module.user.dto.UpdateUserStatusRequest;
import com.aistore.module.user.dto.UserQueryParam;
import com.aistore.module.user.entity.SysUser;
import com.aistore.module.user.mapper.SysUserMapper;
import com.aistore.module.user.vo.UserListResponse;
import com.aistore.module.user.vo.UserSummaryVO;
import com.aistore.module.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 * 实现用户模块全部业务逻辑
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final SysUserMapper userMapper;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户
     * 1. 唯一性校验（username / employeeNo）
     * 2. BCrypt 加密密码
     * 3. 插入数据库
     * 4. 返回用户详情
     */
    @Override
    @Transactional
    public UserVO createUser(CreateUserRequest request) {
        log.info("创建用户: username={}, employeeNo={}", request.getUsername(), request.getEmployeeNo());

        // 1. 唯一性校验 - 用户名
        LambdaQueryWrapper<SysUser> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(SysUser::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw DuplicateResourceException.duplicateUsername();
        }

        // 2. 唯一性校验 - 工号
        LambdaQueryWrapper<SysUser> employeeNoQuery = new LambdaQueryWrapper<>();
        employeeNoQuery.eq(SysUser::getEmployeeNo, request.getEmployeeNo());
        if (userMapper.selectCount(employeeNoQuery) > 0) {
            throw DuplicateResourceException.duplicateEmployeeNo();
        }

        // 3. 转换实体
        SysUser entity = userConverter.toEntity(request);

        // 4. 密码 BCrypt 加密
        entity.setPassword(passwordEncoder.encode(request.getPassword()));

        // 5. 插入数据库
        userMapper.insert(entity);
        log.info("用户创建成功: id={}, username={}", entity.getId(), entity.getUsername());

        // 6. 查询完整实体并返回（获取数据库生成的字段）
        SysUser savedUser = userMapper.selectById(entity.getId());
        return userConverter.toUserVO(savedUser);
    }

    /**
     * 根据 ID 查询用户详情
     */
    @Override
    public UserVO getUserById(Long id) {
        log.debug("查询用户详情: id={}", id);

        SysUser entity = userMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.userNotFound();
        }

        return userConverter.toUserVO(entity);
    }

    /**
     * 分页查询用户列表
     * 1. 构建分页参数（契约 page 从 1 开始，MyBatis-Plus 也从 1 开始）
     * 2. 执行分页查询
     * 3. 转换为 UserSummaryVO
     * 4. 封装 UserListResponse
     */
    @Override
    public UserListResponse listUsers(UserQueryParam param) {
        log.debug("查询用户列表: page={}, pageSize={}, keyword={}", param.getPage(), param.getPageSize(), param.getKeyword());

        // 1. 构建分页参数
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? param.getPageSize() : 20;
        if (pageSize > 100) {
            pageSize = 100;
        }

        Page<SysUser> page = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询
        IPage<SysUser> result = userMapper.selectUserPage(
                page,
                param.getKeyword(),
                param.getEmployeeNo(),
                param.getName(),
                param.getPhoneNumber(),
                param.getStatus(),
                param.getDepartmentId(),
                param.getDepartmentType(),
                param.getJobTitle(),
                param.getGender(),
                param.getCreatedAtStart(),
                param.getCreatedAtEnd()
        );

        // 3. 转换为 UserSummaryVO
        List<UserSummaryVO> items = result.getRecords().stream()
                .map(userConverter::toUserSummaryVO)
                .toList();

        // 4. 封装 UserListResponse
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);

        return UserListResponse.builder()
                .items(items)
                .total(result.getTotal())
                .page(pageNum)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }

    /**
     * 更新用户信息
     * 1. 查询用户是否存在
     * 2. 使用非 null 字段覆盖更新
     * 3. 返回更新后的用户详情
     */
    @Override
    @Transactional
    public UserVO updateUser(Long id, UpdateUserRequest request) {
        log.info("更新用户信息: id={}", id);

        // 1. 查询用户
        SysUser entity = userMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.userNotFound();
        }

        // 2. 非 null 字段覆盖更新
        userConverter.updateEntity(entity, request);

        // 3. 执行更新
        userMapper.updateById(entity);
        log.info("用户信息更新成功: id={}", id);

        // 4. 返回更新后的完整用户详情
        SysUser updatedUser = userMapper.selectById(id);
        return userConverter.toUserVO(updatedUser);
    }

    /**
     * 删除用户（逻辑删除）
     * MyBatis-Plus @TableLogic 自动处理逻辑删除
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户: id={}", id);

        // 1. 查询用户是否存在
        SysUser entity = userMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.userNotFound();
        }

        // 2. 逻辑删除（MyBatis-Plus 自动将 deleted 设为 1）
        userMapper.deleteById(id);
        log.info("用户删除成功: id={}", id);
    }

    /**
     * 变更用户状态
     * 1. 查询用户是否存在
     * 2. 更新状态
     * 3. 返回更新后的用户详情
     */
    @Override
    @Transactional
    public UserVO updateUserStatus(Long id, UpdateUserStatusRequest request) {
        log.info("变更用户状态: id={}, status={}", id, request.getStatus());

        // 1. 查询用户
        SysUser entity = userMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.userNotFound();
        }

        // 2. 更新状态
        entity.setStatus(request.getStatus());
        userMapper.updateById(entity);
        log.info("用户状态变更成功: id={}, status={}", id, request.getStatus());

        // 3. 返回更新后的用户详情
        SysUser updatedUser = userMapper.selectById(id);
        return userConverter.toUserVO(updatedUser);
    }

    /**
     * 重置用户密码
     * 1. 查询用户是否存在
     * 2. BCrypt 加密新密码
     * 3. 更新密码
     */
    @Override
    @Transactional
    public void resetUserPassword(Long id, ResetUserPasswordRequest request) {
        log.info("重置用户密码: id={}", id);

        // 1. 查询用户
        SysUser entity = userMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.userNotFound();
        }

        // 2. BCrypt 加密新密码
        entity.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // 3. 更新密码
        userMapper.updateById(entity);
        log.info("用户密码重置成功: id={}", id);
    }
}
