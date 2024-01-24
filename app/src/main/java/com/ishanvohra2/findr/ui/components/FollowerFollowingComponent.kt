package com.ishanvohra2.findr.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ishanvohra2.findr.R
import com.ishanvohra2.findr.viewModels.FollowerFollowingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowerFollowingComponent(
    private val onBackPressed: () -> Unit,
    private val onProfileClicked: (username: String) -> Unit
) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FollowerFollowingPage(
        username: String,
        profileViewModel: FollowerFollowingViewModel = viewModel()
    ){
        profileViewModel.getFollowers(username)
        profileViewModel.getFollowing(username)
        profileViewModel.getRepos(username)
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
            FollowerFollowing(profileViewModel, username)
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun FollowerFollowing(profileViewModel: FollowerFollowingViewModel, username: String){
        val pagerState = rememberPagerState(pageCount = {3})
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
                    text = stringResource(id = R.string.following),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.small_spacing))
                )
            }
            Tab(selected = false, onClick = {
                selectedInt.intValue = 1
                CoroutineScope(Dispatchers.Main).launch {
                    pagerState.scrollToPage(2)
                }
            }) {
                Text(
                    text = stringResource(id = R.string.repos),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.small_spacing))
                )
            }
        }
        HorizontalPager(state = pagerState) {
            when(it){
                0 -> {
                    FollowersList(followerFollowingViewModel = profileViewModel, username)
                }
                1 -> {
                    FollowingList(followerFollowingViewModel = profileViewModel, username)
                }
                2 -> {
                    ReposList(followerFollowingViewModel = profileViewModel, username)
                }
            }
        }
        LaunchedEffect(key1 = pagerState.currentPage, block = {
            selectedInt.intValue = pagerState.currentPage
        })
    }

    @Composable
    fun ReposList(followerFollowingViewModel: FollowerFollowingViewModel, username: String){
        when(val state =
            followerFollowingViewModel.reposState.collectAsState().value){
            is FollowerFollowingViewModel.ReposUiState.ErrorState -> {
                /*TODO*/
            }
            FollowerFollowingViewModel.ReposUiState.LoadingState -> {
                LoadingState()
            }
            is FollowerFollowingViewModel.ReposUiState.SuccessState -> {
                val listState = rememberLazyListState()
                LazyColumn(state = listState){
                    items(state.items.count()){ i ->
                        RepositoryItem(repo = state.items[i])
                    }
                }
                LaunchedEffect(key1 = listState.canScrollForward, block = {
                    if(!listState.canScrollForward){
                        followerFollowingViewModel.nextPageRepos(username)
                    }
                })
            }
        }
    }

    @Composable
    fun FollowingList(followerFollowingViewModel: FollowerFollowingViewModel, username: String){
        when(val state =
            followerFollowingViewModel.followingUiState.collectAsState().value){
            is FollowerFollowingViewModel.ListUiState.ErrorState -> {
                //TODO
            }
            FollowerFollowingViewModel.ListUiState.LoadingState -> {
                LoadingState()
            }
            is FollowerFollowingViewModel.ListUiState.SuccessState -> {
                val listState = rememberLazyListState()
                LazyColumn(state = listState){
                    items(state.items.count()){ i ->
                        UserItem(
                            user = state.items[i],
                            onProfileClicked = { onProfileClicked(state.items[i].login) }
                        )

                        LaunchedEffect(key1 = listState.canScrollForward, block = {
                            if(!listState.canScrollForward){
                                followerFollowingViewModel.getNextPageFollowing(username)
                            }
                        })
                    }
                }
            }
        }
    }

    @Composable
    fun FollowersList(followerFollowingViewModel: FollowerFollowingViewModel, username: String){
        when(val state =
            followerFollowingViewModel.followersUiState.collectAsState().value){
            is FollowerFollowingViewModel.ListUiState.ErrorState -> {
                //TODO
            }
            FollowerFollowingViewModel.ListUiState.LoadingState -> {
                LoadingState()
            }
            is FollowerFollowingViewModel.ListUiState.SuccessState -> {
                val listState = rememberLazyListState()
                LazyColumn(state = listState){
                    items(state.items.count()){ i ->
                        UserItem(
                            user = state.items[i],
                            onProfileClicked = { onProfileClicked(state.items[i].login) }
                        )

                        LaunchedEffect(key1 = listState.canScrollForward, block = {
                            if(!listState.canScrollForward){
                                followerFollowingViewModel.getNextPageFollowers(username)
                            }
                        })
                    }
                }
            }
        }
    }

}