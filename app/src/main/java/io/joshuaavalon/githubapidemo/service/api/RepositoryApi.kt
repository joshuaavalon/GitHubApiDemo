package io.joshuaavalon.githubapidemo.service.api

import io.joshuaavalon.githubapidemo.service.model.Repository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @see <a href="https://developer.github.com/v3/repos/">Repositories | GitHub Developer Guide</a>
 */
interface RepositoryApi {

    @GET("/user/repos")
    fun listRepositories(@Header("Authorization") authorization: String,
                         @Query("visibility") visibility: String?,
                         @Query("affiliation") affiliation: String?,
                         @Query("type") type: String?,
                         @Query("sort") sort: String?,
                         @Query("direction") direction: String?)
            : Call<List<Repository>>

    @GET("/users/{defaultUser}/repos?per_page=100")
    fun listUserRepositories(@Path("defaultUser") userName: String,
                             @Query("type") type: String?,
                             @Query("sort") sort: String?,
                             @Query("direction") direction: String?)
            : Call<List<Repository>>
}