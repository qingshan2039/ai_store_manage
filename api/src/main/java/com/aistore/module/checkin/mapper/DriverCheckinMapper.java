package com.aistore.module.checkin.mapper;

import com.aistore.module.checkin.entity.DriverCheckin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/** 司机打卡 Mapper（列表/详情用 BaseMapper + LambdaQueryWrapper，无需自定义 XML） */
@Mapper
public interface DriverCheckinMapper extends BaseMapper<DriverCheckin> {
}
