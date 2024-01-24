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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ishanvohra2.findr.R
import com.ishanvohra2.findr.data.EventResponseItem
import com.ishanvohra2.findr.data.SearchRepositoriesResponse
import com.ishanvohra2.findr.data.SearchUsersResponse
import com.ishanvohra2.findr.getTextFromEvent

@Composable
fun UserItem(
    user: SearchUsersResponse.Item,
    onProfileClicked: (user: SearchUsersResponse.Item) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.large_spacing),
                vertical = dimensionResource(id = R.dimen.small_spacing),
            )
            .clickable { onProfileClicked(user) },
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
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .data(user.avatar_url)
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
                    text = user.login,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = TextUnit(16f, TextUnitType.Sp)
                )
            }
        }
    }
}

@Composable
fun LoadingState(){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val composition by rememberLottieComposition(
            LottieCompositionSpec
                .RawRes(R.raw.loading)
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
fun Event(eventResponseItem: EventResponseItem, onProfileClicked: (s: String) -> Unit){
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
                    .clickable { onProfileClicked(eventResponseItem.actor.login) }
            ){
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(eventResponseItem.actor.avatar_url)
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
                        text = "@${eventResponseItem.actor.login}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " ${getTextFromEvent(eventResponseItem)}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = TextUnit(16f, TextUnitType.Sp)
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
                    text = eventResponseItem.repo.name,
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
                    val url = eventResponseItem.repo.url
                        .replace("api.", "")
                        .replace("/repos", "")
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