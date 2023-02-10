package com.example.beautyhome.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.beautyhome.presentation.auth.SignInScreen
import com.example.beautyhome.presentation.auth.SignUpScreen
import com.example.beautyhome.presentation.screens.MainScreen
import com.example.beautyhome.presentation.screens.ProfileScreen
import com.example.beautyhome.presentation.utils.Constants
import com.example.beautyhome.presentation.viewmodel.AuthViewModel

sealed class Screens(val route: String){
    object Main: Screens(route = Constants.Screens.MAIN_SCREEN)
    object Profile: Screens(route = Constants.Screens.PROFILE_SCREEN)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(navHostController: NavHostController){
    NavHost(
        navController = navHostController,
        route = Constants.Graph.ROOT,
        startDestination = Constants.Graph.AUTHENTICATION
    ) {
        authNavGraph(navController = navHostController)
        homeNavGraph(navController = navHostController)
    }
}