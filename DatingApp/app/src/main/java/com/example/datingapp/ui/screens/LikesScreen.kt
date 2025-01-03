package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.LikeEntity
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.CustomButton
import com.example.datingapp.ui.components.MatchDialog
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.ui.theme.LikePink
import com.example.datingapp.ui.theme.LikePinkAlpha
import com.example.datingapp.ui.theme.MediumGray
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.ui.utils.calculateAge
import com.example.datingapp.viewmodel.ChatViewModel
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.MatchViewModel
import com.example.datingapp.viewmodel.PhotoViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LikeScreen(
    navController: NavController,
    currentUserId: String,
    profileViewModel: ProfileViewModel,
    likeViewModel: LikeViewModel,
    photoViewModel: PhotoViewModel,
    matchViewModel: MatchViewModel,
    chatViewModel: ChatViewModel
) {
    val likedUsers = remember { mutableStateOf<List<Pair<UserEntity, Bitmap?>>>(emptyList()) }
    val usersWhoLiked = remember { mutableStateOf<List<Pair<UserEntity, Bitmap?>>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val isShowingLikedUsers = remember { mutableStateOf(true) }

    val showMatchDialog = remember { mutableStateOf(false) }
    val matchedUserId = remember { mutableStateOf("") }

    fun loadLikedUsers() {
        isLoading.value = true
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

    fun loadUsersWhoLiked() {
        isLoading.value = true
        likeViewModel.fetchUsersWhoLikedCurrent(
            currentUserId,
            onSuccess = { userWhoLikedIds ->
                val userPhotos = mutableListOf<Pair<UserEntity, Bitmap?>>()
                val remainingIds = userWhoLikedIds.toMutableList()
                userWhoLikedIds.forEach { userId ->
                    profileViewModel.fetchUserFromFirestore(
                        uid = userId,
                        onSuccess = { user ->
                            photoViewModel.getPhotoBitmap(
                                idUser = userId,
                                onSuccess = { bitmap ->
                                    userPhotos.add(user to bitmap)
                                    remainingIds.remove(userId)
                                    if (remainingIds.isEmpty()) {
                                        usersWhoLiked.value = userPhotos
                                        isLoading.value = false
                                    }
                                },
                                onFailure = {
                                    userPhotos.add(user to null)
                                    remainingIds.remove(userId)
                                    if (remainingIds.isEmpty()) {
                                        usersWhoLiked.value = userPhotos
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

    LaunchedEffect(Unit) {
        loadLikedUsers()
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
                text = if (isShowingLikedUsers.value) "SEE WHO LIKES YOU" else "SEE WHO YOU LIKE",
                onClick = {
                    if (isShowingLikedUsers.value) {
                        loadUsersWhoLiked()
                    } else {
                        loadLikedUsers()
                    }
                    isShowingLikedUsers.value = !isShowingLikedUsers.value
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        color = MediumPink
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(bottom = 55.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (isShowingLikedUsers.value) {
                        itemsIndexed(likedUsers.value) { _, (user, photo) ->
                            UserYouLikeCard(user = user, photo = photo)
                        }
                    } else {
                        itemsIndexed(usersWhoLiked.value) { _, (user, photo) ->
                            UserLikesYouCard(
                                user = user,
                                photo = photo,
                                currentUserId = currentUserId,
                                likeViewModel = likeViewModel,
                                matchViewModel = matchViewModel,
                                chatViewModel = chatViewModel,
                                onMatch = { id ->
                                    matchedUserId.value = id
                                    showMatchDialog.value = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    if (showMatchDialog.value) {
        MatchDialog(
            navController = navController,
            currentUserId = currentUserId,
            likedUserId = matchedUserId.value,
            onDismiss = { showMatchDialog.value = false },
            photoViewModel = photoViewModel,
            profileViewModel = profileViewModel
        )
    }
}


@Composable
fun UserYouLikeCard(user: UserEntity, photo: Bitmap?) {
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

@Composable
fun UserLikesYouCard(
    user: UserEntity,
    photo: Bitmap?,
    currentUserId: String,
    likeViewModel: LikeViewModel,
    matchViewModel: MatchViewModel,
    chatViewModel: ChatViewModel,
    onMatch: (String) -> Unit
) {

    var showLikeIcon by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable {
                if (!showLikeIcon) {
                    showLikeIcon = true
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (photo != null) {
                Image(
                    bitmap = photo.asImageBitmap(),
                    contentDescription = "${user.firstName}'s photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            if (isLiked) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LikePinkAlpha)
                ) {}
            }

            if (showLikeIcon) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (!isLiked) Color.Black.copy(alpha = 0.5f) else LikePinkAlpha),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_heart),
                        contentDescription = "like_icon",
                        modifier = Modifier
                            .size(65.dp)
                            .clickable{
                                showLikeIcon = false
                                isLiked = true
                                likeViewModel.saveLikeToFirestore(
                                    LikeEntity(currentUserId, user.uid),
                                    onSuccess = {
                                        matchViewModel.checkForMatchAndCreate(
                                            currentUserId = currentUserId,
                                            likedUserId = user.uid,
                                            onMatchFound = {
                                                onMatch("${user.uid}")
                                                chatViewModel.createChat(
                                                    currentUserId,
                                                    user.uid,
                                                    onSuccess = {
                                                        Log.d("MyTag", "Chat created!")
                                                    },
                                                    onFailure = { error ->
                                                        Log.d("MyTag", error)
                                                    }
                                                )
                                            },
                                            onSuccess = {
                                                Log.d("MyTag", "Like and match processed")
                                            },
                                            onFailure = { error ->
                                                Log.e("MyTag", "Match creation failed: $error")
                                            }
                                        )
                                    },
                                    onFailure = { error ->
                                        Log.d("MyTag", "Like save error: $error")
                                    }
                                )
                            },
                        tint = LikePink
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = "cancel_icon",
                        modifier = Modifier
                            .size(65.dp)
                            .clickable{
                                showLikeIcon = false;
                            },
                        tint = Color.Red
                    )
                }
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
