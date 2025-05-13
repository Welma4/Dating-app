package com.example.datingapp.ui.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.theme.LikePink
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.ProfileViewModel

@Composable
fun MatchDialog(
    navController: NavController,
    currentUserId: String,
    likedUserId: String,
    onDismiss: () -> Unit,
    photoViewModel: PhotoViewModel,
    profileViewModel: ProfileViewModel,
) {
    val currentUserPhoto = remember { mutableStateOf<Bitmap?>(null) }
    val likedUserPhoto = remember { mutableStateOf<Bitmap?>(null) }
    val likedUser = remember { mutableStateOf(UserEntity()) }

    LaunchedEffect(currentUserId, likedUserId) {
        photoViewModel.getPhotoBitmap(
            idUser = currentUserId,
            onSuccess = { bitmap ->
                currentUserPhoto.value = bitmap
            },
            onFailure = { error ->
                Log.d("MyTag", "Error loading current user photo: $error")
            },
        )

        photoViewModel.getPhotoBitmap(
            idUser = likedUserId,
            onSuccess = { bitmap ->
                likedUserPhoto.value = bitmap
            },
            onFailure = { error ->
                Log.d("MyTag", "Error loading liked user photo: $error")
            },
        )

        profileViewModel.fetchUserFromFirestore(
            likedUserId,
            onSuccess = { user ->
                likedUser.value = user
            },
            onFailure = { error ->
                Log.d("MyTag", error)
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MediumPink),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.match_text),
                    contentDescription = "match",
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(0.8f),
                )
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = "${likedUser.value.firstName} likes you too!",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 10.dp),
                )
                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    currentUserPhoto.value?.let { bitmap ->
                        Image(
                            bitmap = Bitmap.createScaledBitmap(bitmap, 110, 110, false).asImageBitmap(),
                            contentDescription = "currentUserPhoto",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                        )
                    }
                    likedUserPhoto.value?.let { bitmap ->
                        Image(
                            bitmap = Bitmap.createScaledBitmap(bitmap, 110, 110, false).asImageBitmap(),
                            contentDescription = "likedUserPhoto",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { navController.navigate("Chat/$likedUserId") },
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(0.9f),
                        colors = ButtonDefaults.buttonColors(containerColor = LikePink, contentColor = Color.White),
                    ) {
                        Text(
                            text = "SEND A MESSAGE",
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(0.9f)
                            .border(
                                width = 5.dp,
                                color = LikePink,
                                shape = RoundedCornerShape(28.dp),
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    ) {
                        Text(
                            text = "KEEP SWIPING",
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    }
}
