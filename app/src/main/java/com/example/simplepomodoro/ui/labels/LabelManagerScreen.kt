package com.example.simplepomodoro.ui.labels

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
    var showAddLabelAction by remember { mutableStateOf(false) }

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
            LabelsList(
                labels = labels,
                onLabelDeleteClick = { labelNameToDelete ->
                    viewModel.deleteLabelByName(labelNameToDelete)
                },
                onLabelSaveClick = { oldName, newName ->
                    viewModel.updateLabelName(oldName = oldName, newName = newName)
                }
            )
            AddLabelTextButton(
                onClick = {
                    showAddLabelAction = true
                },
                onSaveLabel = { labelName ->
                    val labelTrimmed = labelName.trim()
                    if (labelTrimmed.isNotEmpty()) {
                        // save to DB
                        viewModel.insertLabel(LabelEntity(name = labelTrimmed))
                    }

                    showAddLabelAction = false
                },
                showAddLabelAction = showAddLabelAction
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LabelsList(
    labels: List<LabelEntity>,
    onLabelDeleteClick: (String) -> Unit,
    onLabelSaveClick: (oldName: String, newName: String) -> Unit
) {
    var labelInEditMode by remember { mutableStateOf("") }
    //val listState = rememberLazyListState()

    LazyColumn() {
        items(labels) { label ->
            LabelItem(
                modifier = Modifier.animateItemPlacement(),
                label = label,
                editMode = label.name == labelInEditMode,
                onLabelClick = {
                    // toggle edit mode for this label
                    labelInEditMode = label.name
                },
                onLabelSaveClick = { oldName: String, newName: String ->
                    labelInEditMode = ""
                    onLabelSaveClick(oldName, newName)
                },
                onLabelDeleteClick = {
                    onLabelDeleteClick(it)
                }
            )
            Divider()
        }
    }
}

@Composable
fun AddLabelTextButton(
    onClick: () -> Unit,
    onSaveLabel: (String) -> Unit,
    showAddLabelAction: Boolean
) {
    Crossfade(targetState = showAddLabelAction) { showAddLabel ->
        if (showAddLabel) {
            LabelItem(
                label = LabelEntity(name = ""),
                editMode = true,
                // on label click is not necessary here, since we are always in edit mode
                onLabelClick = {},
                onLabelSaveClick = { oldName: String, newName: String ->
                    onSaveLabel(newName)
                },
                // we can't delete a label which was not created yet
                onLabelDeleteClick = {}
            )
        } else {
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
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LabelItem(
    modifier: Modifier = Modifier,
    label: LabelEntity,
    editMode: Boolean,
    onLabelClick: () -> Unit,
    onLabelSaveClick: (oldName: String, newName: String) -> Unit,
    onLabelDeleteClick: (String) -> Unit
) {
    var tmpLabelName by rememberSaveable {
        mutableStateOf(label.name)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(enabled = !editMode) {
                onLabelClick()
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box {
            BasicTextField(
                value = tmpLabelName,
                onValueChange = {
                    tmpLabelName = it
                },
                enabled = editMode,
            )
            // show hint if text is empty
            if (tmpLabelName.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.enter_label_name)
                )
            }
        }

        AnimatedVisibility(
            visible = editMode,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth ->
                    fullWidth / 2
                }
            ),
            exit = fadeOut()
        ) {
            Row {
                IconButton(onClick = {
                    onLabelSaveClick(
                        label.name, tmpLabelName
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(id = R.string.save_label_changes)
                    )
                }

                IconButton(onClick = {
                    onLabelDeleteClick(label.name)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.delete_label)
                    )
                }
            }
        }
    }
}