package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ishanvohra2.findr.datastore.DataStoreConstants
import com.ishanvohra2.findr.datastore.DataStoreManager
import com.ishanvohra2.findr.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class EditProfileViewModel: ViewModel() {

    val updateProfileUiState = MutableStateFlow<UpdateProfileUiState>(
        UpdateProfileUiState.IdleState
    )

    val profileDetails = MutableStateFlow<Map<String, Any?>>(mapOf())

    fun getProfile(){
        viewModelScope.launch {
            val dataStore by inject<DataStoreManager>(DataStoreManager::class.java)
            dataStore.getPreference(DataStoreConstants.USER_PROFILE)
                .firstOrNull()?.let {
                    val map = Gson().fromJson(it, Map::class.java)
                            as Map<String, Any?>
                    profileDetails.emit(map)
                }
        }
    }

    private suspend fun saveProfile(map: Map<String, Any?>){
        val dataStore by inject<DataStoreManager>(DataStoreManager::class.java)
        dataStore.putPreference(
            DataStoreConstants.USER_PROFILE,
            Gson().toJson(map)
        )
        profileDetails.emit(map)
    }

    fun updateProfile(map: Map<String, Any?>){
        viewModelScope.launch {
            ProfileRepository().updateUser(Gson().toJson(map)).run {
                if (isSuccessful && body() != null){
                    saveProfile(body()!!)
                    updateProfileUiState.emit(
                        UpdateProfileUiState.SuccessState(body()!!)
                    )
                }
                else{
                    updateProfileUiState.emit(
                        UpdateProfileUiState.ErrorState(
                            errorBody()?.string()
                        )
                    )
                }
            }
        }
    }

    sealed class UpdateProfileUiState{
        data object IdleState: UpdateProfileUiState()
        data object LoadingState: UpdateProfileUiState()
        data class SuccessState(val userDetails: Map<String, Any?>): UpdateProfileUiState()
        data class ErrorState(val message: String? = null): UpdateProfileUiState()

    }

}