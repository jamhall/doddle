/*
 * MIT License
 *
 * Copyright (c) 2022 Jamie Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.doddle.storage.sql.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@MappedJdbcTypes({JdbcType.ARRAY})
@MappedTypes({Object.class})
public class ListArrayTypeHandler extends BaseTypeHandler<List<?>> {

    @Override
    public List<?> getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return extractArray(resultSet.getArray(columnName));
    }

    @Override
    public List<?> getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return extractArray(resultSet.getArray(columnIndex));
    }

    @Override
    public List<?> getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return extractArray(callableStatement.getArray(columnIndex));
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i,
                                    List<?> parameter, JdbcType jdbcType) throws SQLException {
        final Array array = preparedStatement.getConnection().createArrayOf("TEXT", parameter.toArray());
        try {
            preparedStatement.setArray(i, array);
        } finally {
            array.free();
        }
    }

    protected List<?> extractArray(Array array) throws SQLException {
        if (array == null) {
            return null;
        }
        Object javaArray = array.getArray();
        array.free();
        return new ArrayList<>(asList((Object[]) javaArray));
    }
}
