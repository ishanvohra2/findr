package com.ishanvohra2.findr.viewModels

import androidx.lifecycle.ViewModel
import com.ishanvohra2.findr.data.SearchUsersResponse

class MainViewModel: ViewModel() {

    var selectedUser: SearchUsersResponse.Item? = null
        get() = field
        set(value) {
            field = value
        }

}