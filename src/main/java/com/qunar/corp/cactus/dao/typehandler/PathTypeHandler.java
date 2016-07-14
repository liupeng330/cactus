/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.qunar.corp.cactus.bean.PathType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午12:15
 */
public class PathTypeHandler implements TypeHandler<PathType> {
    @Override
    public void setParameter(PreparedStatement ps, int i, PathType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public PathType getResult(ResultSet rs, String columnName) throws SQLException {
        return PathType.fromCode(rs.getInt(columnName));
    }

    @Override
    public PathType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return PathType.fromCode(rs.getInt(columnIndex));
    }

    @Override
    public PathType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return PathType.fromCode(cs.getInt(columnIndex));
    }
}
