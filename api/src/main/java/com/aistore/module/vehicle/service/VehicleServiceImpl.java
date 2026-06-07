package com.aistore.module.vehicle.service;

import com.aistore.common.dto.UpdateStatusRequest;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.user.entity.SysUser;
import com.aistore.module.user.mapper.SysUserMapper;
import com.aistore.module.vehicle.converter.VehicleConverter;
import com.aistore.module.vehicle.dto.CreateVehicleRequest;
import com.aistore.module.vehicle.dto.UpdateVehicleRequest;
import com.aistore.module.vehicle.dto.VehicleQueryParam;
import com.aistore.module.vehicle.entity.Vehicle;
import com.aistore.module.vehicle.mapper.VehicleMapper;
import com.aistore.module.vehicle.vo.VehicleListResponse;
import com.aistore.module.vehicle.vo.VehicleSummaryVO;
import com.aistore.module.vehicle.vo.VehicleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 车辆服务实现 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final SysUserMapper userMapper;
    private final VehicleConverter vehicleConverter;

    @Override
    @Transactional
    public VehicleVO createVehicle(CreateVehicleRequest request) {
        if (vehicleMapper.selectCount(new LambdaQueryWrapper<Vehicle>()
                .eq(Vehicle::getPlateNo, request.getPlateNo())) > 0) {
            throw DuplicateResourceException.duplicateVehiclePlate();
        }
        Vehicle entity = vehicleConverter.toEntity(request);
        vehicleMapper.insert(entity);
        return toVO(vehicleMapper.selectById(entity.getId()));
    }

    @Override
    public VehicleVO getVehicleById(Long id) {
        Vehicle entity = vehicleMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.vehicleNotFound();
        }
        return toVO(entity);
    }

    @Override
    public VehicleListResponse listVehicles(VehicleQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;
        Page<Vehicle> page = new Page<>(pageNum, pageSize);
        IPage<Vehicle> result = vehicleMapper.selectVehiclePage(page, param.getKeyword(), param.getStatus());

        List<Vehicle> records = result.getRecords();
        List<Long> userIds = records.stream()
                .flatMap(v -> Stream.of(v.getDefaultDriverUserId(), v.getDefaultEscortUserId()))
                .filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, String> names = userNames(userIds);

        List<VehicleSummaryVO> items = records.stream()
                .map(v -> vehicleConverter.toSummaryVO(v,
                        displayName(v.getDefaultDriverUserId(), v.getDefaultDriverOther(), names),
                        displayName(v.getDefaultEscortUserId(), v.getDefaultEscortOther(), names)))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return VehicleListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public VehicleVO updateVehicle(Long id, UpdateVehicleRequest request) {
        Vehicle entity = vehicleMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.vehicleNotFound();
        }
        if (request.getPlateNo() != null && !request.getPlateNo().equals(entity.getPlateNo())
                && vehicleMapper.selectCount(new LambdaQueryWrapper<Vehicle>()
                .eq(Vehicle::getPlateNo, request.getPlateNo())
                .ne(Vehicle::getId, id)) > 0) {
            throw DuplicateResourceException.duplicateVehiclePlate();
        }
        vehicleConverter.updateEntity(entity, request);
        vehicleMapper.updateById(entity);
        return toVO(vehicleMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        if (vehicleMapper.selectById(id) == null) {
            throw ResourceNotFoundException.vehicleNotFound();
        }
        vehicleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public VehicleVO updateVehicleStatus(Long id, UpdateStatusRequest request) {
        Vehicle entity = vehicleMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.vehicleNotFound();
        }
        entity.setStatus(request.getStatus());
        vehicleMapper.updateById(entity);
        return toVO(vehicleMapper.selectById(id));
    }

    /** 单条详情：即时解析常态司机/跟车员显示名 */
    private VehicleVO toVO(Vehicle e) {
        List<Long> ids = new ArrayList<>();
        if (e.getDefaultDriverUserId() != null) ids.add(e.getDefaultDriverUserId());
        if (e.getDefaultEscortUserId() != null) ids.add(e.getDefaultEscortUserId());
        Map<Long, String> names = userNames(ids);
        return vehicleConverter.toVO(e,
                displayName(e.getDefaultDriverUserId(), e.getDefaultDriverOther(), names),
                displayName(e.getDefaultEscortUserId(), e.getDefaultEscortOther(), names));
    }

    /** 显示名：有 userId 取用户名，否则取 OTHER 替补名 */
    private String displayName(Long userId, String other, Map<Long, String> names) {
        if (userId != null) {
            return names.get(userId);
        }
        return other;
    }

    private Map<Long, String> userNames(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getName));
    }
}
