package com.example.beautyhome.presentation.screens

import android.net.Uri
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.beautyhome.R
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.UserViewModel
import com.example.beautyhome.ui.theme.BeautyHomeTheme
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.Purple200
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import me.onebone.toolbar.*

@Composable
fun ProfileScreen(
    viewModel: UserViewModel = viewModel(),
    navController: NavController
) {

    BeautyHomeTheme{
        val state = rememberCollapsingToolbarScaffoldState()
        val scaffoldState = rememberScaffoldState()
        viewModel.getImg()
        viewModel.getUser()

        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri -> selectedImageUri = uri }
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
                viewModel.uploadImg(selectedImageUri.toString())
            },
            floatingActionButtonPosition = androidx.compose.material.FabPosition.End,
            isFloatingActionButtonDocked = true,
            topBar = {CustomToolbar(navController = navController, viewModel = viewModel, state = state)},

        ) {
            it.calculateTopPadding()
        }
    }
}

@Composable
fun CustomToolbar(
    viewModel: UserViewModel = viewModel(),
    navController: NavController,
    state: CollapsingToolbarScaffoldState
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

                val imgUser = viewModel.img.value.orEmpty()
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

                IconButton(onClick = {
                    navController.navigate(Screens.Main.route)
                }) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "back to main screen", tint = Purple200)
                }

                val user = viewModel.userList.value
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
                Log.e("ProfileScreen", record.toString())

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