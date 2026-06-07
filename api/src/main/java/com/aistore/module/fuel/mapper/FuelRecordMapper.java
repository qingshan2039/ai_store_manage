package com.aistore.module.fuel.mapper;

import com.aistore.module.fuel.entity.FuelRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打油记录 Mapper。
 * 列表/详情使用 BaseMapper 的 selectPage/selectById（实体 autoResultMap 套用 images 的 jsonb 类型处理器），
 * 故无需自定义 XML。
 */
@Mapper
public interface FuelRecordMapper extends BaseMapper<FuelRecord> {
}
