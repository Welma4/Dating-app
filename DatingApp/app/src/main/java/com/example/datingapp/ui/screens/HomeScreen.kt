package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.datingapp.ui.components.NavigationMenu
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.R
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.utils.calculateAge
import com.example.datingapp.viewmodel.PhotoViewModel
import com.google.accompanist.pager.*
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel
) {

    val usersList = remember { mutableStateOf<List<UserEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        profileViewModel.fetchUsersExceptCurrent(
            currentUserUid = currentUserUid,
            onSuccess = { fetchedUsers ->
                usersList.value = fetchedUsers
            },
            onFailure = { error ->
                Log.e("HomeScreen", "Error loading users: $error")
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) }
    ) {
        if (usersList.value.isNotEmpty()) {
            VerticalSwipeFeed(
                items = usersList.value.map { it },
                photoViewModel
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Loading users...", fontSize = 40.sp)
            }
        }
    }
}

val A: Int = 1;
val B: Int = 1;

@Composable
fun VerticalSwipeFeed(
    items: List<UserEntity>,
    photoViewModel: PhotoViewModel
) {
    val pagerState = rememberPagerState()
    val itemCount = items.size
    val photoBitmaps = remember { mutableStateOf<Map<String, Bitmap>>(emptyMap()) }

    LaunchedEffect(items) {
        items.forEach { user ->
            photoViewModel.getPhotoBitmap(
                idUser = user.uid,
                onSuccess = { bitmap ->
                    photoBitmaps.value = photoBitmaps.value + (user.uid to bitmap)
                },
                onFailure = { error ->
                    Log.e("VerticalSwipeFeed", "Error loading photo for user ${user.uid}: $error")
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.home_app_logo),
            contentDescription = "home_app_logo",
            modifier = Modifier

                .padding(vertical = 40.dp)
                .fillMaxWidth(0.4f)
                .height(36.dp)
        )


        VerticalPager(
            count = Int.MAX_VALUE,
            state = pagerState,
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(30.dp),
                ),
            itemSpacing = 30.dp,
        ) { virtualPage ->
            val actualPage = virtualPage % itemCount
            val user = items[actualPage]
            val userPhoto = photoBitmaps.value[user.uid]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                if (userPhoto != null) {
                    Image(
                        bitmap = userPhoto.asImageBitmap(),
                        contentDescription = "user_photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(30.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_icon),
                        contentDescription = "default_photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(30.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 15.dp)
                ) {
                    Text(
                        text = "${items[actualPage].firstName}  ${items[actualPage].secondName}, ${calculateAge(items[actualPage].birthDate!!)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = items[actualPage].location,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                    )
                }

            }
        }
    }
}

