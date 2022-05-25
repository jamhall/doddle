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
package dev.doddle.storage.sql.dao;

import dev.doddle.common.support.NotNull;
import dev.doddle.storage.common.StorageException;
import dev.doddle.storage.common.domain.Job;
import dev.doddle.storage.common.domain.JobMessage;
import dev.doddle.storage.common.domain.JobMessageFilter;
import dev.doddle.storage.common.domain.Pageable;
import dev.doddle.storage.common.interceptors.JobMessageInterceptor;
import dev.doddle.storage.common.support.KeyGenerator;
import dev.doddle.storage.common.support.UUIDKeyGenerator;
import dev.doddle.storage.sql.mappers.JobMessageMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class JobMessageDao {

    private final static Logger                logger = LoggerFactory.getLogger(JobMessageDao.class);
    private final        SqlSessionFactory     sqlSessionFactory;
    private final        JobMessageInterceptor interceptor;

    /**
     * Create a new instance
     *
     * @param sqlSessionFactory the persistence session factory
     */
    public JobMessageDao(@NotNull final SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, new UUIDKeyGenerator());
    }

    /**
     * Create a new instance
     *
     * @param sqlSessionFactory the persistence session factory
     * @param keyGenerator      the generator for creating unique identifiers
     */
    public JobMessageDao(@NotNull final SqlSessionFactory sqlSessionFactory, @NotNull final KeyGenerator keyGenerator) {
        this.sqlSessionFactory = requireNonNull(sqlSessionFactory, "sqlSessionFactory cannot be null");
        this.interceptor = createInterceptor(keyGenerator);
    }

    /**
     * Count all messages for a given job
     *
     * @param job the job
     * @return the number of messages
     */
    public Integer countAll(@NotNull final Job job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Count all messages for the job: {}", job.getId());
            final JobMessageMapper mapper = session.getMapper(JobMessageMapper.class);
            return mapper.countAll(job);
        } catch (PersistenceException exception) {
            throw new StorageException(
                format("Error counting messages for job: %s", job.getId()), exception
            );
        }
    }

    /**
     * Create a new job message
     *
     * @param job     the job
     * @param message the message
     * @return the job message
     */
    public JobMessage create(@NotNull final Job job, final @NotNull JobMessage message) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final JobMessage record = interceptor.apply(message);
            logger.debug("Creating job message with id: {}", record.getId());
            final JobMessageMapper mapper = session.getMapper(JobMessageMapper.class);
            mapper.create(job, record);
            session.commit();
            return record;
        } catch (PersistenceException exception) {
            throw new StorageException(
                format("Error creating job message for job: %s", job.getId()), exception
            );
        }
    }

    public void deleteAll(@NotNull final Job job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Deleting all messages for the job: {}", job.getId());
            final JobMessageMapper mapper = session.getMapper(JobMessageMapper.class);
            mapper.deleteAll(job);
            session.commit();
        } catch (PersistenceException exception) {
            throw new StorageException(
                format("Error deleting messages for job: %s", job.getId()), exception
            );
        }
    }

    /**
     * Get all messages for a given job
     *
     * @param job      the job
     * @param filter   the filter
     * @param pageable the pageable (limit, offset)
     * @return a list of messages
     */
    public List<JobMessage> getAll(@NotNull Job job, @NotNull JobMessageFilter filter, @NotNull Pageable pageable) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching all job messages for job: {}", job.getId());
            final JobMessageMapper mapper = session.getMapper(JobMessageMapper.class);
            return mapper.getAll(job, filter, pageable);
        } catch (PersistenceException exception) {
            throw new StorageException(
                format("Error fetching messages for job: %s", job.getId()), exception
            );
        }
    }

    /**
     * Get all messages for a given job
     *
     * @param job the job
     * @return a list of messages
     */
    public List<JobMessage> getAll(@NotNull Job job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching all job messages for job: {}", job.getId());
            final JobMessageMapper mapper = session.getMapper(JobMessageMapper.class);
            return mapper.getAll(job);
        } catch (PersistenceException exception) {
            throw new StorageException(
                format("Error fetching messages for job: %s", job.getId()), exception
            );
        }
    }

    /**
     * Create the interceptor
     *
     * @param keyGenerator the generator for creating unique identifiers
     * @return a new interceptor
     */
    private JobMessageInterceptor createInterceptor(@NotNull final KeyGenerator keyGenerator) {
        return new JobMessageInterceptor(requireNonNull(keyGenerator, "keyGenerator cannot be null"));
    }

}
