package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

    val followersUiState = MutableStateFlow<ListUiState>(
        ListUiState.LoadingState
    )

    val followingUiState = MutableStateFlow<ListUiState>(
        ListUiState.LoadingState
    )

    fun getFollowers(username: String){
        viewModelScope.launch {
            followersUiState.emit(ListUiState.LoadingState)
            ProfileRepository().getFollowers(username).run {
                if(isSuccessful && body() != null){
                    followersUiState.emit(ListUiState.SuccessState(body()!!))
                }
                else{
                    followersUiState.emit(ListUiState.ErrorState(null))
                }
            }
        }
    }

    fun getFollowing(username: String){
        viewModelScope.launch {
            followingUiState.emit(ListUiState.LoadingState)
            ProfileRepository().getFollowing(username).run {
                if(isSuccessful && body() != null){
                    followingUiState.emit(ListUiState.SuccessState(body()!!))
                }
                else{
                    followingUiState.emit(ListUiState.ErrorState(null))
                }
            }
        }
    }

    sealed class ListUiState(){
        data object LoadingState: ListUiState()
        data class SuccessState(
            val items: List<SearchUsersResponse.Item>
        ): ListUiState()
        data class ErrorState(
            val message: String?
        ): ListUiState()
    }

}