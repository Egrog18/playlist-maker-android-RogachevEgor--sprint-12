package com.example.playlist_maker_android_rogachevegor_.ui.activity

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlist_maker_android_rogachevegor_.R
import com.example.playlist_maker_android_rogachevegor_.domain.models.Playlist
import com.example.playlist_maker_android_rogachevegor_.domain.models.Track
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors

@Composable
fun PlayerScreen(
    colors: AppColors,
    track: Track,
    playlists: List<Playlist>,
    onBackClick: () -> Unit,
    onFavoriteClick: (Boolean) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddToPlaylist by rememberSaveable {
        mutableStateOf(false)
    }
    var isFavorite by rememberSaveable(track.id, track.favorite) {
        mutableStateOf(track.favorite)
    }
    val artworkPlaceholder = painterResource(id = R.drawable.ic_track_placeholder)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
    ) {
        Box(
            modifier = Modifier
                .width(360.dp)
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        ) {
            AsyncImage(
                model = track.artworkUrl100,
                contentDescription = null,
                placeholder = artworkPlaceholder,
                error = artworkPlaceholder,
                fallback = artworkPlaceholder,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .offset(x = 24.dp, y = 82.dp)
                    .size(312.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier
                    .offset(x = 24.dp, y = 423.dp)
                    .fillMaxWidth()
                    .padding(end = 24.dp)
            ) {
                Text(
                    text = track.trackName,
                    color = colors.primaryText,
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = track.artistName,
                    color = colors.primaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            PlayerActionButton(
                icon = R.drawable.ic_player_add_to_playlist,
                contentDescription = stringResource(R.string.add_to_playlist),
                modifier = Modifier.offset(x = 24.dp, y = 526.dp),
                onClick = { showAddToPlaylist = true }
            )

            PlayerActionButton(
                icon = R.drawable.ic_player_favorite,
                contentDescription = if (isFavorite) {
                    stringResource(R.string.remove_from_favorites)
                } else {
                    stringResource(R.string.add_to_favorites)
                },
                tint = if (isFavorite) colors.mainHeaderBackground else Color.Unspecified,
                modifier = Modifier.offset(x = 285.dp, y = 526.dp),
                onClick = {
                    val newFavoriteStatus = !isFavorite
                    isFavorite = newFavoriteStatus
                    onFavoriteClick(newFavoriteStatus)
                }
            )

            PlayerDetailRow(
                colors = colors,
                title = stringResource(R.string.duration),
                value = track.trackTime,
                modifier = Modifier.offset(y = 601.dp)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(R.string.back),
            tint = colors.primaryMainIcon,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
                .size(24.dp)
                .clickable { onBackClick() }
        )

        AddToPlaylistBottomSheet(
            colors = colors,
            playlists = playlists,
            isVisible = showAddToPlaylist,
            onDismissRequest = { showAddToPlaylist = false },
            onPlaylistClick = { playlist ->
                onPlaylistClick(playlist)
                showAddToPlaylist = false
            }
        )
    }
}

@Composable
private fun PlayerActionButton(
    @DrawableRes icon: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(51.dp)
            .clickable { onClick() }
    )
}

@Composable
private fun PlayerDetailRow(
    colors: AppColors,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = colors.primaryIcon,
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = colors.primaryText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToPlaylistBottomSheet(
    colors: AppColors,
    playlists: List<Playlist>,
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    if (!isVisible) {
        return
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val handleColor = if (colors.screenBackground == DarkColors.screenBackground) {
        Color.White
    } else {
        Color(0xFFE6E8EB)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = colors.screenBackground,
        contentColor = colors.primaryText,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = handleColor)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.64f)
        ) {
            Text(
                text = stringResource(R.string.add_to_playlist),
                color = colors.primaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 6.dp, bottom = 24.dp)
            )

            if (playlists.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_playlists),
                    color = colors.primaryText,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
            } else {
                playlists.forEach { playlist ->
                    PlaylistSheetItem(
                        colors = colors,
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistSheetItem(
    colors: AppColors,
    playlist: Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = playlist.name,
                color = colors.primaryText,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(R.string.playlist_track_count, playlist.tracks.size),
                color = colors.primaryIcon,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenLightPreview() {
    PlayerScreen(
        colors = LightColors,
        track = Track(
            trackName = "Yesterday (Remastered 2009)",
            artistName = "The Beatles",
            trackTime = "2:55"
        ),
        playlists = listOf(
            Playlist(
                id = 1,
                name = "Best songs 2021",
                description = "",
                tracks = emptyList()
            )
        ),
        onBackClick = {},
        onFavoriteClick = {},
        onPlaylistClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlayerScreenDarkPreview() {
    PlayerScreen(
        colors = DarkColors,
        track = Track(
            trackName = "Yesterday (Remastered 2009)",
            artistName = "The Beatles",
            trackTime = "2:55"
        ),
        playlists = emptyList(),
        onBackClick = {},
        onFavoriteClick = {},
        onPlaylistClick = {}
    )
}
