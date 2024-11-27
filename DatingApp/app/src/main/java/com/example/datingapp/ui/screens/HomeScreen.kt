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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.R
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.FiltersDialog
import com.example.datingapp.ui.theme.MediumGray
import com.example.datingapp.ui.utils.calculateAge
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.PreferencesViewModel
import com.google.accompanist.pager.*
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    preferencesViewModel: PreferencesViewModel,
    genderViewModel: GenderViewModel,
    currentUserId: String
) {
    val usersList = remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    val isFilterMenuVisible = remember { mutableStateOf(false) }

    var genderIdPreference by remember { mutableStateOf("") }
    var ageRangePreference by remember { mutableStateOf(0 to 100) }


    LaunchedEffect(Unit, genderIdPreference, ageRangePreference) {
        preferencesViewModel.fetchPreferencesForUser(
            idUser = currentUserId,
            onSuccess = { preferencesEntity ->
                genderIdPreference = preferencesEntity.gender
                ageRangePreference = preferencesEntity.startAgeRange to preferencesEntity.endAgeRange

                profileViewModel.fetchFilteredUsers(
                    currentUserUid = currentUserId,
                    startAgeRange = ageRangePreference.first,
                    endAgeRange = ageRangePreference.second,
                    gender = genderIdPreference,
                    onSuccess = { fetchedUsers ->
                        usersList.value = fetchedUsers
                    },
                    onFailure = { error ->
                        Log.e("HomeScreen", "Error loading users: $error")
                    }
                )
            },
            onFailure = { error ->
                Log.e("HomeScreen", "Error loading preferences: $error")
            }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { NavigationMenu(navController) },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderSection(isFilterMenuVisible)

                if (usersList.value.isNotEmpty()) {
                    VerticalSwipeFeed(
                        items = usersList.value.map { it },
                        photoViewModel = photoViewModel,
                    )
                } else {
                    LoadingState()
                }
            }
        }

        FiltersDialog(
            preferencesViewModel = preferencesViewModel,
            genderViewModel = genderViewModel,
            currentUserId = currentUserId,
            isDialog = isFilterMenuVisible.value,
            onDialogChange = { isFilterMenuVisible.value = it },
            onGenderChange = { newGenderId ->
                genderIdPreference = newGenderId
                Log.d("MyTag", "Updated Gender: $newGenderId")
            },
            onAgeRangeChange = { newAgeRange ->
                ageRangePreference = newAgeRange
                Log.d("MyTag", "Updated Age Range: ${newAgeRange.first}, ${newAgeRange.second}")
            }
        )

    }
}

@Composable
fun HeaderSection(isFilterMenuVisible: MutableState<Boolean>) {
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

@Composable
fun VerticalSwipeFeed(
    items: List<UserEntity>,
    photoViewModel: PhotoViewModel,
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

    VerticalPager(
        count = Int.MAX_VALUE,
        state = pagerState,
        modifier = Modifier
            .fillMaxHeight(0.92f)
            .padding(horizontal = 16.dp),
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
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black, ambientColor = Color.Black)
                        .clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_icon),
                    contentDescription = "default_photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(30.dp), spotColor = Color.Black, ambientColor = Color.Black)
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
                    style = TextStyle(shadow = Shadow(MediumGray, Offset(5.0f, 2.0f), 1.0f))
                )
                Text(
                    text = items[actualPage].location,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    style = TextStyle(shadow = Shadow(MediumGray, Offset(5.0f, 2.0f), 1.0f))
                )
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Loading users...", fontSize = 40.sp)
    }
}
