package com.ishanvohra2.findr.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ishanvohra2.findr.R
import com.ishanvohra2.findr.viewModels.EditProfileViewModel

class EditProfileComponent(
    private val onBackPressed: () -> Unit
) {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditProfilePage(editProfileViewModel: EditProfileViewModel = viewModel()){
        editProfileViewModel.getProfile()
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_profile)
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
            Box(modifier = Modifier.fillMaxWidth()) {
                UserDetails(body = editProfileViewModel.profileDetails.collectAsState().value) {
                    editProfileViewModel.updateProfile(it)
                }
                when(val state =
                    editProfileViewModel.updateProfileUiState.collectAsState().value){
                    is EditProfileViewModel.UpdateProfileUiState.ErrorState -> {
                        Toast
                            .makeText(
                                LocalContext.current,
                                state.message?:"Something went wrong. Try again",
                                Toast.LENGTH_LONG
                            ).show()
                    }
                    EditProfileViewModel.UpdateProfileUiState.LoadingState -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    is EditProfileViewModel.UpdateProfileUiState.SuccessState -> {
                        Toast
                            .makeText(
                                LocalContext.current,
                                "Profile updated!",
                                Toast.LENGTH_LONG
                            ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    @Composable
    fun UserDetails(body: Map<String, Any?>, onUpdateClick: (m: Map<String, Any?>) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.xlarge_spacing)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .data(body["avatar_url"] as String)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            val nameState = remember{
                mutableStateOf(body["name"] as String)
            }
            val blogState = remember{
                mutableStateOf((body["bio"] as String?)?:"")
            }
            val bioState = remember{
                mutableStateOf((body["bio"] as String?)?:"" )
            }
            val companyState = remember{
                mutableStateOf((body["company"] as String?)?:"")
            }
            OutlinedTextField(
                value = nameState.value,
                onValueChange = {
                    nameState.value = it
                },
                label = {
                    Text(text = stringResource(id = R.string.name_hint))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.large_spacing))
            )
            OutlinedTextField(
                value = bioState.value,
                onValueChange = { bioState.value = it },
                label = {
                    Text(text = stringResource(id = R.string.bio_hint))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = dimensionResource(id = R.dimen.large_spacing),
                        start = dimensionResource(id = R.dimen.large_spacing),
                        end = dimensionResource(id = R.dimen.large_spacing),
                    )
            )
            OutlinedTextField(
                value = blogState.value,
                onValueChange = { blogState.value = it },
                label = {
                    Text(text = stringResource(id = R.string.blog_hint))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = dimensionResource(id = R.dimen.large_spacing),
                        start = dimensionResource(id = R.dimen.large_spacing),
                        end = dimensionResource(id = R.dimen.large_spacing),
                    )
            )
            OutlinedTextField(
                value = companyState.value,
                onValueChange = { companyState.value = it },
                label = {
                    Text(text = stringResource(id = R.string.company_hint))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = dimensionResource(id = R.dimen.large_spacing),
                        start = dimensionResource(id = R.dimen.large_spacing),
                        end = dimensionResource(id = R.dimen.large_spacing),
                    )
            )
            Button(
                onClick = { onUpdateClick(
                    mapOf(
                        "name" to nameState.value,
                        "blog" to blogState.value,
                        "bio" to bioState.value,
                        "company" to companyState.value
                    )
                ) }
            ) {
                Text(text = stringResource(id = R.string.update_button))
            }
        }
    }

}