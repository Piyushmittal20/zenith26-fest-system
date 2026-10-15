package com.zenith26.fest.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zenith26.fest.data.api.ApiClient
import com.zenith26.fest.data.api.ZenithApiService
import com.zenith26.fest.data.model.DeleteResponse
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.RegistrationDetail
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.StatsOut
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository encapsulating data operations and network error parsing.
 */
class FestRepository(private val apiService: ZenithApiService = ApiClient.service) {

    private val gson = Gson()

    suspend fun getEvents(category: String?, search: String?): Result<List<Event>> = withContext(Dispatchers.IO) {
        try {
            val catParam = if (category.isNullOrBlank() || category.equals("All", ignoreCase = true)) null else category
            val searchParam = if (search.isNullOrBlank()) null else search.trim()
            val events = apiService.getEvents(catParam, searchParam)
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventDetail(eventId: Int): Result<Event> = withContext(Dispatchers.IO) {
        try {
            val event = apiService.getEventDetail(eventId)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerStudent(eventId: Int, payload: RegistrationIn): Result<RegistrationOut> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.registerStudent(eventId, payload)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string(), response.code())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRegistrations(eventId: Int? = null): Result<List<RegistrationDetail>> = withContext(Dispatchers.IO) {
        try {
            val list = apiService.getRegistrations(eventId)
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStats(): Result<StatsOut> = withContext(Dispatchers.IO) {
        try {
            val stats = apiService.getStats()
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(payload: EventCreateIn): Result<Event> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createEvent(payload)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string(), response.code())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelRegistration(registrationId: Int): Result<DeleteResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.cancelRegistration(registrationId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string(), response.code())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(errorJson: String?, statusCode: Int): String {
        if (errorJson.isNullOrBlank()) {
            return when (statusCode) {
                400 -> "Bad Request: Please check your input."
                404 -> "Not Found: The requested item does not exist."
                409 -> "Conflict: Registration could not be completed."
                else -> "Server error ($statusCode)"
            }
        }
        return try {
            val obj = gson.fromJson(errorJson, JsonObject::class.java)
            if (obj.has("detail")) {
                obj.get("detail").asString
            } else {
                errorJson
            }
        } catch (_: Exception) {
            errorJson
        }
    }
}
