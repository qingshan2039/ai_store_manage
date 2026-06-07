package com.aistore.module.department.service;

import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceInUseException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.department.converter.DepartmentConverter;
import com.aistore.module.department.dto.CreateDepartmentRequest;
import com.aistore.module.department.dto.DepartmentQueryParam;
import com.aistore.module.department.dto.UpdateDepartmentRequest;
import com.aistore.module.department.dto.UpdateDepartmentStatusRequest;
import com.aistore.module.department.entity.SysDepartment;
import com.aistore.module.department.mapper.SysDepartmentMapper;
import com.aistore.module.department.vo.DepartmentListResponse;
import com.aistore.module.department.vo.DepartmentSummaryVO;
import com.aistore.module.department.vo.DepartmentVO;
import com.aistore.module.user.entity.SysUser;
import com.aistore.module.user.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门服务实现类
 * 实现部门模块全部业务逻辑
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final SysDepartmentMapper departmentMapper;
    private final DepartmentConverter departmentConverter;
    private final SysUserMapper userMapper;

    /**
     * 创建部门
     * 1. 唯一性校验（name / code）
     * 2. 插入数据库
     * 3. 返回部门详情
     */
    @Override
    @Transactional
    public DepartmentVO createDepartment(CreateDepartmentRequest request) {
        log.info("创建部门: name={}, code={}", request.getName(), request.getCode());

        // 1. 唯一性校验 - 名称
        LambdaQueryWrapper<SysDepartment> nameQuery = new LambdaQueryWrapper<>();
        nameQuery.eq(SysDepartment::getName, request.getName());
        if (departmentMapper.selectCount(nameQuery) > 0) {
            throw DuplicateResourceException.duplicateDepartmentName();
        }

        // 2. 唯一性校验 - 编码
        LambdaQueryWrapper<SysDepartment> codeQuery = new LambdaQueryWrapper<>();
        codeQuery.eq(SysDepartment::getCode, request.getCode());
        if (departmentMapper.selectCount(codeQuery) > 0) {
            throw DuplicateResourceException.duplicateDepartmentCode();
        }

        // 3. 转换并插入
        SysDepartment entity = departmentConverter.toEntity(request);
        departmentMapper.insert(entity);
        log.info("部门创建成功: id={}, name={}", entity.getId(), entity.getName());

        // 4. 查询完整实体返回（获取数据库生成的字段）
        SysDepartment saved = departmentMapper.selectById(entity.getId());
        return departmentConverter.toDepartmentVO(saved);
    }

    /**
     * 根据 ID 查询部门详情
     */
    @Override
    public DepartmentVO getDepartmentById(Long id) {
        log.debug("查询部门详情: id={}", id);

        SysDepartment entity = departmentMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.departmentNotFound();
        }

        return departmentConverter.toDepartmentVO(entity);
    }

    /**
     * 分页查询部门列表
     */
    @Override
    public DepartmentListResponse listDepartments(DepartmentQueryParam param) {
        log.debug("查询部门列表: page={}, pageSize={}, keyword={}", param.getPage(), param.getPageSize(), param.getKeyword());

        // 1. 构建分页参数
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? param.getPageSize() : 20;
        if (pageSize > 100) {
            pageSize = 100;
        }

        Page<SysDepartment> page = new Page<>(pageNum, pageSize);
        String type = param.getType() != null ? param.getType().name() : null;

        // 2. 执行分页查询
        IPage<SysDepartment> result = departmentMapper.selectDepartmentPage(
                page, param.getKeyword(), type, param.getStatus());

        // 3. 转换为 DepartmentSummaryVO
        List<DepartmentSummaryVO> items = result.getRecords().stream()
                .map(departmentConverter::toDepartmentSummaryVO)
                .toList();

        // 4. 封装 DepartmentListResponse
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);

        return DepartmentListResponse.builder()
                .items(items)
                .total(result.getTotal())
                .page(pageNum)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }

    /**
     * 更新部门信息
     * 1. 查询部门是否存在
     * 2. 名称变更时校验唯一性（排除自身）
     * 3. 使用非 null 字段覆盖更新
     */
    @Override
    @Transactional
    public DepartmentVO updateDepartment(Long id, UpdateDepartmentRequest request) {
        log.info("更新部门信息: id={}", id);

        // 1. 查询部门
        SysDepartment entity = departmentMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.departmentNotFound();
        }

        // 2. 名称变更时校验唯一性（排除自身）
        if (request.getName() != null && !request.getName().equals(entity.getName())) {
            LambdaQueryWrapper<SysDepartment> nameQuery = new LambdaQueryWrapper<>();
            nameQuery.eq(SysDepartment::getName, request.getName())
                    .ne(SysDepartment::getId, id);
            if (departmentMapper.selectCount(nameQuery) > 0) {
                throw DuplicateResourceException.duplicateDepartmentName();
            }
        }

        // 3. 非 null 字段覆盖更新
        departmentConverter.updateEntity(entity, request);
        departmentMapper.updateById(entity);
        log.info("部门信息更新成功: id={}", id);

        // 4. 返回更新后的完整部门详情
        SysDepartment updated = departmentMapper.selectById(id);
        return departmentConverter.toDepartmentVO(updated);
    }

    /**
     * 删除部门（逻辑删除）
     * 删除前校验部门下是否仍有用户
     */
    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("删除部门: id={}", id);

        // 1. 查询部门是否存在
        SysDepartment entity = departmentMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.departmentNotFound();
        }

        // 2. 校验部门下是否仍有用户（逻辑未删除的用户）
        LambdaQueryWrapper<SysUser> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(SysUser::getDepartmentId, id);
        if (userMapper.selectCount(userQuery) > 0) {
            throw ResourceInUseException.departmentInUse();
        }

        // 3. 逻辑删除（MyBatis-Plus 自动将 deleted 设为 1）
        departmentMapper.deleteById(id);
        log.info("部门删除成功: id={}", id);
    }

    /**
     * 变更部门状态
     */
    @Override
    @Transactional
    public DepartmentVO updateDepartmentStatus(Long id, UpdateDepartmentStatusRequest request) {
        log.info("变更部门状态: id={}, status={}", id, request.getStatus());

        // 1. 查询部门
        SysDepartment entity = departmentMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.departmentNotFound();
        }

        // 2. 更新状态
        entity.setStatus(request.getStatus());
        departmentMapper.updateById(entity);
        log.info("部门状态变更成功: id={}, status={}", id, request.getStatus());

        // 3. 返回更新后的部门详情
        SysDepartment updated = departmentMapper.selectById(id);
        return departmentConverter.toDepartmentVO(updated);
    }
}
