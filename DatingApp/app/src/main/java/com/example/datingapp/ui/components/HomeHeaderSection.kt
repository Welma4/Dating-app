package com.example.datingapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.datingapp.R

@Composable
fun HomeHeaderSection(isFilterMenuVisible: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 35.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(0.25f))

        Image(
            painter = painterResource(id = R.drawable.home_app_logo),
            contentDescription = "home_app_logo",
            modifier = Modifier
                .weight(2f)
                .height(36.dp)
        )

        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = { isFilterMenuVisible.value = true }
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "filter"
            )
        }
    }
}
