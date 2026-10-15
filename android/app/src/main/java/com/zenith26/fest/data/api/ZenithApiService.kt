package com.zenith26.fest.data.api

import com.zenith26.fest.data.model.DeleteResponse
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.RegistrationDetail
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.StatsOut
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface defining the exact Zenith '26 REST API contract.
 */
interface ZenithApiService {

    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): List<Event>

    @GET("api/events/{event_id}")
    suspend fun getEventDetail(
        @Path("event_id") eventId: Int
    ): Event

    @POST("api/events/{event_id}/register")
    suspend fun registerStudent(
        @Path("event_id") eventId: Int,
        @Body payload: RegistrationIn
    ): Response<RegistrationOut>

    @GET("api/registrations")
    suspend fun getRegistrations(
        @Query("event_id") eventId: Int? = null
    ): List<RegistrationDetail>

    @GET("api/stats")
    suspend fun getStats(): StatsOut

    // Stretch Goals
    @POST("api/events")
    suspend fun createEvent(
        @Body payload: EventCreateIn
    ): Response<Event>

    @DELETE("api/registrations/{registration_id}")
    suspend fun cancelRegistration(
        @Path("registration_id") registrationId: Int
    ): Response<DeleteResponse>
}
