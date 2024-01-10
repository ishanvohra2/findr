package com.ishanvohra2.findr.networking

import com.ishanvohra2.findr.data.SearchRepositoriesResponse
import com.ishanvohra2.findr.data.SearchUsersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("search/users?q=followers%3A%3E%3D1000&ref=searchresults&s=followers&type=Users" +
            "&per_page=5")
    suspend fun fetchTrendingUsers(): Response<SearchUsersResponse>

    @GET("search/repositories?q=stars%3A%3E%3D1000&ref=searchresults&s=stars&type=Repositories" +
            "&per_page=5")
    suspend fun fetchTrendingRepositories(): Response<SearchRepositoriesResponse>

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q")query: String,
        @Query("sort")sortBy: String,
        @Query("per_page")limit: Int
    ): Response<SearchUsersResponse>

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q")query: String,
        @Query("sort")sortBy: String,
        @Query("per_page")limit: Int
    ): Response<SearchRepositoriesResponse>

    @GET("users/{username}/followers")
    suspend fun getFollowersByUsername(
        @Path("username")username: String
    ): Response<List<SearchUsersResponse.Item>>

    @GET("users/{username}/following")
    suspend fun getFollowingByUsername(
        @Path("username")username: String
    ): Response<List<SearchUsersResponse.Item>>

}