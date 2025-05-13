package com.example.datingapp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.CustomButton
import com.example.datingapp.ui.components.MatchDialog
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.ui.components.UserLikesYouCard
import com.example.datingapp.ui.components.UserYouLikeCard
import com.example.datingapp.ui.theme.MediumGray
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.viewmodel.ChatViewModel
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.MatchViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.ProfileViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LikeScreen(
    navController: NavController,
    currentUserId: String,
    profileViewModel: ProfileViewModel,
    likeViewModel: LikeViewModel,
    photoViewModel: PhotoViewModel,
    matchViewModel: MatchViewModel,
    chatViewModel: ChatViewModel,
) {
    val likedUsers = remember { mutableStateOf<List<Pair<UserEntity, Bitmap?>>>(emptyList()) }
    val usersWhoLiked = remember { mutableStateOf<List<Pair<UserEntity, Bitmap?>>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val isShowingLikedUsers = remember { mutableStateOf(true) }

    val showMatchDialog = remember { mutableStateOf(false) }
    val matchedUserId = remember { mutableStateOf("") }

    val likedUsersMessage = remember { mutableStateOf("") }
    val usersWhoLikedMessage = remember { mutableStateOf("") }

    fun loadLikedUsers() {
        isLoading.value = true
        likedUsersMessage.value = ""
        likeViewModel.fetchLikedUsers(
            currentUserId,
            onSuccess = { likedUserIds ->
                if (likedUserIds.isEmpty()) {
                    likedUsersMessage.value = "You haven't liked anyone yet."
                    isLoading.value = false
                    return@fetchLikedUsers
                }
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
                                    }
                                    isLoading.value = false
                                },
                                onFailure = {
                                    userPhotos.add(user to null)
                                    remainingIds.remove(userId)
                                    if (remainingIds.isEmpty()) {
                                        likedUsers.value = userPhotos
                                    }
                                    isLoading.value = false
                                },
                            )
                        },
                        onFailure = {
                            remainingIds.remove(userId)
                            if (remainingIds.isEmpty()) {
                                isLoading.value = false
                            }
                        },
                    )
                }
            },
            onFailure = {
                isLoading.value = false
                likedUsersMessage.value = "Failed to load liked users."
            },
        )
    }

    fun loadUsersWhoLiked() {
        isLoading.value = true
        usersWhoLikedMessage.value = ""
        likeViewModel.fetchUsersWhoLikedCurrent(
            currentUserId,
            onSuccess = { userWhoLikedIds ->
                if (userWhoLikedIds.isEmpty()) {
                    usersWhoLikedMessage.value = "No one has liked you yet."
                    isLoading.value = false
                    return@fetchUsersWhoLikedCurrent
                }
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
                                },
                            )
                        },
                        onFailure = {
                            remainingIds.remove(userId)
                            if (remainingIds.isEmpty()) {
                                isLoading.value = false
                            }
                        },
                    )
                }
            },
            onFailure = {
                isLoading.value = false
                usersWhoLikedMessage.value = "Failed to load users who liked you."
            },
        )
    }

    LaunchedEffect(Unit) {
        loadLikedUsers()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back Arrow",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp),
                    )
                }

                Text(
                    text = "Likes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
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
                    .padding(bottom = 16.dp),
            )

            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center),
                        color = MediumPink,
                    )
                }
            } else if (isShowingLikedUsers.value) {
                if (likedUsers.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = likedUsersMessage.value,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = MediumGray,
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(bottom = 55.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        itemsIndexed(likedUsers.value) { _, (user, photo) ->
                            UserYouLikeCard(user = user, photo = photo)
                        }
                    }
                }
            } else {
                if (usersWhoLiked.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = usersWhoLikedMessage.value,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = MediumGray,
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(bottom = 55.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
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
                                },
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
            profileViewModel = profileViewModel,
        )
    }
}
