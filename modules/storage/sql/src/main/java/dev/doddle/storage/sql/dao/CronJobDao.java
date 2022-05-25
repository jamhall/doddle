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
import dev.doddle.storage.common.domain.CronJob;
import dev.doddle.storage.common.domain.Pageable;
import dev.doddle.storage.common.interceptors.CronJobInterceptor;
import dev.doddle.storage.common.support.KeyGenerator;
import dev.doddle.storage.common.support.UUIDKeyGenerator;
import dev.doddle.storage.sql.mappers.CronJobMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class CronJobDao {

    private final static Logger             logger = LoggerFactory.getLogger(CronJobDao.class);
    private final        SqlSessionFactory  sqlSessionFactory;
    private final        CronJobInterceptor interceptor;

    /**
     * Create a new instance
     *
     * @param sqlSessionFactory the persistence session factory
     */
    public CronJobDao(@NotNull final SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, new UUIDKeyGenerator());
    }

    /**
     * Create a new instance
     *
     * @param sqlSessionFactory the persistence session factory
     * @param keyGenerator      the generator for creating unique identifiers
     */
    public CronJobDao(@NotNull final SqlSessionFactory sqlSessionFactory, @NotNull final KeyGenerator keyGenerator) {
        this.sqlSessionFactory = requireNonNull(sqlSessionFactory, "sqlSessionFactory cannot be null");
        this.interceptor = createInterceptor(keyGenerator);
    }

    /**
     * Count all cron jobs
     *
     * @return the number of cron jobs
     */
    public Integer countAll() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Count all cron jobs");
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            return mapper.countAll();
        } catch (PersistenceException exception) {
            throw new StorageException("Error counting cron jobs", exception);
        }
    }

    /**
     * Create a new cron job
     *
     * @param job the cron job
     * @return the created cron job
     */
    public CronJob create(final @NotNull CronJob job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final CronJob record = interceptor.apply(job);
            logger.debug("Creating cron job with id: {}", record.getId());
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            mapper.create(record);
            session.commit();
            return record;
        } catch (PersistenceException exception) {
            throw new StorageException("Error creating cron job", exception);
        }
    }

    /**
     * Delete a cron job
     *
     * @param job the cron job to delete
     */
    public void delete(@NotNull final CronJob job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Deleting cron job with the id: {}", job.getId());
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            mapper.delete(job);
            session.commit();
        } catch (PersistenceException exception) {
            throw new StorageException("Error deleting cron job", exception);
        }
    }

    /**
     * Delete all cron jobs
     */
    public void deleteAll() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Deleting all cron jobs");
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            mapper.deleteAll();
            session.commit();
        } catch (PersistenceException exception) {
            throw new StorageException("Error deleting all cron jobs", exception);
        }
    }

    /**
     * Get all cron jobs
     *
     * @return a list of cron jobs
     */
    public List<CronJob> getAll() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching all cron jobs");
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            return mapper.getAll();
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching cron jobs", exception);
        }
    }

    /**
     * Get all cron jobs
     *
     * @param pageable the pageable (limit, offset)
     * @return a list of messages
     */
    public List<CronJob> getAll(@NotNull Pageable pageable) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching all cron jobs");
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            return mapper.getAll(pageable);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching cron jobs", exception);
        }
    }

    /**
     * Get a cron job by the given identifier
     *
     * @param id the identifier of the cron job
     * @return the cron job
     */
    public Optional<CronJob> getById(@NotNull final String id) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Getting cron job with the id: {}", id);
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            return mapper.getById(id);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching cron job", exception);
        }
    }

    /**
     * Get a cron job by the given name
     *
     * @param name the name of the cron job
     * @return the cron job
     */
    public Optional<CronJob> getByName(@NotNull final String name) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Getting cron job with the name: {}", name);
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            return mapper.getByName(name);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching cron job", exception);
        }
    }

    /**
     * Update a given cron job
     *
     * @param job the cron job to update
     * @return the updated cron job
     */
    public CronJob update(@NotNull final CronJob job) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Updating cron job: {}", job);
            final CronJobMapper mapper = session.getMapper(CronJobMapper.class);
            mapper.update(job);
            session.commit();
            return job;
        } catch (PersistenceException exception) {
            throw new StorageException("Error updating cron job", exception);
        }
    }

    /**
     * Create the interceptor
     *
     * @param keyGenerator the generator for creating unique identifiers
     * @return a new interceptor
     */
    private CronJobInterceptor createInterceptor(@NotNull final KeyGenerator keyGenerator) {
        return new CronJobInterceptor(requireNonNull(keyGenerator, "keyGenerator cannot be null"));
    }

}
