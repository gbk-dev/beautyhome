package com.example.beautyhome.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.beautyhome.R
import com.example.beautyhome.presentation.navigation.AuthScreens
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.AuthViewModel
import com.example.beautyhome.presentation.widgets.LoadingScreen
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.Purple200
import com.example.beautyhome.ui.theme.Purple500
import com.example.beautyhome.ui.theme.Shapes
import com.example.domain.models.Resource
import com.example.domain.models.User

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavController
){

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val stateSignUp = viewModel.signUp.value
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(stateSignUp) {

        when (stateSignUp) {

            is Resource.Success -> {
                if (stateSignUp.result) {
                    navController.navigate(AuthScreens.SignIn.route)
                    Toast.makeText(context, "Успешно", Toast.LENGTH_LONG).show()
                }
            }

            is Resource.Failure -> {
                Toast.makeText(context, stateSignUp.exception, Toast.LENGTH_LONG).show()
            }

            is Resource.Loading -> Unit
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DefBlack)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                TextField(
                    value = firstName.value,
                    onValueChange = {
                        firstName.value = it
                    },
                    singleLine = true,
                    label = { Text(text = "Имя", color = Color.White) },
                    placeholder = { Text(text = "Введите свое имя") },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "Person Icon") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    })
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = lastName.value,
                    onValueChange = {
                        lastName.value = it
                    },
                    singleLine = true,
                    label = { Text(text = "Фамилия", color = Color.White) },
                    placeholder = { Text(text = "Введите свою фамилию") },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "Person Icon") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    })
                )
                Spacer(modifier = Modifier.height(16.dp))
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
                TextField(
                    value = phone.value,
                    onValueChange = {
                        phone.value = it
                    },
                    singleLine = true,
                    label = { Text(text = "Телефон", color = Color.White) },
                    placeholder = { Text(text = "Введите свой номер телефона") },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Phone, contentDescription = "Phone Icon") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Phone,
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
                Spacer(modifier = Modifier.height(16.dp))
                var confirmPasswordVisibility: Boolean by remember { mutableStateOf(false) }
                TextField(
                    value = confirmPassword.value,
                    onValueChange = {
                        confirmPassword.value = it
                    },
                    singleLine = true,
                    label = { Text(text = "Подтвердить пароль", color = Color.White) },
                    placeholder = { Text(text = "Повторите свой пароль") },
                    visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Password Icon") },
                    trailingIcon = {
                        val image = if (confirmPasswordVisibility)
                            ImageVector.vectorResource(id = R.drawable.ic_visibility)
                        else ImageVector.vectorResource(id = R.drawable.ic_visibility_off)

                        val description = if (confirmPasswordVisibility) "Hide password" else "Show password"

                        IconButton(onClick = {confirmPasswordVisibility = !confirmPasswordVisibility}){
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
                        if (
                            firstName.value.isNotEmpty()
                            && lastName.value.isNotEmpty()
                            && email.value.isNotEmpty()
                            && phone.value.isNotEmpty()
                            && password.value.isNotEmpty()
                            && confirmPassword.value.isNotEmpty()
                        ) {
                            if (password.value == confirmPassword.value){
                                val user = User(
                                    firstName = firstName.value,
                                    lastName = lastName.value,
                                    email = email.value,
                                    phone = phone.value
                                )
                                viewModel.signUp(user = user, password = password.value)
                            } else {
                                Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(context, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
                        }

                    },
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Purple200, contentColor = Color.Black)
                ) {
                    Text(text = "Зарегистрироваться")
                }
                Spacer(modifier = Modifier.height(64.dp))
                TextButton(
                    onClick = {
                        navController.navigate(AuthScreens.SignIn.route)
                    }
                ) {
                    Text(text = "Уже есть аккаунт", color = Color.White)
                }
            }
        }

        if (stateSignUp == Resource.Loading)
            LoadingScreen()
    }
}