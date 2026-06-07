package com.aistore.common.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

/**
 * PostgreSQL jsonb ↔ Map&lt;String,Object&gt; 类型处理器。
 *
 * 写入用 PGobject(type=jsonb) 避免 "character varying = jsonb" 类型不匹配；
 * 读取把 jsonb 文本反序列化为 Map。仅作用于显式标注 @TableField(typeHandler) 的字段，
 * 不改动全局 JDBC 配置。
 */
@MappedTypes(Map.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType)
            throws SQLException {
        String json;
        try {
            json = MAPPER.writeValueAsString(parameter);
        } catch (Exception e) {
            throw new SQLException("序列化 jsonb 失败", e);
        }
        // 以 Types.OTHER 传入 JSON 串，PostgreSQL 驱动会按 jsonb 隐式转换（无需 PGobject 编译期依赖）
        ps.setObject(i, json, Types.OTHER);
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private Map<String, Object> parse(String json) throws SQLException {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, MAP_TYPE);
        } catch (Exception e) {
            throw new SQLException("反序列化 jsonb 失败", e);
        }
    }
}
