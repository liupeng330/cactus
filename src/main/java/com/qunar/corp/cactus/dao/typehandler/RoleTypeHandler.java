package com.qunar.corp.cactus.dao.typehandler;

import com.qunar.corp.cactus.bean.Role;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleTypeHandler implements TypeHandler<Role> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Role parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.code());
    }

    @Override
    public Role getResult(ResultSet rs, String columnName) throws SQLException {
        return Role.codeOf(rs.getInt(columnName));
    }

    @Override
    public Role getResult(ResultSet rs, int columnIndex) throws SQLException {
        return Role.codeOf(rs.getInt(columnIndex));
    }

    @Override
    public Role getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Role.codeOf(cs.getInt(columnIndex));
    }
}
