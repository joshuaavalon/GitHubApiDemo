package io.joshuaavalon.githubapidemo.service.api

import io.joshuaavalon.githubapidemo.service.model.Repository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 *
 * @see <a href="https://developer.github.com/v3/repos/#list-all-public-repositories">Repositories | GitHub Developer Guide</a>
 */
interface RepositoryApi {

    /**
     *  @param visibility Can be one of all, public, or private. Default: all
     *  @param affiliation Comma-separated list of values. Can include:
     *
     *  owner: Repositories that are owned by the authenticated user.
     *
     *  collaborator: Repositories that the user has been added to as a collaborator.
     *
     *  organization_member: Repositories that the user has access to through being a member of an
     *  organization. This includes every repository on every team that the user is on.
     *
     *  Default: owner,collaborator,organization_member
     *  @param type Can be one of all, owner, public, private, member. Default: all
     *
     *  Will cause a 422 error if used in the same request as visibility or affiliation.
     *  @param sort
     *  @param direction
     * @see <a href="https://developer.github.com/v3/repos/#list-your-repositories">List your repositories</a>
     */

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