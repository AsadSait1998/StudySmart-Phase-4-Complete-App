package com.duotech.studysmart.repo

import com.duotech.studysmart.data.StudySmartDao
import com.duotech.studysmart.data.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: StudySmartDao) {

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    fun observeAll(): Flow<List<TaskEntity>> = dao.observeAll()
    fun observeUpcoming(): Flow<List<TaskEntity>> = dao.observeUpcoming()
    fun observeCompleted(): Flow<List<TaskEntity>> = dao.observeCompleted()

    suspend fun getById(id: Long): TaskEntity? = dao.getById(id)

    suspend fun upsert(task: TaskEntity): Long {
        return if (task.id == 0L) {
            dao.insert(task)
        } else {
            dao.update(task.copy(updatedAtMillis = System.currentTimeMillis()))
            task.id
        }
    }

    suspend fun delete(task: TaskEntity) = dao.delete(task)
}
