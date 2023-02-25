package com.example.beautyhome.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.beautyhome.presentation.utils.Constants

sealed class Screens(val route: String){
    object UserMain: Screens(route = Constants.Screens.USER_MAIN_SCREEN)
    object AdminMain: Screens(route = Constants.Screens.ADMIN_MAIN_SCREEN)
    object Profile: Screens(route = Constants.Screens.PROFILE_SCREEN)
    object ListOfRecords: Screens(route = Constants.Screens.LIST_SCREEN)
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