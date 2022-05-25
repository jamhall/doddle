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

import dev.doddle.storage.common.domain.JobCategory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JobCategoryHandler extends BaseTypeHandler<JobCategory> {

    @Override
    public JobCategory getNullableResult(final ResultSet resultSet, final String columnName) throws SQLException {
        return JobCategory.fromName(resultSet.getString(columnName));
    }

    @Override
    public JobCategory getNullableResult(final ResultSet resultSet, final int columnIndex) throws SQLException {
        return JobCategory.fromName(resultSet.getString(columnIndex));
    }

    @Override
    public JobCategory getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        return JobCategory.fromName(cs.getString(columnIndex));
    }

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int index, final JobCategory parameter, final JdbcType jdbcType) throws SQLException {
        ps.setString(index, parameter.getName());
    }
}
