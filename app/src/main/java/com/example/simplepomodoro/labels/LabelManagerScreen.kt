package com.example.simplepomodoro.labels

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun LabelManagerScreen(
    viewModel: LabelManagerViewModel,
    navController: NavController
){
    Text(text = "label manager")
}