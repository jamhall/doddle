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
import dev.doddle.storage.common.domain.JobFilter;
import dev.doddle.storage.common.domain.JobStatistic;
import dev.doddle.storage.common.domain.Pageable;
import dev.doddle.storage.common.interceptors.JobInterceptor;
import dev.doddle.storage.common.support.KeyGenerator;
import dev.doddle.storage.common.support.UUIDKeyGenerator;
import dev.doddle.storage.sql.mappers.JobMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class JobDao {

    private final static Logger            logger = LoggerFactory.getLogger(JobDao.class);
    private final        SqlSessionFactory sqlSessionFactory;
    private final        JobInterceptor    interceptor;

    public JobDao(@NotNull final SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, new UUIDKeyGenerator());
    }

    public JobDao(@NotNull final SqlSessionFactory sqlSessionFactory, @NotNull final KeyGenerator keyGenerator) {
        this.sqlSessionFactory = requireNonNull(sqlSessionFactory, "sqlSessionFactory cannot be null");
        this.interceptor = new JobInterceptor(requireNonNull(keyGenerator, "keyGenerator cannot be null"));
    }

    public Long countAll(@NotNull JobFilter filter) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Count all jobs for the filter: {}", filter);
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.countAll(filter);
        } catch (PersistenceException exception) {
            throw new StorageException("Error counting jobs", exception);
        }
    }

    public Job create(@NotNull final Job job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final Job record = interceptor.apply(job);
            logger.debug("Creating job with id: {}", record.getId());
            final JobMapper mapper = session.getMapper(JobMapper.class);
            mapper.create(record);
            session.commit();
            return record;
        } catch (PersistenceException exception) {
            throw new StorageException("Error creating job", exception);
        }
    }

    public void delete(@NotNull final Job job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Deleting job with the id: {}", job.getId());
            final JobMapper mapper = session.getMapper(JobMapper.class);
            mapper.delete(job);
            session.commit();
        } catch (PersistenceException exception) {
            throw new StorageException("Error deleting job", exception);
        }
    }

    public void deleteAll() throws StorageException {
        deleteAll(null);
    }

    public void deleteAll(final LocalDateTime date) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Deleting all jobs");
            final JobMapper mapper = session.getMapper(JobMapper.class);
            mapper.deleteAll(date);
            session.commit();
        } catch (PersistenceException exception) {
            throw new StorageException("Error deleting all jobs", exception);
        }
    }

    public List<Job> enqueue() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Enqueuing jobs ready to be processed");
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.enqueue();
        } catch (PersistenceException exception) {
            throw new StorageException("Error enqueuing job", exception);
        }
    }

    public List<Job> getAll(@NotNull Pageable pageable) throws StorageException {
        return getAll(new JobFilter(), pageable);
    }

    public List<Job> getAll() throws StorageException {
        return getAll(new JobFilter(), null);
    }

    public List<Job> getAll(@NotNull JobFilter filter, @NotNull Pageable pageable) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching all jobs");
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.getAll(filter, pageable);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching all jobs", exception);
        }
    }

    public List<String> getAllTasks() {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching all tasks");
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.getAllTasks();
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching all tasks", exception);
        }
    }

    public Optional<Job> getById(@NotNull final String id) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Getting job with the id: {}", id);
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.getById(id);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching job", exception);
        }
    }

    public Optional<Job> getByIdentifier(@NotNull final String identifier) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Getting job with the identifier: {}", identifier);
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.getByIdentifier(identifier);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching job", exception);
        }
    }

    public JobStatistic getStatistics() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching job statistics");
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.getStatistics();
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching job statistics", exception);
        }
    }

    public Optional<Job> pick() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Picking job");
            final JobMapper mapper = session.getMapper(JobMapper.class);
            return mapper.pick();
        } catch (PersistenceException exception) {
            throw new StorageException("Error picking job", exception);
        }
    }

    public Job update(@NotNull final Job job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Updating job: {}", job);
            final JobMapper mapper = session.getMapper(JobMapper.class);
            mapper.update(job);
            session.commit();
            return job;
        } catch (PersistenceException exception) {
            throw new StorageException("Error deleting job", exception);
        }
    }

}
