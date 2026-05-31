package com.example.playlist_maker_android_rogachevegor_.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.playlist_maker_android_rogachevegor_.R
import com.example.playlist_maker_android_rogachevegor_.creator.Creator
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.isDarkThemeEnabled
import com.example.playlist_maker_android_rogachevegor_.ui.theme.saveDarkThemeEnabled
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.PlaylistsViewModel
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.SearchViewModel

class MainActivity : ComponentActivity() {
    private val searchViewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
    }
    private val playlistsViewModel by viewModels<PlaylistsViewModel> {
        PlaylistsViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Creator.initialize(applicationContext)

        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(isDarkThemeEnabled()) }
            val colors = if (darkTheme) DarkColors else LightColors
            val navController = rememberNavController()

            PlaylistHost(
                navController = navController,
                colors = colors,
                darkTheme = darkTheme,
                onDarkThemeChange = { enabled ->
                    darkTheme = enabled
                    saveDarkThemeEnabled(enabled)
                },
                searchViewModel = searchViewModel,
                playlistsViewModel = playlistsViewModel
            )
        }
    }
}

@Composable
fun MainScreen(
    colors: AppColors,
    onSearchClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.mainHeaderBackground)
            .statusBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.main_screen_title),
            color = colors.titleOnHeader,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 16.dp,
                top = 14.dp,
                bottom = 30.dp
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
                .background(colors.contentBackground)
                .padding(top = 8.dp)
        ) {
            MenuItem(
                colors = colors,
                icon = R.drawable.ic_search,
                title = stringResource(R.string.search),
                onClick = onSearchClick
            )

            MenuItem(
                colors = colors,
                icon = R.drawable.ic_library,
                title = stringResource(R.string.playlists),
                onClick = onPlaylistsClick
            )

            MenuItem(
                colors = colors,
                icon = R.drawable.ic_favorite_border,
                title = stringResource(R.string.favorites),
                onClick = onFavoritesClick
            )

            MenuItem(
                colors = colors,
                icon = R.drawable.ic_settings,
                title = stringResource(R.string.settings),
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
fun MenuItem(
    colors: AppColors,
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .clickable { onClick() }
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = colors.primaryMainIcon,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            color = colors.primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = colors.secondaryIcon,
            modifier = Modifier.size(24.dp)
        )
    }

    Spacer(modifier = Modifier.height(1.dp))
}

@Preview(showBackground = true)
@Composable
fun MainScreenLightPreview() {
    MainScreen(
        colors = LightColors,
        onSearchClick = {},
        onPlaylistsClick = {},
        onFavoritesClick = {},
        onSettingsClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenDarkPreview() {
    MainScreen(
        colors = DarkColors,
        onSearchClick = {},
        onPlaylistsClick = {},
        onFavoritesClick = {},
        onSettingsClick = {}
    )
}
