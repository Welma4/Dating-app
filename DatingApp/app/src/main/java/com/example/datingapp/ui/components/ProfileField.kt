package com.example.datingapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.ui.theme.poppinsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFontFamily,
            ),
            label = {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = poppinsFontFamily,
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedLabelColor = Color.Gray,
                focusedLabelColor = Color.Gray,
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray,
            ),
        )
    }
}
