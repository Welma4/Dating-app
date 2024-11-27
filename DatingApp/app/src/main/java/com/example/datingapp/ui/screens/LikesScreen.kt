package com.example.datingapp.ui.screens

import CustomButton
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.ui.components.NavigationMenu

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LikeScreen(navController: NavController) {
    val photos = listOf(
        R.drawable.default_user_photo,
        R.drawable.default_user_photo2,
        R.drawable.default_user_photo,
        R.drawable.default_user_photo2,
        R.drawable.default_user_photo,
        R.drawable.default_user_photo2,
        R.drawable.default_user_photo,
    )

    val names = listOf(
        "Grigoriy Petrov, 31\nSt. Petersburg",
        "Anna Ivanova, 25\nMoscow",
        "Grigoriy Petrov, 31\nSt. Petersburg",
        "Anna Ivanova, 25\nMoscow",
        "Sergey Kuznetsov, 28\nNovosibirsk",
        "Elena Smirnova, 22\nKazan",
        "Viktor Sokolov, 35\nYekaterinburg",
        "Olga Fedorova, 27\nSochi"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Кнопка назад и заголовок
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Back Arrow",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Лайки",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            CustomButton(
                text = "SEE WHO LIKES YOU",
                onClick = { /* Добавьте обработчик для кнопки */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "${photos.size} Likes",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Сетка фотографий с поддержкой прокрутки
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Две колонки
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize().padding(bottom = 55.dp)
            ) {
                itemsIndexed(photos) { index, photoResId ->
                    UserCard(photoResId = photoResId, userInfo = names[index % names.size])
                }
            }
        }
    }
}

@Composable
fun UserCard(photoResId: Int, userInfo: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Фотография
            Image(
                painter = painterResource(id = photoResId),
                contentDescription = "User Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            // Имя, возраст и город
            Text(
                text = userInfo,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

