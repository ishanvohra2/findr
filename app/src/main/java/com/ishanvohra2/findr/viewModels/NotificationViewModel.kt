package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra2.findr.data.NotificationResponseItem
import com.ishanvohra2.findr.repositories.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel: ViewModel() {

    val notificationUiState = MutableStateFlow<NotificationsUiState>(
        NotificationsUiState.LoadingState
    )

    private var currentNotificationPage = 1

    fun getNotifications(){
        viewModelScope.launch {
            NotificationRepository().getNotifications().run {
                if(isSuccessful && body()!= null){
                    if(body()!!.isEmpty()){
                        currentNotificationPage -= 1
                    }
                    notificationUiState.emit(
                        NotificationsUiState
                            .SuccessState(body()!!)
                    )
                    notificationUiState.update {
                        when(it){
                            is NotificationsUiState.ErrorState -> {
                                currentNotificationPage -= 1
                                it
                            }
                            NotificationsUiState.LoadingState -> {
                                NotificationsUiState.SuccessState(body()!!)
                            }
                            is NotificationsUiState.SuccessState -> {
                                it.copy(it.list + body()!!)
                            }
                        }
                    }
                }
                else{
                    currentNotificationPage -= 1
                    notificationUiState.emit(NotificationsUiState.ErrorState())
                }
            }
        }
    }

    fun nextPage(){
        currentNotificationPage += 1
        getNotifications()
    }

    sealed class NotificationsUiState{

        data object LoadingState: NotificationsUiState()
        data class SuccessState(
            val list: List<NotificationResponseItem>
        ): NotificationsUiState()

        data class ErrorState(
            val message: String? = null
        ): NotificationsUiState()

    }

}