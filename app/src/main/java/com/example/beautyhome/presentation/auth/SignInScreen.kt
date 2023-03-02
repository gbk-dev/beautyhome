package com.example.beautyhome.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.beautyhome.R
import com.example.beautyhome.presentation.navigation.AuthScreens
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.AuthViewModel
import com.example.beautyhome.presentation.widgets.LoadingScreen
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.Purple200
import com.example.domain.models.Resource
import com.example.domain.models.User

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    navController: NavController
){

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val stateSignIn = viewModel.signIn.value
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(stateSignIn) {
        when (stateSignIn) {
            is Resource.Success -> {
                if (stateSignIn.result) {
                    viewModel.getUser().runCatching {
                        val master = viewModel.user.value?.master
                        if (master!!){
                            navController.navigate(Screens.AdminMain.route)
                            Toast.makeText(context, "Успешно вошли как мастер", Toast.LENGTH_LONG).show()
                        } else {
                            navController.navigate(Screens.UserMain.route)
                            Toast.makeText(context, "Успешно", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            is Resource.Failure -> {
                Toast.makeText(context, stateSignIn.exception, Toast.LENGTH_LONG).show()
            }

            is Resource.Loading -> Unit
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DefBlack)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            TextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                },
                singleLine = true,
                label = { Text(text = "Email", color = Color.White) },
                placeholder = { Text(text = "Введите свой email") },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })

            )
            Spacer(modifier = Modifier.height(16.dp))
            var passwordVisibility: Boolean by remember { mutableStateOf(false) }
            TextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                },
                singleLine = true,
                label = { Text(text = "Пароль", color = Color.White) },
                placeholder = { Text(text = "Введите свой пароль") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Password Icon") },
                trailingIcon = {
                    val image = if (passwordVisibility)
                        ImageVector.vectorResource(id = R.drawable.ic_visibility)
                    else ImageVector.vectorResource(id = R.drawable.ic_visibility_off)

                    val description = if (passwordVisibility) "Hide password" else "Show password"

                    IconButton(onClick = {passwordVisibility = !passwordVisibility}){
                        Icon(imageVector  = image, description)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = {
                    if (email.value.isNotEmpty() && password.value.isNotEmpty()){
                        val user = User(email = email.value)
                        viewModel.signIn(user = user, password = password.value)
                    } else {
                        Toast.makeText(context, "Все поля должны быть заполнены", Toast.LENGTH_LONG).show()
                    }

                },
                colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Purple200, contentColor = Color.Black)
            ) {
                Text(text = "Войти")
            }
            Spacer(modifier = Modifier.height(64.dp))
            TextButton(onClick = {
                navController.navigate(AuthScreens.SignUp.route)
            }) {
                Text(text = "Нету аккаунта", color = Color.White)
            }
        }

        if (stateSignIn == Resource.Loading)
            LoadingScreen()
    }

}