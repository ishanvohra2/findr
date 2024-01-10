package com.ishanvohra2.findr.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ishanvohra2.findr.R
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.viewModels.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileComponent(
    private val onBackPressed: () -> Unit,
    private val onProfileClicked: (userItem: SearchUsersResponse.Item) -> Unit
) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfilePage(
        user: SearchUsersResponse.Item,
        profileViewModel: ProfileViewModel = viewModel()
    ){
        profileViewModel.getFollowers(user.login)
        profileViewModel.getFollowing(user.login)
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_page),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Rounded.DarkMode,
                            contentDescription = "dark mode",
                        )
                    }
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
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.avatar_url)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.large_spacing))
                        .size(200.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "@${user.login}",
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.small_spacing),
                            start = dimensionResource(id = R.dimen.border_margin),
                            end = dimensionResource(id = R.dimen.border_margin),
                            bottom = dimensionResource(id = R.dimen.small_spacing)
                        )
                        .align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = TextUnit(22f, TextUnitType.Sp)
                )
                Text(
                    text = user.url,
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.small_spacing),
                            start = dimensionResource(id = R.dimen.border_margin),
                            end = dimensionResource(id = R.dimen.border_margin),
                            bottom = dimensionResource(id = R.dimen.small_spacing)
                        )
                        .clickable {
                            //TODO open profile link
                        }
                        .align(Alignment.CenterHorizontally),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = TextUnit(16f, TextUnitType.Sp)
                )
                FollowerFollowing(
                    profileViewModel = profileViewModel
                )
            }
        }
    }
    
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun FollowerFollowing(profileViewModel: ProfileViewModel){
        val pagerState = rememberPagerState(pageCount = {2})
        val selectedInt = remember {
            mutableIntStateOf(0)
        }
        TabRow(selectedTabIndex = selectedInt.intValue) {
            Tab(selected = true, onClick = {
                selectedInt.intValue = 0
                CoroutineScope(Dispatchers.Main).launch {
                    pagerState.scrollToPage(0)
                }
            }) {
                Text(
                    text = stringResource(id = R.string.followers),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.large_spacing))
                )
            }
            Tab(selected = false, onClick = {
                selectedInt.intValue = 1
                CoroutineScope(Dispatchers.Main).launch {
                    pagerState.scrollToPage(1)
                }
            }) {
                Text(
                    text = stringResource(id = R.string.followers),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.small_spacing))
                )
            }
        }
        HorizontalPager(state = pagerState) {
            when(it){
                0 -> {
                    FollowersList(profileViewModel = profileViewModel)
                }
                1 -> {
                    FollowingList(profileViewModel = profileViewModel)
                }
            }
        }
        LaunchedEffect(key1 = pagerState.currentPage, block = {
            selectedInt.intValue = pagerState.currentPage
        })
    }

    @Composable
    fun FollowingList(profileViewModel: ProfileViewModel){
        when(val state =
            profileViewModel.followingUiState.collectAsState().value){
            is ProfileViewModel.ListUiState.ErrorState -> {
                //TODO
            }
            ProfileViewModel.ListUiState.LoadingState -> {
                LoadingState()
            }
            is ProfileViewModel.ListUiState.SuccessState -> {
                UsersListPager(users = state.items)
            }
        }
    }

    @Composable
    fun FollowersList(profileViewModel: ProfileViewModel){
        when(val state =
            profileViewModel.followersUiState.collectAsState().value){
            is ProfileViewModel.ListUiState.ErrorState -> {
                //TODO
            }
            ProfileViewModel.ListUiState.LoadingState -> {
                LoadingState()
            }
            is ProfileViewModel.ListUiState.SuccessState -> {
                UsersListPager(users = state.items)
            }
        }
    }

    @Composable
    fun UsersListPager(
        users: List<SearchUsersResponse.Item>
    ){
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            users.forEach { user ->
                UserItem(user = user, onProfileClicked = { onProfileClicked(user) })
            }
        }
    }

}