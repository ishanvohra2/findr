package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishanvohra2.findr.combineState
import com.ishanvohra2.findr.data.SearchRepositoriesResponse
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.repositories.HomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val trendingUsersFlow = MutableStateFlow<TrendingUsersUiState>(
        TrendingUsersUiState.LoadingState
    )
    private val trendingReposFlow = MutableStateFlow<TrendingReposUiState>(
        TrendingReposUiState.LoadingState
    )
    val searchFlow = MutableStateFlow<SearchUserUiState>(
        SearchUserUiState.IdleState
    )

    private var searchJob: Job? = null

    fun homeUiFlow(): StateFlow<Pair<TrendingUsersUiState, TrendingReposUiState>> {
        return combineState(
            trendingUsersFlow,
            trendingReposFlow,
            viewModelScope
        ){ o1, o2 ->
            Pair(o1, o2)
        }
    }

    fun fetchTrendingUsers(){
        viewModelScope.launch {
            if(trendingUsersFlow.value is TrendingUsersUiState.SuccessState) {
                return@launch
            }
            HomeRepository().fetchTrendingUsers().run {
                if(this.isSuccessful && this.body()!=null){
                    trendingUsersFlow.emit(TrendingUsersUiState.SuccessState(
                        this.body()!!
                    ))
                }
                else{
                    trendingUsersFlow.emit(TrendingUsersUiState.ErrorState(null))
                }
            }
        }
    }

    fun fetchTrendingRepos(){
        viewModelScope.launch {
            if(trendingReposFlow.value is TrendingReposUiState.SuccessState) {
                return@launch
            }
            HomeRepository().fetchTrendingRepos().run {
                if(this.isSuccessful && this.body()!=null){
                    trendingReposFlow.emit(TrendingReposUiState.SuccessState(
                        this.body()!!
                    ))
                }
                else{
                    trendingReposFlow.emit(TrendingReposUiState.ErrorState(null))
                }
            }
        }
    }

    fun fetchSearchResponse(query: String){
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchFlow.emit(SearchUserUiState.LoadingState)
            delay(3000L)
            val users: SearchUsersResponse? = HomeRepository().searchUser(query).body()
            val repos: SearchRepositoriesResponse? = HomeRepository().searchRepos(query).body()
            searchFlow.emit(
                SearchUserUiState.SuccessState(
                    users, repos
                )
            )
        }
    }

    fun clearSearchData() {
        viewModelScope.launch {
            searchJob?.cancel()
            searchFlow.emit(SearchUserUiState.IdleState)
        }
    }

    sealed class SearchUserUiState{
        data object LoadingState: SearchUserUiState()
        data object IdleState: SearchUserUiState()
        data class SuccessState(
            val users: SearchUsersResponse?,
            val repos: SearchRepositoriesResponse?
        ): SearchUserUiState()
    }

    sealed class TrendingUsersUiState{
        data object LoadingState: TrendingUsersUiState()
        data class ErrorState(val message: String?): TrendingUsersUiState()
        data class SuccessState(val response: SearchUsersResponse): TrendingUsersUiState()
    }

    sealed class TrendingReposUiState{
        data object LoadingState: TrendingReposUiState()
        data class ErrorState(val message: String?): TrendingReposUiState()
        data class SuccessState(val response: SearchRepositoriesResponse): TrendingReposUiState()
    }

}