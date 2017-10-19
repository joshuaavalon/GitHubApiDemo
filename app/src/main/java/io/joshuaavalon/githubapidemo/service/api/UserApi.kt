package io.joshuaavalon.githubapidemo.service.api

import io.joshuaavalon.githubapidemo.service.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("/users/{defaultUser}")
    fun getUser(@Path("defaultUser") userName: String): Call<User>
}