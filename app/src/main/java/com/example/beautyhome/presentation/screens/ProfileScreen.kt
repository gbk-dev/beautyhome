package com.example.beautyhome.presentation.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.beautyhome.presentation.navigation.AuthScreens
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.UserViewModel
import com.example.beautyhome.ui.theme.BeautyHomeTheme
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.Purple200
import com.example.domain.models.User
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun ProfileScreen(
    viewModel: UserViewModel = viewModel(),
    navController: NavController
) {

    viewModel.getUser()
    viewModel.getImg()
    val user = viewModel.userList.value
    val imgUser = viewModel.img.value

    BeautyHomeTheme{
        val state = rememberCollapsingToolbarScaffoldState()
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri -> viewModel.uploadImg(uri.toString()) }
                )
                if ((18 + (30 - 12) * state.toolbarState.progress).sp.value != 18f){
                    FloatingActionButton(onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(imageVector = Icons.Outlined.AddCircle, contentDescription = null)
                    }
                }

            },
            floatingActionButtonPosition = androidx.compose.material.FabPosition.End,
            isFloatingActionButtonDocked = true,
            topBar = {CustomToolbar(navController = navController, viewModel = viewModel, state = state, user = user, imgUser = imgUser)},

        ) {
            it.calculateTopPadding()
        }
    }
}

@Composable
fun CustomToolbar(
    viewModel: UserViewModel = viewModel(),
    navController: NavController,
    state: CollapsingToolbarScaffoldState,
    user: User?,
    imgUser: String?
) {
    Column(
        modifier = Modifier
            .background(color = DefBlack)
    ) {

        Spacer(
            modifier = Modifier
                .height(32.dp)
        )
        CollapsingToolbarScaffold(
            modifier = Modifier,
            state = state,
            scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
            toolbar = {

                val textSize = (18 + (30 - 12) * state.toolbarState.progress).sp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .pin()
                        .background(color = Color.Transparent)
                )

                Image(
                    modifier= Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .parallax(),
                    painter = rememberAsyncImagePainter(model = imgUser),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = if (textSize.value == 18f) 0f else 1f
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(color = Color.Transparent)
                ) {

                    IconButton(
                        onClick = {
                            navController.navigate(Screens.UserMain.route)
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "back to main screen", tint = Purple200)
                    }

                    Spacer(modifier = Modifier.weight(1f, true))

                    IconButton(
                        onClick = {
                            viewModel.signOut()
                            navController.navigate(AuthScreens.SignIn.route)
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = null, tint = Purple200)
                    }
                }
                Log.e("ProfileScreenUser", user.toString())
                val text = if (textSize.value != 18f) "${user?.firstName} ${user?.lastName}\n${user?.email}" else "${user?.firstName} ${user?.lastName}"
                Text(
                    text = text,
                    style = TextStyle(color = Color.White, fontSize = textSize),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp, bottom = 20.dp)
                        .road(
                            whenCollapsed = Alignment.TopCenter,
                            whenExpanded = Alignment.BottomStart
                        )
                )

            }

        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
            ) {

                viewModel.getRecord()
                val record = viewModel.recordList.value
                val userName = record?.userName
                val service = record?.service
                val date = record?.date
                val time = record?.time

                Text(
                    text = userName.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.White
                )
                Text(
                    text = service.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.White
                )
                Text(
                    text = date.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.White
                )
                Text(
                    text = time.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.White
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent)
                ){
                    items(10){
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "This is settings number $it",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}