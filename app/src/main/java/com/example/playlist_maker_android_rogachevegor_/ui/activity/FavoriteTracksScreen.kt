package com.example.playlist_maker_android_rogachevegor_.ui.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlist_maker_android_rogachevegor_.R
import com.example.playlist_maker_android_rogachevegor_.domain.models.Track
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.PlaylistsViewModel

@Composable
fun FavoritesScreen(
    colors: AppColors,
    viewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteList by viewModel.favoriteList.collectAsState(emptyList())

    FavoriteTracksScreen(
        colors = colors,
        tracks = favoriteList,
        onTrackClick = onTrackClick,
        onBackClick = onBackClick,
        onTrackLongClick = { track ->
            viewModel.toggleFavorite(track, false)
        },
        modifier = modifier
    )
}

@Composable
fun FavoriteTracksScreen(
    colors: AppColors,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit,
    onTrackLongClick: ((Track) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .statusBarsPadding()
    ) {
        FavoriteTracksToolbar(
            colors = colors,
            onBackClick = onBackClick
        )

        if (tracks.isEmpty()) {
            EmptyFavoriteTracks(colors = colors)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = tracks,
                    key = { track -> "${track.id}-${track.artistName}-${track.trackName}" }
                ) { track ->
                    TrackListItem(
                        track = track,
                        colors = colors,
                        onLongClick = onTrackLongClick?.let { longClick ->
                            { longClick(track) }
                        },
                        onClick = { onTrackClick(track) }
                    )
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = colors.secondaryIcon.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteTracksToolbar(
    colors: AppColors,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(R.string.back),
            tint = colors.primaryMainIcon,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = stringResource(R.string.favorites),
            color = colors.primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
private fun EmptyFavoriteTracks(
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    val icon = if (colors.screenBackground == DarkColors.screenBackground) {
        R.drawable.ic_nothing_found_dark
    } else {
        R.drawable.ic_nothing_found_light
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 133.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.empty_favorites),
            color = colors.primaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteTracksLightPreview() {
    FavoriteTracksScreen(
        colors = LightColors,
        tracks = listOf(
            Track(
                trackName = "Yesterday (Remastered 2009)",
                artistName = "The Beatles",
                trackTime = "2:55",
                favorite = true
            )
        ),
        onTrackClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun FavoriteTracksEmptyDarkPreview() {
    FavoriteTracksScreen(
        colors = DarkColors,
        tracks = emptyList(),
        onTrackClick = {},
        onBackClick = {}
    )
}
