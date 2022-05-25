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
package dev.doddle.storage.common;

import dev.doddle.storage.common.domain.*;

import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class NoopStorageProvider implements StorageProvider {

    @Override
    public String getName() {
        return "noop";
    }

    @Override
    public Integer countAllCronJobs() {
        return null;
    }

    @Override
    public Integer countAllMessagesForJob(Job job) {
        return null;
    }

    @Override
    public Integer countAllQueues() throws StorageException {
        return null;
    }

    @Override
    public Long countJobs(JobFilter filter) {
        return 0L;
    }

    @Override
    public Long countJobs() throws StorageException {
        return 0L;
    }

    @Override
    public void create(Reader reader) {

    }

    @Override
    public JobMessage createMessageForJob(Job job, JobMessage message) {
        return null;
    }

    @Override
    public void deleteAllCronJobs() {

    }

    @Override
    public void deleteAllJobs() {

    }

    @Override
    public void deleteAllJobs(LocalDateTime date) throws StorageException {

    }

    @Override
    public void deleteAllMessagesForJob(Job job) {

    }

    @Override
    public void deleteAllQueues() throws StorageException {

    }

    @Override
    public void deleteCronJob(CronJob job) {

    }

    @Override
    public void deleteJob(Job job) {

    }

    @Override
    public void deleteQueue(Queue queue) throws StorageException {

    }

    @Override
    public List<Job> enqueueJobs() {
        return null;
    }

    @Override
    public List<CronJob> getAllCronJobs() {
        return null;
    }

    @Override
    public List<CronJob> getAllCronJobs(Pageable pageable) {
        return null;
    }

    @Override
    public List<JobMessage> getAllMessagesForJob(Job job) {
        return null;
    }

    @Override
    public List<JobMessage> getAllMessagesForJob(Job job, JobMessageFilter filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<Queue> getAllQueues() {
        return null;
    }

    @Override
    public List<String> getAllTasks() throws StorageException {
        return null;
    }

    @Override
    public Optional<CronJob> getCronJobById(String id) {
        return null;
    }

    @Override
    public Optional<CronJob> getCronJobByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Job> getJobById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<Job> getJobByIdentifier(String identifier) throws StorageException {
        return Optional.empty();
    }

    @Override
    public List<Job> getJobs(Pageable pageable) {
        return null;
    }

    @Override
    public List<Job> getJobs(JobFilter filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<Job> getJobs() {
        return null;
    }

    @Override
    public Optional<Queue> getQueueById(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Queue> getQueueByName(String name) {
        return Optional.empty();
    }

    @Override
    public void load(Reader reader) {

    }

    @Override
    public Optional<Job> pickJob() {
        return Optional.empty();
    }

    @Override
    public void reset(Reader reader) {

    }

    @Override
    public CronJob saveCronJob(CronJob job) {
        return null;
    }

    @Override
    public Job saveJob(Job job) {
        return null;
    }

    @Override
    public Queue saveQueue(Queue queue) {
        return null;
    }

    @Override
    public JobStatistic getJobStatistics() throws StorageException {
        return null;
    }
}
