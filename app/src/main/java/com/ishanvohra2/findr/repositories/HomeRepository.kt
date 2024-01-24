package com.ishanvohra2.findr.repositories

import com.ishanvohra2.findr.networking.RetrofitClient
import org.koin.java.KoinJavaComponent.inject

class HomeRepository {

    private val retrofitClient by inject<RetrofitClient>(RetrofitClient::class.java)

    suspend fun fetchTrendingUsers() = retrofitClient.instance.fetchTrendingUsers()

    suspend fun fetchTrendingRepos() = retrofitClient.instance.fetchTrendingRepositories()

    suspend fun searchUser(
        query: String,
        sortBy: String = "followers",
        limit: Int = 10
    ) = retrofitClient.instance.searchUsers(
        query, sortBy, limit
    )

    suspend fun searchRepos(
        query: String = "stars%3A%3E%3D1000",
        sortBy: String = "stars",
        limit: Int = 10
    ) = retrofitClient.instance.searchRepositories(
        query, sortBy, limit
    )

    suspend fun fetchReceivedEvents(page: Int = 1, username: String) = retrofitClient
        .instance
        .getReceivedEventsByUsername(username, page = page)

}