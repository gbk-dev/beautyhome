package com.example.beautyhome.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.beautyhome.presentation.navigation.Screens
import com.example.beautyhome.presentation.viewmodel.RecordViewModel
import com.example.beautyhome.ui.theme.DefBlack
import com.example.beautyhome.ui.theme.Purple200
import com.example.beautyhome.ui.theme.Purple500

@Composable
fun ListOfRecordsScreen(
    navController: NavController,
    viewModel: RecordViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DefBlack)
    ) {
        
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.navigate(Screens.AdminMain.route)
                }
            ) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null, tint = Purple200)
            }

            Text(text = "Список активных записей", color = Purple200)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
        ){
            viewModel.getAllRecord()
            val recordsList = viewModel.allRecordList.value.orEmpty()
            items(recordsList){ records ->
                val data = records.date
                val userName = records.userName
                val time = records.time
                val service = records.service
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = Color.Transparent),
                    border = BorderStroke(1.dp, Purple200),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "Пользователь - $userName", modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp, end = 16.dp))
                        Text(text = "Услуга - $service", modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp))
                        Text(text = "Дата - $data", modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp))
                        Text(text = "Время - $time", modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp))
                    }
                }
            }
        }
    }
}