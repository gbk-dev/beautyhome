package com.example.beautyhome.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.beautyhome.presentation.screens.ListOfRecordsScreen
import com.example.beautyhome.presentation.screens.MainScreen
import com.example.beautyhome.presentation.screens.ProfileScreen
import com.example.beautyhome.presentation.utils.Constants
import com.example.beautyhome.presentation.viewmodel.RecordViewModel
import com.example.beautyhome.presentation.viewmodel.UserViewModel


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeNavGraph(navController: NavHostController){
    navigation(
        route = Constants.Graph.HOME,
        startDestination = Screens.Main.route
    ){
        composable(
            route = Screens.Main.route
        ){
            val viewModel = hiltViewModel<RecordViewModel>()
            MainScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = Screens.Profile.route
        ){
            val viewModel = hiltViewModel<UserViewModel>()
            ProfileScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = Screens.ListOfRecords.route
        ){
            val viewModel = hiltViewModel<RecordViewModel>()
            ListOfRecordsScreen(navController = navController, viewModel = viewModel)
        }
    }
}