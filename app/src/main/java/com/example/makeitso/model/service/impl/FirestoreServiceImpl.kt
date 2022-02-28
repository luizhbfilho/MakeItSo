package com.example.makeitso.model.service.impl

import com.example.makeitso.model.Task
import com.example.makeitso.model.service.FirestoreService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor() : FirestoreService {
    override suspend fun getTask(
        taskId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (Task) -> Unit
    ) {
        Firebase.firestore
            .collection(TASK_COLLECTION)
            .document(taskId)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result -> onSuccess(result.toObject() ?: Task()) }
    }

    override suspend fun getTasksForUser(
        userId: String,
        onError: (Throwable) -> Unit,
        onSuccess: (List<Task>) -> Unit
    ) {
        Firebase.firestore
            .collection(TASK_COLLECTION)
            .whereEqualTo(USER_ID, userId)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result ->
                val tasks = mutableListOf<Task>()
                for (document in result) tasks.add(document.toObject())
                onSuccess(tasks)
            }
    }

    override suspend fun saveTask(task: Task, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(TASK_COLLECTION)
            .document(task.id)
            .set(task)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override suspend fun deleteTask(taskId: String, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(TASK_COLLECTION)
            .document(taskId)
            .delete()
            .addOnCompleteListener { onResult(it.exception) }
    }

    companion object {
        private const val TASK_COLLECTION = "Task"
        private const val USER_ID = "userId"
    }
}