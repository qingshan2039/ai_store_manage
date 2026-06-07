package com.aistore.module.checkin.service;

import com.aistore.common.exception.BusinessException;
import com.aistore.common.exception.DuplicateResourceException;
import com.aistore.common.exception.ResourceNotFoundException;
import com.aistore.module.checkin.converter.DriverCheckinConverter;
import com.aistore.module.checkin.dto.CreateDriverCheckinRequest;
import com.aistore.module.checkin.dto.DriverCheckinQueryParam;
import com.aistore.module.checkin.dto.UpdateDriverCheckinRequest;
import com.aistore.module.checkin.entity.DriverCheckin;
import com.aistore.module.checkin.mapper.DriverCheckinMapper;
import com.aistore.module.checkin.vo.DriverCheckinListResponse;
import com.aistore.module.checkin.vo.DriverCheckinSummaryVO;
import com.aistore.module.checkin.vo.DriverCheckinVO;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 司机打卡服务实现 */
@Service
@RequiredArgsConstructor
public class DriverCheckinServiceImpl implements DriverCheckinService {

    private final DriverCheckinMapper checkinMapper;
    private final SysUserMapper userMapper;
    private final VehicleMapper vehicleMapper;
    private final DriverCheckinConverter checkinConverter;

    @Override
    @Transactional
    public DriverCheckinVO createDriverCheckin(CreateDriverCheckinRequest request) {
        DriverCheckin entity = checkinConverter.toEntity(request);
        requireDriver(entity);
        checkDuplicate(entity.getDriverUserId(), entity.getCheckinDate(), null);
        checkinMapper.insert(entity);
        return toVO(checkinMapper.selectById(entity.getId()));
    }

    @Override
    public DriverCheckinVO getDriverCheckinById(Long id) {
        DriverCheckin entity = checkinMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.driverCheckinNotFound();
        }
        return toVO(entity);
    }

    @Override
    public DriverCheckinListResponse listDriverCheckins(DriverCheckinQueryParam param) {
        int pageNum = param.getPage() != null && param.getPage() >= 1 ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null && param.getPageSize() >= 1 ? Math.min(param.getPageSize(), 100) : 20;

        LambdaQueryWrapper<DriverCheckin> wrapper = new LambdaQueryWrapper<DriverCheckin>()
                .eq(param.getDriverUserId() != null, DriverCheckin::getDriverUserId, param.getDriverUserId())
                .eq(param.getVehicleId() != null, DriverCheckin::getVehicleId, param.getVehicleId())
                .eq(param.getCheckinStatus() != null, DriverCheckin::getCheckinStatus,
                        param.getCheckinStatus() != null ? param.getCheckinStatus().name() : null)
                .ge(param.getCheckinDateStart() != null, DriverCheckin::getCheckinDate, param.getCheckinDateStart())
                .le(param.getCheckinDateEnd() != null, DriverCheckin::getCheckinDate, param.getCheckinDateEnd())
                .orderByDesc(DriverCheckin::getCheckinDate)
                .orderByDesc(DriverCheckin::getId);
        IPage<DriverCheckin> result = checkinMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<DriverCheckin> records = result.getRecords();
        Map<Long, String> names = userNames(records.stream()
                .flatMap(c -> Stream.of(c.getDriverUserId(), c.getEscortUserId()))
                .filter(Objects::nonNull).distinct().toList());
        Map<Long, String> plates = vehiclePlates(records.stream()
                .map(DriverCheckin::getVehicleId).filter(Objects::nonNull).distinct().toList());

        List<DriverCheckinSummaryVO> items = records.stream()
                .map(c -> checkinConverter.toSummaryVO(c,
                        displayName(c.getDriverUserId(), c.getDriverOther(), names),
                        c.getVehicleId() != null ? plates.get(c.getVehicleId()) : null,
                        displayName(c.getEscortUserId(), c.getEscortOther(), names)))
                .toList();
        int totalPages = (int) Math.ceil((double) result.getTotal() / pageSize);
        return DriverCheckinListResponse.builder()
                .items(items).total(result.getTotal()).page(pageNum).pageSize(pageSize).totalPages(totalPages).build();
    }

    @Override
    @Transactional
    public DriverCheckinVO updateDriverCheckin(Long id, UpdateDriverCheckinRequest request) {
        DriverCheckin entity = checkinMapper.selectById(id);
        if (entity == null) {
            throw ResourceNotFoundException.driverCheckinNotFound();
        }
        checkinConverter.updateEntity(entity, request);
        requireDriver(entity);
        checkDuplicate(entity.getDriverUserId(), entity.getCheckinDate(), id);
        checkinMapper.updateById(entity);
        return toVO(checkinMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteDriverCheckin(Long id) {
        if (checkinMapper.selectById(id) == null) {
            throw ResourceNotFoundException.driverCheckinNotFound();
        }
        checkinMapper.deleteById(id);
    }

    /** 司机必填：在册用户或替补名至少其一 */
    private void requireDriver(DriverCheckin e) {
        if (e.getDriverUserId() == null && !StringUtils.hasText(e.getDriverOther())) {
            throw new BusinessException("DRIVER_REQUIRED", "司机不能为空（请选择在册司机或填写替补名）");
        }
    }

    /** 同司机同日唯一（仅对在册司机校验；OTHER 替补不限制）。excludeId 为更新时排除自身。 */
    private void checkDuplicate(Long driverUserId, java.time.LocalDate checkinDate, Long excludeId) {
        if (driverUserId == null || checkinDate == null) {
            return;
        }
        LambdaQueryWrapper<DriverCheckin> w = new LambdaQueryWrapper<DriverCheckin>()
                .eq(DriverCheckin::getDriverUserId, driverUserId)
                .eq(DriverCheckin::getCheckinDate, checkinDate)
                .ne(excludeId != null, DriverCheckin::getId, excludeId);
        if (checkinMapper.selectCount(w) > 0) {
            throw DuplicateResourceException.duplicateDriverCheckin();
        }
    }

    private DriverCheckinVO toVO(DriverCheckin e) {
        List<Long> userIds = Stream.of(e.getDriverUserId(), e.getEscortUserId()).filter(Objects::nonNull).distinct().toList();
        Map<Long, String> names = userNames(userIds);
        String plate = e.getVehicleId() != null ? vehiclePlates(List.of(e.getVehicleId())).get(e.getVehicleId()) : null;
        return checkinConverter.toVO(e,
                displayName(e.getDriverUserId(), e.getDriverOther(), names),
                plate,
                displayName(e.getEscortUserId(), e.getEscortOther(), names));
    }

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

    private Map<Long, String> vehiclePlates(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        return vehicleMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Vehicle::getId, Vehicle::getPlateNo));
    }
}
