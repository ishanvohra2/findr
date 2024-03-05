package com.ishanvohra2.findr.repositories

import com.ishanvohra2.findr.networking.RetrofitClient
import org.koin.java.KoinJavaComponent.inject

class ProfileRepository {

    private val retrofitClient by inject<RetrofitClient>(RetrofitClient::class.java)

    suspend fun updateUser(map: String) =
        retrofitClient.instance.updateUser(map)

    suspend fun getFollowers(
        username: String,
        page: Int = 1
    ) = retrofitClient.instanceWithoutCache.getFollowersByUsername(username, page)

    suspend fun getFollowing(
        username: String,
        page: Int = 1
    ) = retrofitClient.instanceWithoutCache.getFollowingByUsername(username, page)

    suspend fun getRepos(
        username: String,
        page: Int = 1
    ) = retrofitClient.instance.getReposByUsername(username, page)

    suspend fun fetchEvents(username: String, page: Int = 1) = retrofitClient
        .instance
        .getEventsByUsername(username, page = page)

    suspend fun getUserByUsername(username: String) = retrofitClient
        .instanceWithoutCache
        .getUserByUsername(username)

    suspend fun checkIfUserIsFollowed(username: String) = retrofitClient
        .instanceWithoutCache
        .checkIfUserIsFollowed(username)

    suspend fun followUser(username: String) = retrofitClient
        .instanceWithoutCache
        .followUser(username)

    suspend fun unfollowUser(username: String) = retrofitClient
        .instanceWithoutCache
        .unfollowUser(username)

}