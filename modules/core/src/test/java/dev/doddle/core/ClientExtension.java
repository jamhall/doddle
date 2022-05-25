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
package dev.doddle.core;


import org.junit.jupiter.api.extension.*;

public class ClientExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    @Override
    public void afterEach(ExtensionContext context) throws Exception {

    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }
//
//    private final DataSource dataSource;
//    private final Storage    storage;
//
//    public ClientExtension() {
//        this.dataSource = createDataSource();
//        this.storage = new Storage(new PostgresStorageAdapter(dataSource));
//    }
//
//    public Client createClient() {
//        final ClientFactory factory = new ClientFactory();
//        return factory.concurrency(5)
//            .jobListener(new NullJobListener())
//            .loopStrategy(new DefaultLoopStrategy())
//            .maxRetries(1)
//            .taskPayloadMapperAdapter(new JacksonTaskDataMapperAdapter(new ObjectMapper()))
//            .threadNamingStrategy(new DefaultThreadNamingStrategy())
//            .storage(storage)
//            .createClient();
//    }
//
//    private DataSource createDataSource() {
//        final HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(getenv("POSTGRES_STORAGE_DATABASE_URL"));
//        config.setUsername(getenv("POSTGRES_STORAGE_DATABASE_USERNAME"));
//        config.setPassword(getenv("POSTGRES_STORAGE_DATABASE_PASSWORD"));
//        return new HikariDataSource(config);
//    }
//
//    private void runSqlScript(String resource) {
//        try (final Connection connection = dataSource.getConnection()) {
//            final Reader reader = getResourceAsReader(resource);
//            final PrintWriter out = new PrintWriter(System.out, true);
//            final ScriptRunner runner = new ScriptRunner(connection);
//            runner.setAutoCommit(true);
//            runner.setLogWriter(out);
//            runner.setErrorLogWriter(out);
//            runner.runScript(reader);
//        } catch (SQLException | IOException exception) {
//            throw new RuntimeException(exception);
//        }
//    }
//
//    @Override
//    public void afterEach(ExtensionContext context) {
//        runSqlScript("reset.sql");
//    }
//
//    @Override
//    public void beforeEach(ExtensionContext context) {
//        runSqlScript("schema.sql");
//        runSqlScript("fixtures.sql");
//    }
//
//    @Override
//    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
//        final Class<?> type = parameterContext.getParameter().getType();
//        return asList(Client.class, Storage.class).contains(type);
//    }
//
//    @Override
//    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
//        final Class<?> type = parameterContext.getParameter().getType();
//        if (type == Client.class) {
//            return createClient();
//        } else if (type == Storage.class) {
//            return storage;
//        }
//        return null;
//    }


}
