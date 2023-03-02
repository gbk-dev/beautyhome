package com.example.beautyhome.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.beautyhome.presentation.navigation.AuthScreens
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.UserViewModel
import com.example.beautyhome.ui.theme.*
import com.example.domain.models.Record
import com.example.domain.models.User
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun ProfileScreen(
    viewModel: UserViewModel,
    navController: NavController
) {

    BeautyHomeTheme{
        viewModel.getUser()
        viewModel.getImg()
        viewModel.getRecord()

        val state = rememberCollapsingToolbarScaffoldState()
        val scaffoldState = rememberScaffoldState()

        val user = viewModel.user.value
        val imgUser = viewModel.img.value
        val record = viewModel.record.value

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
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = true,
            topBar = {
                CustomToolbar(
                    navController = navController,
                    viewModel = viewModel,
                    state = state,
                    user = user,
                    imgUser = imgUser,
                    record = record
                )
                     },

        ) {
            it.calculateTopPadding()
        }
    }
}

@Composable
fun CustomToolbar(
    viewModel: UserViewModel,
    navController: NavController,
    state: CollapsingToolbarScaffoldState,
    user: User?,
    imgUser: String?,
    record: Record?
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
                            if (user?.master!!){
                                navController.navigate(Screens.AdminMain.route)
                            } else {
                                navController.navigate(Screens.UserMain.route)
                            }
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
                val text = if (textSize.value != 18f) "${user?.firstName} ${user?.lastName}\n${user?.email}" else "${user?.firstName} ${user?.lastName}"
                Text(
                    text = text,
                    style = TextStyle(color = darkWhite, fontSize = textSize),
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
                    .fillMaxSize()
                    .background(color = DefBlack)
                    .padding(8.dp)
            ) {
                viewModel.getUser().runCatching {
                    val master = user?.master!!

                    val firstName = user.firstName.orEmpty()
                    val lastName = user.lastName.orEmpty()
                    val email = user.email.orEmpty()
                    val phone = user.phone.orEmpty()

                    val fontSize = 18.sp
                    val color = Purple

                    viewModel.getRecord().runCatching {
                        if (!master && record != null) {

                            val service = record.service
                            val date = record.date
                            val time = record.time

                            Card(
                                backgroundColor = CalendarBack,
                                border = BorderStroke(0.5.dp, Purple200),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column {
                                    Text(
                                        text = firstName,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, top = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = lastName,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = email,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = phone,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp, bottom = 16.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                backgroundColor = CalendarBack,
                                border = BorderStroke(0.5.dp, Purple200),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier.background(color = Color.Transparent)
                                ) {
                                    Text(
                                        text = service,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, top = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = date,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = time,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp, bottom = 16.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                }
                            }
                        } else {
                            Card(
                                backgroundColor = CalendarBack,
                                border = BorderStroke(0.5.dp, Purple200),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column {
                                    Text(
                                        text = firstName,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, top = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = lastName,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = email,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                    Text(
                                        text = phone,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, 8.dp, bottom = 16.dp),
                                        color = color,
                                        fontSize = fontSize
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}