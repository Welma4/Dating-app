package com.example.datingapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.Routes
import com.example.datingapp.ui.components.GradientBackground
import com.example.datingapp.ui.theme.poppinsFontFamily

@Composable
fun LoginScreen(
    navController: NavController,
) {
    MaterialTheme {
        GradientBackground()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "login_logo",
            modifier = Modifier
                .padding(top = 80.dp)
                .size(250.dp),
        )
        Spacer(modifier = Modifier.weight(1f))

        Column {
            Text(
                text = "Find your partner in life",
                style = TextStyle(
                    fontSize = 46.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "We created to bring together amazing singles who want to find love, laughter and happily even after!",
                style = TextStyle(fontSize = 18.sp, color = Color.White),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = poppinsFontFamily,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { },
                colors = ButtonDefaults.buttonColors(Color.White),
                onClick = { },
                border = BorderStroke(2.dp, Color(0xFFEBEAEC)),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google logo",
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with Google",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        fontFamily = poppinsFontFamily,
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { },
                colors = ButtonDefaults.buttonColors(Color.White),
                onClick = { navController.navigate(Routes.SignIn) },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.email_logo),
                        contentDescription = "Email logo",
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with email",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        fontFamily = poppinsFontFamily,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Don't have account? ",
                fontFamily = poppinsFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
            )
            Text(
                modifier = Modifier.clickable { navController.navigate(Routes.SignUp) },
                text = "Sign up",
                fontFamily = poppinsFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}
