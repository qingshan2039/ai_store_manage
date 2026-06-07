package com.aistore.module.department.service;

import com.aistore.module.department.dto.CreateDepartmentRequest;
import com.aistore.module.department.dto.DepartmentQueryParam;
import com.aistore.module.department.dto.UpdateDepartmentRequest;
import com.aistore.module.department.dto.UpdateDepartmentStatusRequest;
import com.aistore.module.department.vo.DepartmentListResponse;
import com.aistore.module.department.vo.DepartmentVO;

/**
 * 部门服务接口
 * 定义部门模块的全部业务操作
 */
public interface DepartmentService {

    /**
     * 创建部门
     *
     * @param request 创建部门请求
     * @return 创建成功的部门详情
     */
    DepartmentVO createDepartment(CreateDepartmentRequest request);

    /**
     * 根据 ID 查询部门详情
     *
     * @param id 部门 ID
     * @return 部门详情
     */
    DepartmentVO getDepartmentById(Long id);

    /**
     * 分页查询部门列表
     *
     * @param param 查询参数
     * @return 分页部门列表
     */
    DepartmentListResponse listDepartments(DepartmentQueryParam param);

    /**
     * 更新部门信息
     *
     * @param id      部门 ID
     * @param request 更新部门请求
     * @return 更新后的部门详情
     */
    DepartmentVO updateDepartment(Long id, UpdateDepartmentRequest request);

    /**
     * 删除部门（逻辑删除）
     *
     * @param id 部门 ID
     */
    void deleteDepartment(Long id);

    /**
     * 变更部门状态
     *
     * @param id      部门 ID
     * @param request 状态变更请求
     * @return 变更后的部门详情
     */
    DepartmentVO updateDepartmentStatus(Long id, UpdateDepartmentStatusRequest request);
}
