package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra2.findr.combineState
import com.ishanvohra2.findr.data.EventResponseItem
import com.ishanvohra2.findr.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel: ViewModel() {

    val eventsUiState = MutableStateFlow<EventsUiState>(EventsUiState.LoadingState)
    val profileUiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.LoadingState)
    val isFollowedByUser = MutableStateFlow(false)

    private var currentEventPage = 1

    fun userProfileFlow(): StateFlow<Pair<UserProfileUiState, Boolean>>{
        return combineState(
            profileUiState,
            isFollowedByUser,
            viewModelScope
        ){ o1, o2 ->
            Pair(o1, o2)
        }
    }

    fun followUser(username: String){
        viewModelScope.launch {
            ProfileRepository().followUser(username).run {
                if(code() == 204)
                    isFollowedByUser.emit(true)
            }
        }
    }

    fun unfollowUser(username: String){
        viewModelScope.launch {
            ProfileRepository().unfollowUser(username).run {
                if(code() == 204)
                    isFollowedByUser.emit(false)
            }
        }
    }

    private suspend fun isFollowedByUser(username: String){
        ProfileRepository().checkIfUserIsFollowed(username).run {
            if(code() == 204)
                isFollowedByUser.emit(true)
            else
                isFollowedByUser.emit(false)
        }
    }

    fun getUserByUsername(username: String){
        viewModelScope.launch {
            isFollowedByUser(username)
            ProfileRepository().getUserByUsername(username).run {
                if(isSuccessful && body()!= null){
                    profileUiState.emit(UserProfileUiState.SuccessState(body()!!))
                }
                else{
                    profileUiState.emit(UserProfileUiState.ErrorState())
                }
            }
        }
    }

    fun getRecentEvents(username: String){
        viewModelScope.launch {
            ProfileRepository().fetchEvents(username, currentEventPage).run {
                if(isSuccessful && body()!= null){
                    if(body()!!.isEmpty()) {
                        currentEventPage -= 1
                    }
                    eventsUiState.update {
                        when(it){
                            is EventsUiState.ErrorState -> {
                                currentEventPage -= 1
                                it
                            }
                            EventsUiState.LoadingState -> {
                                EventsUiState.SuccessState(body()!!)
                            }
                            is EventsUiState.SuccessState -> {
                                it.copy(it.list + body()!!)
                            }
                        }
                    }
                }
                else{
                    currentEventPage -= 1
                    eventsUiState.emit(EventsUiState.ErrorState())
                }
            }
        }
    }

    fun nextPageEvents(username: String){
        currentEventPage += 1
        getRecentEvents(username)
    }

    sealed class EventsUiState{
        data object LoadingState: EventsUiState()
        data class SuccessState(val list: List<EventResponseItem>): EventsUiState()
        data class ErrorState(val message: String? = null): EventsUiState()
    }

    sealed class UserProfileUiState{
        data object LoadingState: UserProfileUiState()
        data class SuccessState(val userDetails: Map<String, Any?>): UserProfileUiState()
        data class ErrorState(val message: String? = null): UserProfileUiState()

    }

}