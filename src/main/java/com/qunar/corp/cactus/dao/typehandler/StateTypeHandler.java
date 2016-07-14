/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.dao.typehandler;

import com.qunar.corp.cactus.bean.Status;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhenyu.nie created on 2013 13-12-24 下午12:21
 */
public class StateTypeHandler implements TypeHandler<Status> {
    @Override
    public void setParameter(PreparedStatement ps, int i, Status parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public Status getResult(ResultSet rs, String columnName) throws SQLException {
        return Status.fromCode(rs.getInt(columnName));
    }

    @Override
    public Status getResult(ResultSet rs, int columnIndex) throws SQLException {
        return Status.fromCode(rs.getInt(columnIndex));
    }

    @Override
    public Status getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Status.fromCode(cs.getInt(columnIndex));
    }
}
