package com.example.datingapp.ui.components

import AgeRangeSelector
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.datingapp.R
import com.example.datingapp.data.PreferencesEntity
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.PreferencesViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable()
fun FiltersDialog(
    preferencesViewModel: PreferencesViewModel,
    genderViewModel: GenderViewModel,
    isDialog: Boolean,
    onDialogChange: (Boolean) -> Unit,
    onGenderChange: (String) -> Unit,
    onAgeRangeChange: (Pair<Int, Int>) -> Unit,
) {
    var selectedGender by remember { mutableStateOf("Male") }
    var ageRange by remember { mutableStateOf(0 to 100) }
    var genderId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        preferencesViewModel.fetchPreferencesForUser(
            FirebaseAuth.getInstance().currentUser?.uid ?: "",
            onSuccess = { preferences ->
                genderId = preferences.gender
                ageRange = (preferences.startAgeRange to preferences.endAgeRange)
            },
            onFailure = { error ->
                Log.d("MyTag", error)
            }
        )
    }


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
                            onClick = {
                                genderViewModel.getIdByGenderName(
                                    selectedGender,
                                    onSuccess = { id ->
                                        genderId = id
                                        preferencesViewModel.savePreferencesToFirestore(
                                            PreferencesEntity(
                                                FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                                genderId,
                                                ageRange.first,
                                                ageRange.second
                                            ),
                                            onSuccess = {
                                                Log.d("MyTag", "Preferences successfully saved")
                                                onGenderChange(genderId)
                                                onAgeRangeChange(ageRange)
                                            },
                                            onFailure = { error ->
                                                Log.d("MyTag", "Error saving preferences: ${error}")
                                            }
                                        )
                                        onDialogChange(false)
                                    },
                                    onFailure = { error ->
                                        Log.d("MyTag", error)
                                    }
                                )
                            }
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
                        GenderPreferenceMenu(selectedGender = selectedGender, onGenderSelected = { selectedGender = it })
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Age",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Left
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        AgeRangeSelector(
                            initialRange = ageRange
                        ) { startAge, endAge ->
                            ageRange = startAge to endAge
                        }
                    }
                }
            }
        }
    }
}
