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
package dev.doddle.storage.sql;

import dev.doddle.common.support.NotNull;
import dev.doddle.storage.common.StorageException;
import dev.doddle.storage.common.StorageProvider;
import dev.doddle.storage.common.domain.*;
import dev.doddle.storage.sql.dao.CronJobDao;
import dev.doddle.storage.sql.dao.JobDao;
import dev.doddle.storage.sql.dao.JobMessageDao;
import dev.doddle.storage.sql.dao.QueueDao;
import dev.doddle.storage.sql.handlers.JobCategoryHandler;
import dev.doddle.storage.sql.handlers.JobStateHandler;
import dev.doddle.storage.sql.handlers.ListArrayTypeHandler;
import dev.doddle.storage.sql.mappers.CronJobMapper;
import dev.doddle.storage.sql.mappers.JobMapper;
import dev.doddle.storage.sql.mappers.JobMessageMapper;
import dev.doddle.storage.sql.mappers.QueueMapper;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public abstract class AbstractSQLStorageProvider implements StorageProvider {

    protected final DataSource    dataSource;
    protected final String        name;
    protected       JobDao        jobDao;
    protected       JobMessageDao jobMessageDao;
    protected       CronJobDao    cronJobDao;
    protected       QueueDao      queueDao;

    /**
     * Create a new sql adapter
     *
     * @param databaseId the database id to use for this adapter
     * @param dataSource the datasource to use for this adapter
     */
    public AbstractSQLStorageProvider(@NotNull final String databaseId, @NotNull final DataSource dataSource) {
        requireNonNull(databaseId, "databaseId cannot be null");
        this.dataSource = requireNonNull(dataSource, "dataSource cannot be null");
        this.name = databaseId;

        bind(databaseId);
    }

    @Override
    public Integer countAllCronJobs() throws StorageException {
        return this.cronJobDao.countAll();
    }

    @Override
    public Integer countAllMessagesForJob(@NotNull Job job) throws StorageException {
        return this.jobMessageDao.countAll(job);
    }

    @Override
    public Integer countAllQueues() throws StorageException {
        return this.queueDao.countAll();
    }

    @Override
    public Long countJobs(JobFilter filter) throws StorageException {
        return this.jobDao.countAll(filter);
    }

    @Override
    public Long countJobs() throws StorageException {
        return countJobs(new JobFilter());
    }

    @Override
    public void create(Reader reader) {
        load(reader);
    }

    public CronJob createCronJob(@NotNull final CronJob job) throws StorageException {
        return this.cronJobDao.create(job);
    }

    public Job createJob(Job job) throws StorageException {
        return this.jobDao.create(job);
    }

    @Override
    public JobMessage createMessageForJob(@NotNull Job job,
                                          @NotNull JobMessage message) throws StorageException {
        return this.jobMessageDao.create(job, message);
    }

    public Queue createQueue(@NotNull final Queue queue) throws StorageException {
        return this.queueDao.create(queue);
    }

    @Override
    public void deleteAllCronJobs() throws StorageException {
        this.cronJobDao.deleteAll();
    }

    @Override
    public void deleteAllJobs() throws StorageException {
        this.jobDao.deleteAll();
    }

    @Override
    public void deleteAllJobs(@NotNull LocalDateTime date) throws StorageException {
        this.jobDao.deleteAll(date);
    }

    @Override
    public void deleteAllMessagesForJob(@NotNull Job job) throws StorageException {
        this.jobMessageDao.deleteAll(job);
    }

    @Override
    public void deleteAllQueues() throws StorageException {
        this.queueDao.deleteAll();
    }

    @Override
    public void deleteCronJob(@NotNull final CronJob job) throws StorageException {
        this.cronJobDao.delete(job);
    }

    @Override
    public void deleteJob(Job job) throws StorageException {
        this.jobDao.delete(job);
    }

    @Override
    public void deleteQueue(@NotNull Queue queue) throws StorageException {
        this.queueDao.delete(queue);
    }

    @Override
    public List<Job> enqueueJobs() throws StorageException {
        return this.jobDao.enqueue();
    }

    @Override
    public List<CronJob> getAllCronJobs() throws StorageException {
        return this.cronJobDao.getAll();
    }

    @Override
    public List<CronJob> getAllCronJobs(@NotNull Pageable pageable) throws StorageException {
        return this.cronJobDao.getAll(pageable);
    }

    @Override
    public List<JobMessage> getAllMessagesForJob(@NotNull Job job) throws StorageException {
        return this.jobMessageDao.getAll(job);
    }

    @Override
    public List<JobMessage> getAllMessagesForJob(@NotNull Job job,
                                                 @NotNull JobMessageFilter filter,
                                                 @NotNull Pageable pageable) throws StorageException {
        return this.jobMessageDao.getAll(job, filter, pageable);
    }

    @Override
    public List<Queue> getAllQueues() {
        return this.queueDao.getAll();
    }

    @Override
    public List<String> getAllTasks() throws StorageException {
        return this.jobDao.getAllTasks();
    }

    @Override
    public Optional<CronJob> getCronJobById(@NotNull final String id) throws StorageException {
        return this.cronJobDao.getById(id);
    }

    @Override
    public Optional<CronJob> getCronJobByName(@NotNull final String name) throws StorageException {
        return this.cronJobDao.getByName(name);
    }

    @Override
    public Optional<Job> getJobById(@NotNull final String id) throws StorageException {
        return this.jobDao.getById(id);
    }

    @Override
    public Optional<Job> getJobByIdentifier(@NotNull final String identifier) throws StorageException {
        return this.jobDao.getByIdentifier(identifier);
    }

    @Override
    public JobStatistic getJobStatistics() throws StorageException {
        return this.jobDao.getStatistics();
    }

    @Override
    public List<Job> getJobs(@NotNull Pageable pageable) throws StorageException {
        return this.jobDao.getAll(new JobFilter(), pageable);
    }

    @Override
    public List<Job> getJobs(@NotNull JobFilter filter, @NotNull Pageable pageable) throws StorageException {
        return this.jobDao.getAll(filter, pageable);
    }

    @Override
    public List<Job> getJobs() throws StorageException {
        return this.jobDao.getAll(null);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Optional<Queue> getQueueById(@NotNull String id) {
        return this.queueDao.getById(id);
    }

    @Override
    public Optional<Queue> getQueueByName(@NotNull String name) {
        return this.queueDao.getByName(name);
    }

    @Override
    public void load(Reader reader) {
        try (final Connection connection = dataSource.getConnection()) {
            final PrintWriter out = new PrintWriter(System.out, true);
            final ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(true);
            runner.setLogWriter(out);
            runner.setErrorLogWriter(out);
            runner.runScript(reader);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Job> pickJob() throws StorageException {
        return this.jobDao.pick();
    }

    @Override
    public void reset(Reader reader) {
        load(reader);
    }

    @Override
    public CronJob saveCronJob(@NotNull final CronJob job) throws StorageException {
        if (job.getId() == null) {
            return this.createCronJob(job);
        }
        return this.updateCronJob(job);
    }

    @Override
    public Job saveJob(Job job) throws StorageException {
        if (job.getId() == null) {
            return this.createJob(job);
        }
        return this.updateJob(job);
    }

    @Override
    public Queue saveQueue(@NotNull final Queue queue) {
        if (queue.getId() == null) {
            return this.createQueue(queue);
        }
        return this.updateQueue(queue);
    }

    public CronJob updateCronJob(@NotNull final CronJob job) throws StorageException {
        return this.cronJobDao.update(job);
    }

    public Job updateJob(Job job) throws StorageException {
        return this.jobDao.update(job);
    }

    public Queue updateQueue(@NotNull final Queue queue) throws StorageException {
        return this.queueDao.update(queue);
    }

    protected void bind(@NotNull final String databaseId) {
        final SqlSessionFactory sqlSessionFactory = createSqlSessionFactory(databaseId, dataSource);
        this.jobDao = new JobDao(sqlSessionFactory);
        this.jobMessageDao = new JobMessageDao(sqlSessionFactory);
        this.cronJobDao = new CronJobDao(sqlSessionFactory);
        this.queueDao = new QueueDao(sqlSessionFactory);
    }

    protected Configuration buildConfiguration(@NotNull final String databaseId, @NotNull final Environment environment) {
        final Configuration configuration = new Configuration(environment);
        configuration.addMapper(JobMapper.class);
        configuration.addMapper(JobMessageMapper.class);
        configuration.addMapper(CronJobMapper.class);
        configuration.addMapper(QueueMapper.class);
        configuration.setDatabaseId(databaseId);
        configuration.getTypeHandlerRegistry().register(JobStateHandler.class);
        configuration.getTypeHandlerRegistry().register(JobCategoryHandler.class);
        configuration.getTypeHandlerRegistry().register(ListArrayTypeHandler.class);
        return configuration;
    }

    protected Environment buildEnvironment(@NotNull final DataSource dataSource) {
        final TransactionFactory transactionFactory = new JdbcTransactionFactory();
        return new Environment("doddle", transactionFactory, dataSource);
    }

    protected SqlSessionFactory createSqlSessionFactory(@NotNull final String databaseId, @NotNull final DataSource dataSource) {
        final Environment environment = buildEnvironment(dataSource);
        final Configuration configuration = buildConfiguration(databaseId, environment);
        final SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        return builder.build(configuration);
    }
}

