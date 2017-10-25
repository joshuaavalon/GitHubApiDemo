package io.joshuaavalon.githubapidemo.service

import android.util.Base64
import io.joshuaavalon.githubapidemo.service.model.Repository

/**
 * Implementation of GitHub REST API v3 with Authentication
 */
class AuthGitHubService(userName: String, password: String) : GitHubService() {

    private val authorization = "Basic ${
    Base64.encodeToString("$userName:$password".toByteArray(Charsets.UTF_8), Base64.NO_WRAP)}"

    fun listRepositories(onResponse: (List<Repository>) -> Unit,
                         onFailure: (Throwable) -> Unit,
                         visibility: String? = null,
                         affiliation: String? = null,
                         type: String? = null,
                         sort: String? = null,
                         direction: String? = null) {
        enqueue(onResponse, onFailure) {
            repositoryApi.listRepositories(authorization, visibility, affiliation, type, sort, direction)
        }
    }
}