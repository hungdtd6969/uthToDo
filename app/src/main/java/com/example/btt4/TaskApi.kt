package com.example.btt4

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object TaskApi {
    private const val BASE_URL = "https://share.tryapps.store/mock/teach"

    suspend fun getTasks(token: String): List<ApiTask> = withContext(Dispatchers.IO) {
        val response = request("GET", "/tasks", token)
        requireStatus(response, 200)
        val array = JSONArray(response.body)
        (0 until array.length()).map { apiTaskFromJson(array.getJSONObject(it)) }
    }

    suspend fun getTask(token: String, id: Int): ApiTask = withContext(Dispatchers.IO) {
        val response = request("GET", "/tasks/$id", token)
        requireStatus(response, 200)
        apiTaskFromJson(JSONObject(response.body))
    }

    suspend fun createTask(token: String, task: ListTask): ApiTask = withContext(Dispatchers.IO) {
        val response = request("POST", "/tasks", token, task.toCreateJson())
        requireStatus(response, 201)
        apiTaskFromJson(JSONObject(response.body))
    }

    suspend fun updateTask(token: String, task: ListTask) = withContext(Dispatchers.IO) {
        val response = request("PATCH", "/tasks/${task.id}", token, task.toPatchJson())
        requireStatus(response, 200)
    }

    suspend fun deleteTask(token: String, id: Int) = withContext(Dispatchers.IO) {
        val response = request("DELETE", "/tasks/$id", token)
        requireStatus(response, 204)
    }

    private fun request(
        method: String,
        path: String,
        token: String,
        body: JSONObject? = null
    ): ApiResponse {
        val connection = (URL(BASE_URL + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Authorization", "Bearer $token")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
        }
        return try {
            if (body != null) {
                connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val responseBody = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            ApiResponse(status, responseBody)
        } finally {
            connection.disconnect()
        }
    }

    private fun requireStatus(response: ApiResponse, expected: Int) {
        if (response.status != expected) {
            val message = runCatching { JSONObject(response.body).optString("message") }.getOrNull()
                .orEmpty().ifBlank { response.body }
            throw IOException("API trả lỗi ${response.status}: $message")
        }
    }

    private data class ApiResponse(val status: Int, val body: String)
}
