package com.example.datingapp.data

import com.example.datingapp.R

sealed class BottomMenuItem(
    val title: String,
    val route: String,
    val iconId: Int,
    val selectedIconId: Int
) {
    object Home : BottomMenuItem(
        title = "home",
        route = Routes.Home,
        iconId = R.drawable.ic_home,
        selectedIconId = R.drawable.ic_selected_home
    )
    object Likes : BottomMenuItem(
        title = "likes",
        route = "",
        iconId = R.drawable.ic_likes,
        selectedIconId = R.drawable.ic_selected_likes
    )
    object Messages : BottomMenuItem(
        title = "messages",
        route = "",
        iconId = R.drawable.ic_messages,
        selectedIconId = R.drawable.ic_selected_messages
    )
    object Profile : BottomMenuItem(
        title = "profile",
        route = Routes.Profile,
        iconId = R.drawable.ic_profile,
        selectedIconId = R.drawable.ic_selected_profile
    )

}