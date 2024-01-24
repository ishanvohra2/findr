package com.ishanvohra2.findr.repositories

import com.ishanvohra2.findr.networking.RetrofitClient
import org.koin.java.KoinJavaComponent.inject

class NotificationRepository {

    private val retrofitClient by inject<RetrofitClient>(RetrofitClient::class.java)

    suspend fun getNotifications(page: Int = 1) = retrofitClient
        .instance
        .getNotifications(page)


}