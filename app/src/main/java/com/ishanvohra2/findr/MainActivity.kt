package com.ishanvohra2.findr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.ishanvohra2.findr.ui.components.EditProfileComponent
import com.ishanvohra2.findr.ui.components.FollowerFollowingComponent
import com.ishanvohra2.findr.ui.components.HomeComponents
import com.ishanvohra2.findr.ui.components.NotificationsComponent
import com.ishanvohra2.findr.ui.components.ProfileComponent
import com.ishanvohra2.findr.ui.components.UserProfileComponent
import com.ishanvohra2.findr.ui.theme.FindrTheme
import com.ishanvohra2.findr.viewModels.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private val authProvider by lazy {
        OAuthProvider.newBuilder("github.com")
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val viewModel by lazy {
        MainViewModel()
    }

    private fun logout(){
        firebaseAuth.signOut()
        viewModel.updateUserProfileUiState(
            MainViewModel.UserProfileUiState.LoggedOutState()
        )
    }

    private fun checkIfUserAlreadyLoggedIn(){
        lifecycleScope.launch {
            if(firebaseAuth.currentUser != null){
                authProvider.scopes = listOf(
                    "user",
                    "repo",
                    "notifications",
                    "read:org"
                )
                firebaseAuth.currentUser!!.startActivityForReauthenticateWithProvider(
                    this@MainActivity,
                    authProvider.build()
                ).addOnFailureListener{
                    logout()
                }.addOnSuccessListener {
                    it.credential?.let { cred -> cred as OAuthCredential
                        it?.additionalUserInfo?.profile?.let { map ->
                            viewModel.updateUserProfileUiState(
                                MainViewModel.UserProfileUiState.SuccessState(
                                    map,
                                    cred.accessToken?:""
                                )
                            )
                        }?: run{
                            logout()
                        }
                    }?: run{
                        logout()
                    }
                }
            }
            else{
                viewModel.updateUserProfileUiState(
                    MainViewModel.UserProfileUiState.LoggedOutState()
                )
            }
        }
    }

    private fun checkIfAuthResultPresent(){
        val pendingResult = firebaseAuth.pendingAuthResult
        pendingResult?.let {
            it.addOnSuccessListener { result ->
                result?.additionalUserInfo?.profile?.let { map ->
                    viewModel.updateUserProfileUiState(
                        MainViewModel.UserProfileUiState.SuccessState(
                            map,
                            (result.credential as OAuthCredential).accessToken?:""
                        )
                    )
                }
            }.addOnFailureListener {
                //Handle failure
            }
        }?: run {
            startLoginFlow()
        }
    }

    private fun startLoginFlow(){
        authProvider.scopes = listOf(
            "user",
            "repo",
            "notifications",
            "read:org"
        )
        firebaseAuth
            .startActivityForSignInWithProvider(this, authProvider.build())
            .addOnSuccessListener { result ->
                result?.additionalUserInfo?.profile?.let { map ->
                    viewModel.updateUserProfileUiState(
                        MainViewModel.UserProfileUiState.SuccessState(
                            map,
                            (result.credential as OAuthCredential).accessToken?:""
                        )
                    )
                }

            }.addOnFailureListener {
                //Handle failure
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfUserAlreadyLoggedIn()
        setContent {
            FindrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home"){
                        composable("home"){
                            HomeComponents(
                                { username ->
                                    navController.navigate("profile/$username")
                                },
                                {
                                    navController.navigate("userProfile")
                                },
                                {
                                    navController.navigate("notifications")
                                }
                            ).HomePage(mainViewModel = viewModel)
                        }
                        composable("profile/{username}"){
                            it.arguments?.getString("username")?.let { username ->
                                ProfileComponent(
                                    onBackPressed = {
                                        navController.popBackStack()
                                    },
                                    onProfileClicked = { user ->
                                        navController.navigate("profile/$user")
                                    },
                                    { user ->
                                        navController.navigate("followerFollowing/$user")
                                    },
                                    viewModel.uiState.value
                                ).ProfilePage(username = username)
                            }
                        }
                        composable("userProfile"){
                            UserProfileComponent(
                                {
                                    navController.popBackStack()
                                },
                                {
                                    checkIfAuthResultPresent()
                                },
                                { username ->
                                    navController.navigate("profile/$username")
                                },
                                {
                                    navController.navigate("followerFollowing/$it")
                                },
                                {
                                    navController.navigate("editProfile")
                                },
                                {
                                    logout()
                                }
                            ).UserProfilePage(viewModel.uiState)
                        }
                        composable("followerFollowing/{username}"){
                            it.arguments?.getString("username")?.let { username ->
                                FollowerFollowingComponent(
                                    {
                                        navController.popBackStack()
                                    },{ user ->
                                        navController.navigate("profile/$username")
                                    }
                                ).FollowerFollowingPage(username)
                            }
                        }
                        composable("editProfile"){
                            EditProfileComponent{
                                navController.popBackStack()
                            }.EditProfilePage()
                        }
                        composable("notifications"){
                            NotificationsComponent({
                                navController.popBackStack()
                            }, { username ->
                                navController.navigate("profile/$username")
                            }).NotificationsPage()
                        }
                    }
                }
            }
        }
    }
}