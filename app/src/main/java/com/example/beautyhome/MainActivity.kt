package com.example.beautyhome

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.beautyhome.presentation.auth.SignInScreen
import com.example.beautyhome.presentation.navigation.SetupNavHost
import com.example.beautyhome.presentation.screens.MainScreen
import com.example.beautyhome.presentation.viewmodel.AuthViewModel
import com.example.beautyhome.ui.theme.BeautyHomeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeautyHomeTheme {
                val navController = rememberNavController()
                SetupNavHost(navHostController = navController)
            }
        }
    }
}