package com.aistore.module.vehicle.mapper;

import com.aistore.module.vehicle.entity.Vehicle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 车辆 Mapper */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {

    IPage<Vehicle> selectVehiclePage(
            Page<Vehicle> page,
            @Param("keyword") String keyword,
            @Param("status") Integer status
    );
}
