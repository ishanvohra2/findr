package com.ishanvohra2.findr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.ui.components.HomeComponents
import com.ishanvohra2.findr.ui.components.ProfileComponent
import com.ishanvohra2.findr.ui.theme.FindrTheme
import com.ishanvohra2.findr.viewModels.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private val viewModel by lazy {
        MainViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FindrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home"){
                        composable("home"){
                            HomeComponents{
                                viewModel.selectedUser = it
                                navController.navigate("profile")
                            }.HomePage()
                        }
                        composable("profile"){
                            viewModel.selectedUser?.let { user ->
                                ProfileComponent(
                                    onProfileClicked = {
                                        viewModel.selectedUser = it
                                        navController.navigate("profile")
                                    },
                                    onBackPressed = {
                                        viewModel.selectedUser = null
                                        navController.popBackStack()
                                    },
                                ).ProfilePage(user = user)
                            }
                        }
                    }
                }
            }
        }
    }
}