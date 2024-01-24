package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra2.findr.data.SearchRepositoriesResponse
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FollowerFollowingViewModel: ViewModel() {

    val followersUiState = MutableStateFlow<ListUiState>(
        ListUiState.LoadingState
    )

    val followingUiState = MutableStateFlow<ListUiState>(
        ListUiState.LoadingState
    )

    val reposState = MutableStateFlow<ReposUiState>(
        ReposUiState.LoadingState
    )

    private var followingCurrentPage = 1
    private var followersCurrentPage = 1
    private var reposCurrentPage = 1

    fun getRepos(username: String){
        viewModelScope.launch {
            ProfileRepository().getRepos(username, reposCurrentPage).run {
                if(isSuccessful && body() != null){
                    if(body()!!.isEmpty())
                        reposCurrentPage -= 1
                    reposState.update {
                        when(it){
                            is ReposUiState.ErrorState -> {
                                reposCurrentPage -= 1
                                it
                            }
                            ReposUiState.LoadingState -> {
                                ReposUiState.SuccessState(body()!!)
                            }
                            is ReposUiState.SuccessState -> {
                                it.copy(
                                    it.items + body()!!
                                )
                            }
                        }
                    }
                }
                else{
                    reposCurrentPage -= 1
                    reposState.emit(ReposUiState.ErrorState(null))
                }
            }
        }
    }

    fun nextPageRepos(username: String){
        reposCurrentPage += 1
        getRepos(username)
    }

    fun getFollowers(username: String){
        viewModelScope.launch {
            ProfileRepository().getFollowers(username, followersCurrentPage).run {
                if(isSuccessful && body() != null){
                    if(body()!!.isEmpty())
                        followersCurrentPage -= 1

                    followersUiState.update {
                        when(it){
                            is ListUiState.ErrorState -> {
                                followersCurrentPage -= 1
                                it
                            }
                            ListUiState.LoadingState -> {
                                ListUiState.SuccessState(body()!!)
                            }
                            is ListUiState.SuccessState -> {
                                it.copy(it.items + body()!!)
                            }
                        }
                    }
                }
                else{
                    followersCurrentPage -= 1
                    followersUiState.emit(ListUiState.ErrorState(null))
                }
            }
        }
    }

    fun getFollowing(username: String){
        viewModelScope.launch {
            ProfileRepository().getFollowing(username, followingCurrentPage).run {
                if(isSuccessful && body() != null){
                    followingUiState.update {
                        when(it){
                            is ListUiState.ErrorState -> {
                                followingCurrentPage -= 1
                                it
                            }
                            ListUiState.LoadingState -> ListUiState.SuccessState(body()!!)
                            is ListUiState.SuccessState -> {
                                if(body()!!.isEmpty())
                                    followingCurrentPage -= 1

                                it.copy(it.items + body()!!)
                            }
                        }
                    }
                }
                else{
                    followingCurrentPage -= 1
                    followingUiState.emit(ListUiState.ErrorState(null))
                }
            }
        }
    }

    fun getNextPageFollowing(username: String){
        followingCurrentPage += 1
        getFollowing(username)
    }

    fun getNextPageFollowers(username: String){
        followersCurrentPage += 1
        getFollowers(username)
    }

    sealed class ReposUiState(){
        data object LoadingState: ReposUiState()
        data class SuccessState(
            val items: List<SearchRepositoriesResponse.Item>
        ): ReposUiState()
        data class ErrorState(
            val message: String?
        ): ReposUiState()
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