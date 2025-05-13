package com.example.datingapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.datingapp.data.GenderEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderMenu(
    options: List<GenderEntity>,
    selectedGender: String,
    onValueChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var currentGender by remember { mutableStateOf(selectedGender) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = currentGender,
            onValueChange = {},
            label = { Text(text = "Gender") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                )
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(text = gender.genderName) },
                    onClick = {
                        currentGender = gender.genderName
                        expanded = false
                        onValueChange(gender.genderName)
                    },
                )
            }
        }
    }
}
