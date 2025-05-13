import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.example.datingapp.viewmodel.PhotoViewModel
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState

@Composable
fun VerticalSwipeFeed(
    items: List<UserEntity>,
    photoViewModel: PhotoViewModel,
    likeViewModel: LikeViewModel,
    matchViewModel: MatchViewModel,
    chatViewModel: ChatViewModel,
    currentUserId: String,
    onMatch: (String) -> Unit,
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
                    Log.e("MyTag", "Error loading photo for user ${user.uid}: $error")
                },
            )
        }
    }

    VerticalPager(
        count = Int.MAX_VALUE,
        state = pagerState,
        modifier =
        Modifier
            .fillMaxHeight(0.92f)
            .padding(horizontal = 16.dp),
        itemSpacing = 30.dp,
    ) { virtualPage ->
        val actualPage = virtualPage % itemCount
        val user = items[actualPage]
        val userPhoto = photoBitmaps.value[user.uid]

        var dragOffset by remember { mutableStateOf(0f) }
        var hasLiked by remember { mutableStateOf(false) }

        val filterAlpha by animateFloatAsState(
            targetValue = if (hasLiked) 0.3f else (dragOffset / 300f).coerceIn(0f, 0.3f),
            animationSpec = tween(durationMillis = 100),
        )
        val showHeartIcon = filterAlpha >= 0.3f

        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            if (!hasLiked) {
                                dragOffset = (dragOffset + dragAmount).coerceAtLeast(0f)
                                if (dragOffset >= 300f) {
                                    hasLiked = true
                                    dragOffset = 300f

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
                                                        },
                                                    )
                                                },
                                                onSuccess = {
                                                    Log.d("MyTag", "Like and match processed")
                                                },
                                                onFailure = { error ->
                                                    Log.e("MyTag", "Match creation failed: $error")
                                                },
                                            )
                                        },
                                        onFailure = { error ->
                                            Log.d("MyTag", "Like save error: $error")
                                        },
                                    )
                                }
                            }
                        },
                        onDragEnd = {
                            if (!hasLiked) {
                                dragOffset = 0f
                            }
                        },
                    )
                },
        ) {
            if (userPhoto != null) {
                Image(
                    bitmap = userPhoto.asImageBitmap(),
                    contentDescription = "user_photo",
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(30.dp),
                            spotColor = Color.Black,
                            ambientColor = Color.Black,
                        ).clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_icon),
                    contentDescription = "default_photo",
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(30.dp),
                            spotColor = Color.Black,
                            ambientColor = Color.Black,
                        ).clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

            if (filterAlpha > 0f) {
                Box(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                        .background(LikePinkAlpha)
                        .alpha(filterAlpha),
                )
            }

            if (showHeartIcon) {
                Box(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.Center),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_heart),
                        contentDescription = "Heart Icon",
                        modifier =
                        Modifier
                            .size(100.dp)
                            .alpha(1f),
                        tint = LikePink,
                    )
                }
            }

            Column(
                modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 15.dp),
            ) {
                Text(
                    text = "${user.firstName}  ${user.secondName}, ${calculateAge(user.birthDate!!)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = TextStyle(shadow = Shadow(MediumGray, Offset(5.0f, 2.0f), 1.0f)),
                )
                Text(
                    text = user.location,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    style = TextStyle(shadow = Shadow(MediumGray, Offset(5.0f, 2.0f), 1.0f)),
                )
            }
        }
    }
}
