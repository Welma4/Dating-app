package com.example.datingapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.datingapp.ui.theme.MediumPink

@Composable
fun GenderPreferenceMenu(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(40))
            .clip(RoundedCornerShape(40))
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(if (selectedGender == "Male") MediumPink else Color.Transparent)
                .clickable(onClick = { onGenderSelected("Male") })
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Male",
                color = if (selectedGender == "Male") Color.White else Color.Gray,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .background(if (selectedGender == "Female") MediumPink else Color.Transparent)
                .clickable(onClick = { onGenderSelected("Female") })
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Female",
                color = if (selectedGender == "Female") Color.White else Color.Gray,
            )
        }
    }
}
