package io.joshuaavalon.githubapidemo.service

import io.joshuaavalon.githubapidemo.service.api.RepositoryApi
import io.joshuaavalon.githubapidemo.service.api.UserApi
import io.joshuaavalon.githubapidemo.service.model.Repository
import io.joshuaavalon.githubapidemo.service.model.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Implementation of GitHub REST API v3 without Authentication
 */
open class GitHubService {
    private val retrofit: Retrofit

    init {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor {
                    it.proceed(it.request()
                            .newBuilder()
                            .addHeader("Accept", "application/vnd.github.v3+json")
                            .build())
                }
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()


    }

    protected val repositoryApi: RepositoryApi by lazy { retrofit.create(RepositoryApi::class.java) }
    protected val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }

    fun listUserRepositories(userName: String,
                             onResponse: (List<Repository>) -> Unit,
                             onFailure: (Throwable) -> Unit,
                             type: String? = null,
                             sort: String? = null,
                             direction: String? = null) {
        enqueue(onResponse, onFailure) { repositoryApi.listUserRepositories(userName, type, sort, direction) }
    }

    fun getUser(userName: String,
                onResponse: (User) -> Unit,
                onFailure: (Throwable) -> Unit) {
        enqueue(onResponse, onFailure) { userApi.getUser(userName) }
    }

    /**
     * Wrap the callback into Retrofit.Callback<T>.
     */
    private fun <T> wrap(onResponse: (T) -> Unit,
                         onFailure: (Throwable) -> Unit): Callback<T> =
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val result = response.body()
                    when {
                        result == null -> onFailure(NullPointerException("Null response"))
                        response.isSuccessful -> onResponse.invoke(result)
                        else -> onFailure(IOException(response.errorBody()?.string()))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    onFailure.invoke(t)
                }
            }

    /**
     * Wrap the api and callback.
     */
    protected fun <T> enqueue(
            onResponse: (T) -> Unit,
            onFailure: (Throwable) -> Unit,
            apiCall: () -> Call<T>) {
        apiCall.invoke().enqueue(wrap(onResponse, onFailure))
    }
}