package com.ishanvohra2.findr.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ishanvohra2.findr.R
import com.ishanvohra2.findr.viewModels.UserProfileViewModel

class ProfileComponent(
    private val onBackPressed: () -> Unit,
    private val onProfileClicked: (username: String) -> Unit,
    private val onFollowerFollowingClicked: (username: String) -> Unit
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfilePage(
        username: String,
        profileViewModel: UserProfileViewModel = viewModel()
    ){
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_page)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIos,
                            contentDescription = "Previous page",
                        )
                    }
                }
            )
            ProfileDetails(user = username, profileViewModel = profileViewModel)
        }
    }

    @Composable
    fun ProfileDetails(profileViewModel: UserProfileViewModel, user: String){
        profileViewModel.getUserByUsername(user)
        when(val state =
            profileViewModel.profileUiState.collectAsState().value){
            is UserProfileViewModel.UserProfileUiState.ErrorState -> {/*TODO()*/
            }
            UserProfileViewModel.UserProfileUiState.LoadingState -> {
                LoadingState()
            }
            is UserProfileViewModel.UserProfileUiState.SuccessState -> {
                profileViewModel.getRecentEvents(user)
                val userDetails = state.userDetails
                Column {
                    Row(
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.large_spacing)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .data(userDetails["avatar_url"])
                                .build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = dimensionResource(id = R.dimen.large_spacing))
                                .size(150.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(id = R.dimen.large_spacing),
                                    top = dimensionResource(id = R.dimen.small_spacing),
                                    end = dimensionResource(id = R.dimen.border_margin),
                                    bottom = dimensionResource(id = R.dimen.small_spacing)
                                ),
                        ) {
                            Text(
                                text = "@${userDetails["login"]}",
                                modifier = Modifier
                                    .padding(
                                        bottom = dimensionResource(id = R.dimen.small_spacing)
                                    ),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = TextUnit(18f, TextUnitType.Sp)
                            )
                            Text(
                                text = "${userDetails["name"]}",
                                modifier = Modifier
                                    .padding(
                                        bottom = dimensionResource(id = R.dimen.small_spacing)
                                    ),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = TextUnit(16f, TextUnitType.Sp)
                            )
                            Text(
                                text = "${userDetails["bio"]?:""}",
                                modifier = Modifier
                                    .padding(
                                        bottom = dimensionResource(id = R.dimen.small_spacing)
                                    ),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = TextUnit(16f, TextUnitType.Sp)
                            )
                            Text(
                                text = "${userDetails["company"]?:""}",
                                modifier = Modifier
                                    .padding(
                                        bottom = dimensionResource(id = R.dimen.small_spacing)
                                    ),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = TextUnit(16f, TextUnitType.Sp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clickable { onFollowerFollowingClicked(user) }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                text = "${
                                    if(userDetails["followers"] is Double){
                                        (userDetails["followers"] as Double).toInt()
                                    }
                                    else{
                                        userDetails["followers"] as Int
                                    }
                                }",
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.large_spacing))
                            )
                            Text(
                                text = stringResource(id = R.string.followers)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                text = "${
                                    if(userDetails["following"] is Double){
                                        (userDetails["following"] as Double).toInt()
                                    }
                                    else{
                                        userDetails["following"] as Int
                                    }
                                }",
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.large_spacing))
                            )
                            Text(
                                text = stringResource(id = R.string.following)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                text = "${
                                    if(userDetails["public_repos"] is Double){
                                        (userDetails["public_repos"] as Double).toInt()
                                    }
                                    else{
                                        userDetails["public_repos"] as Int
                                    }
                                }",
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.large_spacing))
                            )
                            Text(
                                text = stringResource(id = R.string.public_repos)
                            )
                        }
                    }
                    RecentEvents(profileViewModel, user)
                }
            }
        }
    }

    @Composable
    fun RecentEvents(profileViewModel: UserProfileViewModel, username: String){
        when(val state =
            profileViewModel.eventsUiState.collectAsState().value){
            is UserProfileViewModel.EventsUiState.ErrorState -> {
                /*TODO*/
            }
            UserProfileViewModel.EventsUiState.LoadingState -> {
                LoadingState()
            }
            is UserProfileViewModel.EventsUiState.SuccessState -> {
                val listState = rememberLazyListState()
                LazyColumn(state = listState){
                    item {
                        Text(
                            text = stringResource(id = R.string.recent_activity),
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(id = R.dimen.border_margin),
                                    end = dimensionResource(id = R.dimen.border_margin),
                                    bottom = dimensionResource(id = R.dimen.small_spacing)
                                ),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = TextUnit(24f, TextUnitType.Sp)
                        )
                    }
                    items(state.list.count()){ i ->
                        Event(eventResponseItem = state.list[i]){ username ->
                            onProfileClicked(username)
                        }
                    }
                }
                LaunchedEffect(key1 = listState.canScrollForward){
                    if(!listState.canScrollForward){
                        profileViewModel.nextPageEvents(username)
                    }
                }
            }
        }
    }
}