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
package org.junit.jupiter.api.extension;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.doddle.storage.sql.MySQLStorageProvider;
import dev.doddle.storage.sql.PostgresStorageProvider;
import org.postgresql.jdbc.AutoSave;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

public class InjectionModule extends AbstractModule {

    @Provides
    @Singleton
    MySQLContainer<?> providesMySQLContainer() {
        final MySQLContainer<?> container = new MySQLContainer<>("mysql:8.0.22");
        container.start();
        return container;
    }

    @Provides
    @Singleton
    MySQLStorageProvider providesMySQLStorageAdapter(MySQLContainer<?> container) {
        return new MySQLStorageProvider(createSQLDatasource(container));
    }

    @Provides
    @Singleton
    PostgreSQLContainer<?> providesPostgresContainer() {
        final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:10.4");
        container.start();
        return container;
    }

    @Provides
    @Singleton
    PostgresStorageProvider providesPostgresStorageAdapter(PostgreSQLContainer<?> container) {
        return new PostgresStorageProvider(createSQLDatasource(container));
    }

    private DataSource createSQLDatasource(final JdbcDatabaseContainer<?> container) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        // fixes https://stackoverflow.com/questions/2783813/postgres-error-cached-plan-must-not-change-result-type
        config.addDataSourceProperty("autosave", AutoSave.CONSERVATIVE);
        return new HikariDataSource(config);
    }

}
