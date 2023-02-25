package com.example.beautyhome.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.beautyhome.presentation.auth.SignInScreen
import com.example.beautyhome.presentation.auth.SignUpScreen
import com.example.beautyhome.presentation.utils.Constants
import com.example.beautyhome.presentation.viewmodel.AuthViewModel

sealed class AuthScreens(val route: String){
    object SignIn: Screens(route = Constants.Screens.SIGN_IN_SCREEN)
    object SignUp: Screens(route = Constants.Screens.SIGN_UP_SCREEN)
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController){
    navigation(
        route = Constants.Graph.AUTHENTICATION,
        startDestination = AuthScreens.SignIn.route
    ){
        composable(
            route = AuthScreens.SignIn.route
        ){
            val viewModel = hiltViewModel<AuthViewModel>()
            SignInScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = AuthScreens.SignUp.route
        ){
            val viewModel = hiltViewModel<AuthViewModel>()
            SignUpScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}