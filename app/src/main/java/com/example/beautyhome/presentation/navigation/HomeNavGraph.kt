package com.example.beautyhome.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.beautyhome.presentation.screens.AdminMainScreen
import com.example.beautyhome.presentation.screens.ListOfRecordsScreen
import com.example.beautyhome.presentation.screens.UserMainScreen
import com.example.beautyhome.presentation.screens.ProfileScreen
import com.example.beautyhome.presentation.utils.Constants
import com.example.beautyhome.presentation.viewmodel.RecordViewModel
import com.example.beautyhome.presentation.viewmodel.UserViewModel


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeNavGraph(navController: NavHostController){
    navigation(
        route = Constants.Graph.HOME,
        startDestination = Screens.UserMain.route
    ){
        composable(
            route = Screens.UserMain.route
        ){
            val viewModel = hiltViewModel<RecordViewModel>()
            UserMainScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = Screens.AdminMain.route
        ){
            val viewModel = hiltViewModel<RecordViewModel>()
            AdminMainScreen(navController = navController, viewModel = viewModel)
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