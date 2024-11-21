package com.example.datingapp.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.R
import com.google.accompanist.pager.*


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) }
    ) {
        PreviewVerticalSwipeFeed()
    }
}


@Composable
fun VerticalSwipeFeed(items: List<String>) {
    val pagerState = rememberPagerState()
    val itemCount = items.size

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
            itemSpacing = 30.dp
        ) { virtualPage ->
            val actualPage = virtualPage % itemCount
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.default_user_photo),
                    contentDescription = "background_image",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = items[actualPage],
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewVerticalSwipeFeed() {
    val testItems = listOf(
        "Victor, 21",
        "Peter, 25",
        "Semen, 27",
        "Valeriy, 29",
        "Gleb, 31",
    )
    VerticalSwipeFeed(items = testItems)
}

