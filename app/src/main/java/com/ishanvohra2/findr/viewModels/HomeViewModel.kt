package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ishanvohra2.findr.combineState
import com.ishanvohra2.findr.data.EventResponseItem
import com.ishanvohra2.findr.data.SearchRepositoriesResponse
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.datastore.DataStoreConstants
import com.ishanvohra2.findr.datastore.DataStoreManager
import com.ishanvohra2.findr.repositories.HomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

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
    val receivedEvents = MutableStateFlow<ReceivedEventsUiState>(
        ReceivedEventsUiState.LoadingState
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

    fun fetchTrendingUsers(refresh: Boolean = false){
        CoroutineScope(Dispatchers.IO).launch {
            if(trendingUsersFlow.value is TrendingUsersUiState.SuccessState && !refresh) {
                return@launch
            }
            trendingUsersFlow.emit(TrendingUsersUiState.LoadingState)
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

    fun fetchTrendingRepos(refresh: Boolean = false){
        CoroutineScope(Dispatchers.IO).launch {
            if(trendingReposFlow.value is TrendingReposUiState.SuccessState && !refresh) {
                return@launch
            }
            trendingReposFlow.emit(TrendingReposUiState.LoadingState)
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
        searchJob = CoroutineScope(Dispatchers.IO).launch {
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
        CoroutineScope(Dispatchers.IO).launch {
            searchJob?.cancel()
            searchFlow.emit(SearchUserUiState.IdleState)
        }
    }

    fun getReceivedEvents(refresh: Boolean = false){
        CoroutineScope(Dispatchers.IO).launch {
            if(receivedEvents.value is ReceivedEventsUiState.SuccessState && !refresh) {
                return@launch
            }
            val dataStore by
            KoinJavaComponent.inject<DataStoreManager>(DataStoreManager::class.java)
            dataStore.getPreference(DataStoreConstants.USER_PROFILE)
                .firstOrNull()
                ?.let {
                    val map = Gson().fromJson(it, Map::class.java)
                            as Map<*, *>
                    receivedEvents.emit(ReceivedEventsUiState.LoadingState)
                    HomeRepository().fetchReceivedEvents(
                        username = map["login"].toString()
                    ).run {
                        if(this.isSuccessful && body() != null){
                            receivedEvents.emit(ReceivedEventsUiState.SuccessState(body()!!))
                        }
                        else{
                            receivedEvents.emit(
                                ReceivedEventsUiState.ErrorState(
                                    errorBody()?.string()
                                )
                            )
                        }
                    }
                }
                ?: run{
                    receivedEvents.emit(ReceivedEventsUiState.ErrorState(null))
                }
        }
    }

    sealed class ReceivedEventsUiState{
        data object LoadingState: ReceivedEventsUiState()
        data class ErrorState(val message: String?): ReceivedEventsUiState()

        data class SuccessState(val list: List<EventResponseItem>): ReceivedEventsUiState()
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