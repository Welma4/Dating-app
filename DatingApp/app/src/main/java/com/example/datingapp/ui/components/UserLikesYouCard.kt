package com.example.datingapp.ui.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.R
import com.example.datingapp.data.LikeEntity
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.theme.LikePink
import com.example.datingapp.ui.theme.LikePinkAlpha
import com.example.datingapp.ui.theme.MediumGray
import com.example.datingapp.ui.utils.calculateAge
import com.example.datingapp.viewmodel.ChatViewModel
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.MatchViewModel

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
