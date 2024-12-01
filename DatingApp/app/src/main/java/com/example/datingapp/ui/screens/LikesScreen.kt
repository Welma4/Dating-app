package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.CustomButton
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.ui.theme.MediumGray
import com.example.datingapp.ui.utils.calculateAge
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.PhotoViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LikeScreen(
    navController: NavController,
    currentUserId: String,
    profileViewModel: ProfileViewModel,
    likeViewModel: LikeViewModel,
    photoViewModel: PhotoViewModel
) {
    val likedUsers = remember { mutableStateOf<List<Pair<UserEntity, Bitmap?>>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        likeViewModel.fetchLikedUsers(
            currentUserId,
            onSuccess = { likedUserIds ->
                val userPhotos = mutableListOf<Pair<UserEntity, Bitmap?>>()
                val remainingIds = likedUserIds.toMutableList()
                likedUserIds.forEach { userId ->
                    profileViewModel.fetchUserFromFirestore(
                        uid = userId,
                        onSuccess = { user ->
                            photoViewModel.getPhotoBitmap(
                                idUser = userId,
                                onSuccess = { bitmap ->
                                    userPhotos.add(user to bitmap)
                                    remainingIds.remove(userId)
                                    if (remainingIds.isEmpty()) {
                                        likedUsers.value = userPhotos
                                        isLoading.value = false
                                    }
                                },
                                onFailure = {
                                    userPhotos.add(user to null)
                                    remainingIds.remove(userId)
                                    if (remainingIds.isEmpty()) {
                                        likedUsers.value = userPhotos
                                        isLoading.value = false
                                    }
                                }
                            )
                        },
                        onFailure = {
                            remainingIds.remove(userId)
                            if (remainingIds.isEmpty()) {
                                isLoading.value = false
                            }
                        }
                    )
                }
            },
            onFailure = {
                isLoading.value = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back Arrow",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Likes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            CustomButton(
                text = "SEE WHO LIKES YOU",
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(bottom = 55.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    itemsIndexed(likedUsers.value) { _, (user, photo) ->
                        UserCard(user = user, photo = photo)
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: UserEntity, photo: Bitmap?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (photo != null) {
                Image(
                    bitmap = photo.asImageBitmap(),
                    contentDescription = "${user.firstName}'s photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "${user.firstName} ${user.secondName}, ${calculateAge(user.birthDate!!)}",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    style = TextStyle(shadow = Shadow(MediumGray, Offset(5.0f, 2.0f), 1.0f))
                )
                Text(
                    text = user.location,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    style = TextStyle(shadow = Shadow(MediumGray, Offset(5.0f, 2.0f), 1.0f))
                )
            }
        }
    }
}
