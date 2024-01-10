package com.ishanvohra2.findr.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ishanvohra2.findr.R
import com.ishanvohra2.findr.data.SearchRepositoriesResponse
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.viewModels.HomeViewModel

class HomeComponents(
    private val onProfileClicked: (userItem: SearchUsersResponse.Item) -> Unit
){

    private val blurState = mutableStateOf(0.dp)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomePage(
        homeViewModel: HomeViewModel = viewModel()
    ){
        homeViewModel.fetchTrendingUsers()
        homeViewModel.fetchTrendingRepos()
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                SearchField(homeViewModel)
                Column(
                    modifier = Modifier
                        .blur(blurState.value)
                        .align(Alignment.CenterHorizontally)
                ) {
                    TrendingComponent(homeViewModel)
                }
            }
        }
    }

    @Composable
    fun TrendingComponent(homeViewModel: HomeViewModel){
        homeViewModel.homeUiFlow().collectAsState().value.run {
            when(val s = this.first){
                is HomeViewModel.TrendingUsersUiState.ErrorState -> {
                    //TODO Show error
                }
                HomeViewModel.TrendingUsersUiState.LoadingState -> {
                    LoadingState()
                }
                is HomeViewModel.TrendingUsersUiState.SuccessState -> {
                    Text(
                        text = stringResource(id = R.string.users_title),
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(id = R.dimen.small_spacing),
                                start = dimensionResource(id = R.dimen.border_margin),
                                end = dimensionResource(id = R.dimen.border_margin),
                                bottom = dimensionResource(id = R.dimen.small_spacing)
                            ),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = TextUnit(24f, TextUnitType.Sp)
                    )
                    s.response.items?.let {
                        for (user in it){
                            UserItem(user){onProfileClicked(user)}
                        }
                    }
                }
            }
            when(val s = this.second){
                is HomeViewModel.TrendingReposUiState.ErrorState -> {
                    //TODO Show error
                }
                HomeViewModel.TrendingReposUiState.LoadingState -> {
                    if(this.first !is HomeViewModel.TrendingUsersUiState.LoadingState){
                        LoadingState()
                    } else{}
                }
                is HomeViewModel.TrendingReposUiState.SuccessState -> {
                    Text(
                        text = stringResource(id = R.string.repos_title),
                        modifier = Modifier
                            .padding(
                                top = dimensionResource(id = R.dimen.small_spacing),
                                start = dimensionResource(id = R.dimen.border_margin),
                                end = dimensionResource(id = R.dimen.border_margin),
                                bottom = dimensionResource(id = R.dimen.small_spacing)
                            ),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = TextUnit(24f, TextUnitType.Sp)
                    )
                    s.response.items?.let {
                        for (repo in it){
                            RepositoryItem(repo)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchField(homeViewModel: HomeViewModel){
        Box {
            Column {
                var searchText by remember {
                    mutableStateOf(TextFieldValue(""))
                }
                OutlinedTextField(
                    shape = RoundedCornerShape(40.dp),
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.search_bar_hint))
                    },
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.small_spacing),
                            start = dimensionResource(id = R.dimen.border_margin),
                            end = dimensionResource(id = R.dimen.border_margin),
                            bottom = dimensionResource(id = R.dimen.small_spacing)
                        )
                        .zIndex(10f)
                        .fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.small_spacing)),
                        )
                    },
                )
                when(val state =
                    homeViewModel.searchFlow.collectAsState().value){
                    HomeViewModel.SearchUserUiState.IdleState -> {
                        blurState.value = 0.dp
                    }
                    HomeViewModel.SearchUserUiState.LoadingState -> {
                        blurState.value = 20.dp
                        Searching()
                    }
                    is HomeViewModel.SearchUserUiState.SuccessState -> {
                        blurState.value = 20.dp
                        if(state.users == null && state.repos == null){
                            Text(
                                text = stringResource(id = R.string.no_search_results),
                                modifier = Modifier
                                    .padding(
                                        top = dimensionResource(id = R.dimen.small_spacing),
                                        start = dimensionResource(id = R.dimen.border_margin),
                                        end = dimensionResource(id = R.dimen.border_margin),
                                        bottom = dimensionResource(id = R.dimen.small_spacing)
                                    ),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = TextUnit(18f, TextUnitType.Sp)
                            )
                        }
                        if(!state.users?.items.isNullOrEmpty()){
                            Text(
                                text = stringResource(id = R.string.search_users),
                                modifier = Modifier
                                    .padding(
                                        top = dimensionResource(id = R.dimen.small_spacing),
                                        start = dimensionResource(id = R.dimen.border_margin),
                                        end = dimensionResource(id = R.dimen.border_margin),
                                        bottom = dimensionResource(id = R.dimen.small_spacing)
                                    ),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = TextUnit(18f, TextUnitType.Sp)
                            )
                            state.users?.items?.forEach { user ->
                                UserItem(user = user) { onProfileClicked(it) }
                            }
                        }
                        if(!state.repos?.items.isNullOrEmpty()){
                            Text(
                                text = stringResource(id = R.string.search_repos),
                                modifier = Modifier
                                    .padding(
                                        top = dimensionResource(id = R.dimen.small_spacing),
                                        start = dimensionResource(id = R.dimen.border_margin),
                                        end = dimensionResource(id = R.dimen.border_margin),
                                        bottom = dimensionResource(id = R.dimen.small_spacing)
                                    ),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = TextUnit(18f, TextUnitType.Sp)
                            )
                            state.repos?.items?.forEach {
                                RepositoryItem(repo = it)
                            }
                        }
                    }
                }

                LaunchedEffect(key1 = searchText, block = {
                    if(searchText.text.isBlank()){
                        blurState.value = 0.dp
                        homeViewModel.clearSearchData()
                    }
                    else if(searchText.text.length > 3){
                        homeViewModel.fetchSearchResponse(searchText.text)
                    }
                })
            }
        }
    }

    @Composable
    fun Searching(){
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val composition by rememberLottieComposition(
                LottieCompositionSpec
                    .RawRes(R.raw.searching)
            )
            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .size(300.dp),
                iterations = LottieConstants.IterateForever
            )
        }

    }

    @Composable
    fun RepositoryItem(repo: SearchRepositoriesResponse.Item){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.large_spacing),
                    vertical = dimensionResource(id = R.dimen.small_spacing),
                ),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_radius))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        dimensionResource(id = R.dimen.small_spacing)
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(start = dimensionResource(id = R.dimen.medium_spacing))
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = repo.name,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = TextUnit(16f, TextUnitType.Sp)
                    )
                    Text(
                        text = repo.description?:"",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = TextUnit(14f, TextUnitType.Sp),
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.small_spacing))
                    )
                }
                val context = LocalContext.current
                IconButton(onClick = {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(repo.html_url)
                    context.startActivity(i)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Link,
                        contentDescription = "link",
                    )
                }
            }
        }
    }
}