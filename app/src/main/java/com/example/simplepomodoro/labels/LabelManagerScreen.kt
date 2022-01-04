package com.example.simplepomodoro.labels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simplepomodoro.R
import com.example.simplepomodoro.data.entities.LabelEntity

@Composable
fun LabelManagerScreen(
    viewModel: LabelManagerViewModel,
    navController: NavController
) {
    val labels by viewModel.allLabelsStateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.edit_labels))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }

                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LabelsList(labels = labels)
            AddLabelTextButton {
                // todo on click
            }
        }
    }
}

@Composable
fun LabelsList(labels: List<LabelEntity>) {
    LazyColumn() {
        items(labels) { label ->
            LabelItem(label = label)
            Divider()
        }
    }
}

@Composable
fun AddLabelTextButton(onClick: () -> Unit) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.scale(1.5f),
            )
            Text(
                text = "Add Label",
                style = MaterialTheme.typography.button
            )
        }
    }
}

@Composable
fun LabelItem(label: LabelEntity) {
    var tmpLabelName by rememberSaveable {
        mutableStateOf(label.name)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextField(value = tmpLabelName, onValueChange = {
            tmpLabelName = it
        })
    }
}