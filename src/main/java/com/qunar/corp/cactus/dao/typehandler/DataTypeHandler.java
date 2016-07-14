/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.qunar.corp.cactus.bean.DataType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午12:30
 */
public class DataTypeHandler implements TypeHandler<DataType> {
    @Override
    public void setParameter(PreparedStatement ps, int i, DataType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public DataType getResult(ResultSet rs, String columnName) throws SQLException {
        return DataType.fromCode(rs.getInt(columnName));
    }

    @Override
    public DataType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return DataType.fromCode(rs.getInt(columnIndex));
    }

    @Override
    public DataType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return DataType.fromCode(cs.getInt(columnIndex));
    }
}
