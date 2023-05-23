package com.wim.gmapsapp_android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wim.gmapsapp_android.model.Item

@Composable
fun AutoCompleteTextField(
    modifier: Modifier,
    suggestions: List<Item>,
    onSearch: (String) -> Unit,
    onItemSelected: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                onSearch(text)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            placeholder = { Text("Search...") },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                cursorColor = Color.Black,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .onFocusChanged { focusState ->
                    isTextFieldFocused = focusState.isFocused
                }
        )

        if (isTextFieldFocused && text.length > 5) {
            val suggestionsList = suggestions.toList() // Convert MutableStateList to List

            if (suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 250.dp)
                ) {
                    items(suggestionsList) { item ->
                        Text(
                            text = item.address.label,
                            modifier = Modifier
                                .clickable {
                                    text = item.address.label
                                    isTextFieldFocused = false
                                    onItemSelected(item.address.label)
                                }
                                .padding(16.dp)
                        )
                    }
                }
            } else {
                CircularProgressIndicator(modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally))
            }
        }
    }
}