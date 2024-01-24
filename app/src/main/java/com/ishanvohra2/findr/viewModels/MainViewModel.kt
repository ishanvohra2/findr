package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ishanvohra2.findr.datastore.DataStoreConstants
import com.ishanvohra2.findr.datastore.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class MainViewModel: ViewModel() {

    val uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.LoggedOutState())

    fun updateUserProfileUiState(state: UserProfileUiState){
        viewModelScope.launch {
            if(state is UserProfileUiState.SuccessState){
                saveProfileDetails(state.body, state.token)
            }
            if(state is UserProfileUiState.LoggedOutState){
                clearData()
            }
            uiState.emit(state)
        }
    }

    private suspend fun clearData() {
        val dataStore by inject<DataStoreManager>(DataStoreManager::class.java)
        dataStore.clearAllPreference()
    }

    private suspend fun saveProfileDetails(body: Map<String, Any?>, token: String){
        val dataStore by inject<DataStoreManager>(DataStoreManager::class.java)
        dataStore.putPreference(DataStoreConstants.USER_PROFILE, Gson().toJson(body))
        dataStore.putPreference(DataStoreConstants.AUTH_TOKEN, token)
    }

    sealed class UserProfileUiState{
        data class SuccessState(val body: Map<String, Any?>, val token: String)
            : UserProfileUiState()
        data class LoggedOutState(val message: String? = null): UserProfileUiState()
        data object LoadingState: UserProfileUiState()
    }

}