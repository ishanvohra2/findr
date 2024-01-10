package com.ishanvohra2.findr.repositories

import com.ishanvohra2.findr.networking.RetrofitClient
import org.koin.java.KoinJavaComponent

class ProfileRepository {

    private val retrofitClient by KoinJavaComponent.inject<RetrofitClient>(RetrofitClient::class.java)

    suspend fun getFollowers(
        username: String
    ) = retrofitClient.instance.getFollowersByUsername(username)

    suspend fun getFollowing(
        username: String
    ) = retrofitClient.instance.getFollowingByUsername(username)

}