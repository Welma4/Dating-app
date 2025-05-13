package com.example.datingapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditableTextField(
    fieldValue: String,
    onValueChange: (String) -> Unit,
    fieldName: String,
) {
    OutlinedTextField(
        value = fieldValue,
        onValueChange = onValueChange,
        label = { Text(fieldName) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    )
}
