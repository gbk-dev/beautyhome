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
import com.example.beautyhome.presentation.utils.Constants
import com.example.beautyhome.presentation.viewmodel.AuthViewModel

sealed class Screens(val route: String){
    object SignIn: Screens(route = Constants.Screens.SIGN_IN_SCREEN)
    object SignUp: Screens(route = Constants.Screens.SIGN_UP_SCREEN)
    object Main: Screens(route = Constants.Screens.MAIN_SCREEN)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(navHostController: NavHostController){
    NavHost(
        navController = navHostController,
        startDestination = Screens.SignIn.route
    ) {
        composable(route = Screens.SignIn.route){
            val viewModel = hiltViewModel<AuthViewModel>()
            SignInScreen(viewModel, navController = navHostController)
        }
        composable(route = Screens.SignUp.route){
            val viewModel = hiltViewModel<AuthViewModel>()
            SignUpScreen(viewModel, navController = navHostController)
        }
        composable(route = Screens.Main.route){
            MainScreen()
        }
    }
}