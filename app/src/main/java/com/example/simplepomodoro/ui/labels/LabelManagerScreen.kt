package com.example.simplepomodoro.ui.labels

import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
    ) { scaffoldPaddings ->
        Surface {
            LabelsList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddings),
                labels = labels,
                onLabelDelete = { labelNameToDelete ->
                    viewModel.deleteLabelByName(labelNameToDelete)
                },
                onLabelUpdate = { oldName, newName ->
                    viewModel.updateLabelName(oldName = oldName, newName = newName)
                },
                onLabelInsert = {
                    viewModel.insertLabel(it)
                }
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LabelsList(
    modifier: Modifier = Modifier,
    labels: List<LabelEntity>,
    onLabelDelete: (String) -> Unit,
    onLabelUpdate: (oldName: String, newName: String) -> Unit,
    onLabelInsert: (labelToInsert: LabelEntity) -> Unit
) {
    var labelInEditMode by remember { mutableStateOf("") }
    var showAddLabelAction by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Column(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.animateContentSize(animationSpec = TweenSpec())
        ) {
            items(labels, key = { it.name }) { label ->
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
                        onLabelUpdate(oldName, newName)
                    },
                    onLabelDeleteClick = {
                        onLabelDelete(it)
                    },
                )
            }
        }

        AddLabelTextButton(
            modifier = Modifier,
            onClick = {
                labelInEditMode = ""
                showAddLabelAction = true
            },
            onSaveLabel = { labelName ->
                val labelTrimmed = labelName.trim()
                if (labelTrimmed.isNotEmpty()) {
                    // save to DB
                    onLabelInsert(LabelEntity(name = labelTrimmed))
                }

                showAddLabelAction = false
            },
            showAddLabelAction = showAddLabelAction && labelInEditMode == ""
        )
    }
}

@Composable
fun AddLabelTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onSaveLabel: (String) -> Unit,
    showAddLabelAction: Boolean
) {
    Crossfade(targetState = showAddLabelAction, label = "addLabelCrossfade") { showAddLabel ->
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
                modifier = modifier
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
                    )
                }
            }
        }
    }

}

@Composable
fun LabelItem(
    modifier: Modifier = Modifier,
    label: LabelEntity,
    editMode: Boolean,
    onLabelClick: () -> Unit,
    onLabelSaveClick: (oldName: String, newName: String) -> Unit,
    onLabelDeleteClick: (String) -> Unit,
) {
    var tmpLabelName by rememberSaveable {
        mutableStateOf(label.name)
    }

    val localStyle = LocalTextStyle.current
    val mergedStyle = localStyle.merge(TextStyle(color = LocalContentColor.current))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(enabled = true) {
                onLabelClick()
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize(),
                value = tmpLabelName,
                onValueChange = {
                    tmpLabelName = it
                },
                enabled = editMode,
                singleLine = true,
                textStyle = mergedStyle,
                cursorBrush = SolidColor(mergedStyle.color),
                keyboardActions = KeyboardActions(onDone = {
                    onLabelSaveClick(
                        label.name, tmpLabelName
                    )
                }),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (tmpLabelName.isEmpty()) {
                            // show hint if text is empty
                            Text(
                                text = stringResource(id = R.string.enter_label_name),
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )
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