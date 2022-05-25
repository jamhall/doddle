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
import dev.doddle.storage.common.domain.Queue;
import dev.doddle.storage.common.interceptors.QueueInterceptor;
import dev.doddle.storage.common.support.KeyGenerator;
import dev.doddle.storage.common.support.UUIDKeyGenerator;
import dev.doddle.storage.sql.mappers.QueueMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class QueueDao {

    private final static Logger            logger = LoggerFactory.getLogger(QueueDao.class);
    private final        SqlSessionFactory sqlSessionFactory;
    private final        QueueInterceptor  interceptor;

    /**
     * Create a new instance
     *
     * @param sqlSessionFactory the persistence session factory
     */
    public QueueDao(@NotNull final SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, new UUIDKeyGenerator());
    }

    /**
     * Create a new instance
     *
     * @param sqlSessionFactory the persistence session factory
     * @param keyGenerator      the generator for creating unique identifiers
     */
    public QueueDao(@NotNull final SqlSessionFactory sqlSessionFactory, @NotNull final KeyGenerator keyGenerator) {
        this.sqlSessionFactory = requireNonNull(sqlSessionFactory, "sqlSessionFactory cannot be null");
        this.interceptor = createInterceptor(keyGenerator);
    }

    /**
     * Count all queues
     *
     * @return the number of queues
     */
    public Integer countAll() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Count all queues");
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            return mapper.countAll();
        } catch (PersistenceException exception) {
            throw new StorageException("Error counting queues", exception);
        }
    }

    /**
     * Create a new queue
     *
     * @param queue the queue
     * @return the created queue
     */
    public Queue create(final @NotNull Queue queue) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            final Queue record = interceptor.apply(queue);
            logger.debug("Creating queue with id: {}", record.getId());
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            mapper.create(record);
            session.commit();
            return record;
        } catch (PersistenceException exception) {
            throw new StorageException("Error creating queue", exception);
        }
    }

    /**
     * Delete a queue
     *
     * @param queue the queue to delete
     */
    public void delete(@NotNull final Queue queue) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Deleting queue with the id: {}", queue.getId());
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            mapper.delete(queue);
            session.commit();
        } catch (PersistenceException exception) {
            throw new StorageException("Error deleting queue", exception);
        }
    }

    /**
     * Delete all queues
     */
    public void deleteAll() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Deleting all queues");
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            mapper.deleteAll();
            session.commit();
        } catch (PersistenceException exception) {
            throw new StorageException("Error deleting all queues", exception);
        }
    }

    /**
     * Get all queues
     *
     * @return a list of queues
     */
    public List<Queue> getAll() throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Fetching all queues");
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            return mapper.getAll();
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching queues", exception);
        }
    }

    /**
     * Get a queue by the given identifier
     *
     * @param id the identifier of the queue
     * @return the queue
     */
    public Optional<Queue> getById(@NotNull final String id) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Getting queue with the id: {}", id);
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            return mapper.getById(id);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching queue", exception);
        }
    }

    /**
     * Get a queue by the given name
     *
     * @param name the name of the queue
     * @return the queue
     */
    public Optional<Queue> getByName(@NotNull final String name) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Getting queue with the name: {}", name);
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            return mapper.getByName(name);
        } catch (PersistenceException exception) {
            throw new StorageException("Error fetching queue", exception);
        }
    }

    /**
     * Update a given queue
     *
     * @param queue the queue to update
     * @return the updated queue
     */
    public Queue update(@NotNull final Queue queue) throws StorageException {
        try (final SqlSession session = sqlSessionFactory.openSession()) {
            logger.debug("Updating queue: {}", queue);
            final QueueMapper mapper = session.getMapper(QueueMapper.class);
            mapper.update(queue);
            session.commit();
            return queue;
        } catch (PersistenceException exception) {
            throw new StorageException("Error updating queue", exception);
        }
    }

    /**
     * Create the interceptor
     *
     * @param keyGenerator the generator for creating unique identifiers
     * @return a new interceptor
     */
    private QueueInterceptor createInterceptor(@NotNull final KeyGenerator keyGenerator) {
        return new QueueInterceptor(requireNonNull(keyGenerator, "keyGenerator cannot be null"));
    }
}
