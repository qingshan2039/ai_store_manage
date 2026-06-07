package com.aistore.module.fuel.service;

import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.fuel.converter.FuelRecordConverter;
import com.aistore.module.fuel.dto.CreateFuelRecordRequest;
import com.aistore.module.fuel.dto.FuelRecordQueryParam;
import com.aistore.module.fuel.dto.UpdateFuelRecordRequest;
import com.aistore.module.fuel.entity.FuelRecord;
import com.aistore.module.fuel.mapper.FuelRecordMapper;
import com.aistore.module.fuel.vo.FuelRecordListResponse;
import com.aistore.module.fuel.vo.FuelRecordSummaryVO;
import com.aistore.module.fuel.vo.FuelRecordVO;
import com.aistore.module.user.entity.SysUser;
import com.aistore.module.user.mapper.SysUserMapper;
import com.aistore.module.vehicle.entity.Vehicle;
import com.aistore.module.vehicle.mapper.VehicleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** 打油记录服务实现 */
@Service
@RequiredArgsConstructor
public class FuelRecordServiceImpl implements FuelRecordService {

    private final FuelRecordMapper fuelRecordMapper;
    private final VehicleMapper vehicleMapper;
    private final SysUserMapper userMapper;
    private final FuelRecordConverter fuelRecordConverter;

    @Override
    @Transactional
    public FuelRecordVO createFuelRecord(CreateFuelRecordRequest request) {
        FuelRecord entity = fuelRecordConverter.toEntity(request);
        fuelRecordMapper.insert(entity);
        return toVO(fuelRecordMapper.selectById(entity.getId()));
    }

    @Override
    public FuelRecordVO getFuelRecordById(Long id) {
        FuelRecord entity = fuelRecordMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.fuelRecordNotFound();
        }
        return toVO(entity);
    }

    @Override
    public FuelRecordListResponse listFuelRecords(FuelRecordQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;

        LambdaQueryWrapper<FuelRecord> wrapper = new LambdaQueryWrapper<FuelRecord>()
                .eq(param.getVehicleId() != null, FuelRecord::getVehicleId, param.getVehicleId())
                .ge(param.getFuelDateStart() != null, FuelRecord::getFuelDate, param.getFuelDateStart())
                .le(param.getFuelDateEnd() != null, FuelRecord::getFuelDate, param.getFuelDateEnd())
                .orderByDesc(FuelRecord::getFuelDate)
                .orderByDesc(FuelRecord::getId);
        IPage<FuelRecord> result = fuelRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<FuelRecord> records = result.getRecords();
        Map<Long, String> plates = vehiclePlates(records.stream().map(FuelRecord::getVehicleId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> drivers = userNames(records.stream().map(FuelRecord::getDriverUserId).filter(Objects::nonNull).distinct().toList());

        List<FuelRecordSummaryVO> items = records.stream()
                .map(f -> fuelRecordConverter.toSummaryVO(f, plates.get(f.getVehicleId()),
                        f.getDriverUserId() != null ? drivers.get(f.getDriverUserId()) : null))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return FuelRecordListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public FuelRecordVO updateFuelRecord(Long id, UpdateFuelRecordRequest request) {
        FuelRecord entity = fuelRecordMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.fuelRecordNotFound();
        }
        fuelRecordConverter.updateEntity(entity, request);
        fuelRecordMapper.updateById(entity);
        return toVO(fuelRecordMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteFuelRecord(Long id) {
        if (fuelRecordMapper.selectById(id) == null) {
            throw ResourceNotFoundException.fuelRecordNotFound();
        }
        fuelRecordMapper.deleteById(id);
    }

    private FuelRecordVO toVO(FuelRecord e) {
        String plate = e.getVehicleId() != null ? vehiclePlates(List.of(e.getVehicleId())).get(e.getVehicleId()) : null;
        String driver = e.getDriverUserId() != null ? userNames(List.of(e.getDriverUserId())).get(e.getDriverUserId()) : null;
        return fuelRecordConverter.toVO(e, plate, driver);
    }

    private Map<Long, String> vehiclePlates(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        return vehicleMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Vehicle::getId, Vehicle::getPlateNo));
    }

    private Map<Long, String> userNames(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getName));
    }
}
