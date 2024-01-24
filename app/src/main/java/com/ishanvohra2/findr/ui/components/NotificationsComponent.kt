package com.ishanvohra2.findr.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ishanvohra2.findr.R
import com.ishanvohra2.findr.data.NotificationResponseItem
import com.ishanvohra2.findr.viewModels.NotificationViewModel

class NotificationsComponent(
    private val onBackPressed: () -> Unit,
    private val onProfileClicked: (s: String) -> Unit
) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NotificationsPage(notiViewModel: NotificationViewModel = viewModel()){
        notiViewModel.getNotifications()
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.notifications)
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
            NotificationList(notiViewModel)
        }
    }

    @Composable
    fun NotificationList(notiViewModel: NotificationViewModel = viewModel()){
        when(val state =
            notiViewModel.notificationUiState.collectAsState().value){
            is NotificationViewModel.NotificationsUiState.ErrorState -> {
                Text(
                    text = stringResource(id = R.string.empty_noti_message),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.xxlarge_spacing))
                )
            }
            NotificationViewModel.NotificationsUiState.LoadingState -> {
                LoadingState()
            }
            is NotificationViewModel.NotificationsUiState.SuccessState -> {
                if(state.list.isEmpty()){
                    Text(
                        text = stringResource(id = R.string.empty_noti_message),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.xxlarge_spacing))
                    )
                }
                else{
                    val listState = rememberLazyListState()
                    LazyColumn(state = listState){
                        items(state.list.count()) { i ->
                            NotificationItem(state.list[i])
                        }
                    }
                    LaunchedEffect(key1 = listState.canScrollForward, block = {
                        if(!listState.canScrollForward){
                            notiViewModel.nextPage()
                        }
                    })
                }
            }
        }
    }

    @Composable
    fun NotificationItem(notification: NotificationResponseItem) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.large_spacing),
                    vertical = dimensionResource(id = R.dimen.small_spacing),
                ),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_radius))
        ){
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            dimensionResource(id = R.dimen.small_spacing)
                        )
                        .clickable { onProfileClicked(notification.repository.owner.login) }
                ){
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(
                                notification.repository.owner.avatar_url
                            )
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.medium_spacing))
                    ) {
                        Text(
                            text = notification.subject.title,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.small_spacing)
                        )
                ) {
                    Text(
                        text = notification.repository.name,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .weight(1f)
                    )
                    val context = LocalContext.current
                    IconButton(onClick = {
                        val i = Intent(Intent.ACTION_VIEW)
                        val url = notification.repository.html_url
                        i.data = Uri.parse(url)
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

}