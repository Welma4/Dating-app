package com.example.datingapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datingapp.navigation.DatingApp
import com.example.datingapp.ui.theme.DatingAppTheme
import com.example.datingapp.ui.theme.poppinsFontFamily
import androidx.compose.material3.ExperimentalMaterial3Api

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DatingAppTheme {
                    DatingApp()
            }
        }
    }
}

@Composable
fun GradientBackground() {
    val gradientColors = listOf(
        Color(0xFFBC7BE4),
        Color(0xFFA020F0)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
    )
}


@Composable
fun LoginScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)
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
                .size(250.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        Column {
            Text(
                text = "Find your partner in life",
                style = TextStyle(
                    fontSize = 46.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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
                onClick = { /* handle Google login */ },
                border = BorderStroke(2.dp, Color(0xFFEBEAEC))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )    {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google logo",
                        modifier = Modifier.size(18.dp)
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
                onClick = { navController.navigate("ProfileScreen") },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    Image(
                        painter = painterResource(id = R.drawable.email_logo),
                        contentDescription = "Email logo",
                        modifier = Modifier.size(24.dp)
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
            Text(
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 15.dp),
                text = ("By signing in, you agree with our Terms & conditions."),
                style = TextStyle(fontSize = 10.sp),
                textAlign = TextAlign.Center,
                fontFamily = poppinsFontFamily,
                color = Color.White
            )
    }
}

@Preview
@Composable
fun ProfileScreen() {
    val gradientColors = listOf(
        Color(0xFFA020F0),
        Color(0xFFBC7BE4)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB09DFF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(brush = Brush.horizontalGradient(colors = gradientColors)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(60.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, CircleShape)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.osel2),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Jenny, 22",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontFamily = poppinsFontFamily
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppinsFontFamily
                )
                Text(
                    text = "Edit",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier.clickable { /* Логика для редактирования */ },
                    fontFamily = poppinsFontFamily
                )
            }

            ProfileField(label = "Name", value = "Jenny")
            ProfileField(label = "Phone Number", value = "+91 9876543210")
            ProfileField(label = "Date of birth", value = "02-05-1997")
            ProfileField(label = "Email", value = "abcqwertyu@gmail.com")
            ProfileField(label = "Location", value = "Switzerland")

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFontFamily
                ),
            label = { Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = poppinsFontFamily) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedLabelColor = Color.Gray,
                focusedLabelColor = Color.Gray,
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray
            )
        )
    }
}
