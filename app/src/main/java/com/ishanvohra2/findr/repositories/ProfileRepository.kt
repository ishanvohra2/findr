package com.ishanvohra2.findr.repositories

import com.ishanvohra2.findr.networking.RetrofitClient
import org.koin.java.KoinJavaComponent

class ProfileRepository {

    private val retrofitClient by KoinJavaComponent.inject<RetrofitClient>(RetrofitClient::class.java)

    suspend fun updateUser(map: String) =
        retrofitClient.instance.updateUser(map)

    suspend fun getFollowers(
        username: String,
        page: Int = 1
    ) = retrofitClient.instance.getFollowersByUsername(username, page)

    suspend fun getFollowing(
        username: String,
        page: Int = 1
    ) = retrofitClient.instance.getFollowingByUsername(username, page)

    suspend fun getRepos(
        username: String,
        page: Int = 1
    ) = retrofitClient.instance.getReposByUsername(username, page)

    suspend fun fetchEvents(username: String, page: Int = 1) = retrofitClient
        .instance
        .getEventsByUsername(username, page = page)

    suspend fun getUserByUsername(username: String) = retrofitClient
        .instance
        .getUserByUsername(username)

}