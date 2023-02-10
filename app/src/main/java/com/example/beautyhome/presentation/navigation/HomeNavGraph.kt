package com.example.beautyhome.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.beautyhome.presentation.screens.MainScreen
import com.example.beautyhome.presentation.screens.ProfileScreen
import com.example.beautyhome.presentation.utils.Constants


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeNavGraph(navController: NavHostController){
    navigation(
        route = Constants.Graph.HOME,
        startDestination = Screens.Main.route
    ){
        composable(
            route = Screens.Main.route
        ){
            MainScreen(navController = navController)
        }
        composable(
            route = Screens.Profile.route
        ){
            ProfileScreen(navController = navController)
        }
    }
}