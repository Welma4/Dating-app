package com.example.datingapp.ui.screens

import AgeRangeSelector
import ProfileViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.datingapp.ui.components.NavigationMenu
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.R
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.GenderPreferenceMenu
import com.example.datingapp.ui.theme.MediumPink
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
    val isFilterMenuVisible = remember { mutableStateOf(false) }

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

    Box(modifier = Modifier.fillMaxSize())
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) }
    ) {
        if (usersList.value.isNotEmpty()) {
            VerticalSwipeFeed(
                items = usersList.value.map { it },
                photoViewModel,
                isFilterMenuVisible
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
        FiltresDialog(isFilterMenuVisible.value) { isFilterMenuVisible.value = it }
    }
}


@Composable
fun VerticalSwipeFeed(
    items: List<UserEntity>,
    photoViewModel: PhotoViewModel,
    isFilterMenuVisible: MutableState<Boolean>
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(0.25f))

            Image(
                painter = painterResource(id = R.drawable.home_app_logo),
                contentDescription = "home_app_logo",
                modifier = Modifier
                    .padding(vertical = 40.dp)
                    .weight(2f)
                    .height(36.dp)
            )
            IconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = { isFilterMenuVisible.value = true }
            ) {
                Icon(
                    modifier = Modifier
                        .size(30.dp),
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "filter"
                )
            }
        }

        VerticalPager(
            count = Int.MAX_VALUE,
            state = pagerState,
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .shadow(
                    elevation = 8.dp,
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

@Composable()
fun FiltresDialog(isDialog: Boolean, onDialogChange: (Boolean) -> Unit) {
    var selectedGender = remember { mutableStateOf("Male") }
    var ageRange by remember { mutableStateOf(0 to 100) }

    if (isDialog) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .background(Color.White, shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 35.dp, bottom = 25.dp)
                    ) {
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = { onDialogChange(false) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "close",
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.8f))
                        Text(
                            text = "Filter",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = { onDialogChange(false) }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_save_filters),
                                tint = MediumPink,
                                contentDescription = "save",
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                    ) {
                        Text(
                            text = "Gender",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        GenderPreferenceMenu(selectedGender = selectedGender.value, onGenderSelected = { selectedGender.value = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Age",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AgeRangeSelector() { startAge, endAge ->
                            ageRange = startAge to endAge
                        }
                    }
                }
            }
        }
    }
}


