package com.example.beautyhome.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beautyhome.R
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.Purple200
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun ProfileScreen(navController: NavController) {

    val state = rememberCollapsingToolbarScaffoldState()

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
                        .fillMaxSize()
                        .parallax(),
                    painter = painterResource(id = R.drawable.ic_profile_image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = if (textSize.value == 18f) 0f else 1f
                )

                IconButton(onClick = {
                    navController.navigate(Screens.Main.route)
                }) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "back to main screen", tint = Purple200)
                }

                Text(
                    "Collapsing Toolbar",
                    style = TextStyle(color = Color.White, fontSize = textSize),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp, bottom = 20.dp)
                        .road(whenCollapsed = Alignment.TopCenter, whenExpanded = Alignment.BottomStart)
                )


            }
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent)
            ){
                items(100){
                    Card(
                        modifier= Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "This is card number $it",
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