package com.example.playlist_maker_android_rogachevegor_.ui.activity

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.PlaylistViewModel

@Composable
fun PlaylistScreen(
    colors: AppColors,
    viewModel: PlaylistViewModel,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val playlist by viewModel.playlist.collectAsState(null)

    PlaylistScreenContent(
        colors = colors,
        playlist = playlist,
        onTrackClick = onTrackClick,
        onBackClick = onBackClick,
        onDeletePlaylist = onDeletePlaylist,
        modifier = modifier
    )
}

@Composable
private fun PlaylistScreenContent(
    colors: AppColors,
    playlist: Playlist?,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .statusBarsPadding()
    ) {
        PlaylistHeader(
            colors = colors,
            coverImageUri = playlist?.coverImageUri,
            onBackClick = onBackClick
        )

        if (playlist == null) {
            PlaylistNotFound(
                colors = colors,
                modifier = Modifier.weight(1f)
            )
        } else {
            PlaylistInfo(
                colors = colors,
                playlist = playlist,
                onDeleteClick = { showDeleteDialog = true }
            )

            if (playlist.tracks.isEmpty()) {
                EmptyPlaylistTracks(
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = playlist.tracks,
                        key = { track -> "${track.id}-${track.artistName}-${track.trackName}" }
                    ) { track ->
                        TrackListItem(
                            track = track,
                            colors = colors,
                            artwork = trackArtwork(track),
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog && playlist != null) {
        PlaylistDeleteDialog(
            colors = colors,
            playlist = playlist,
            onConfirm = {
                showDeleteDialog = false
                onDeletePlaylist(playlist.id)
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}

@Composable
private fun PlaylistHeader(
    colors: AppColors,
    coverImageUri: String?,
    onBackClick: () -> Unit
) {
    val coverPlaceholder = painterResource(id = R.drawable.ic_music)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(376.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(R.string.back),
            tint = colors.primaryMainIcon,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(205.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.searchFieldBackground),
            contentAlignment = Alignment.Center
        ) {
            if (coverImageUri != null) {
                AsyncImage(
                    model = Uri.parse(coverImageUri),
                    contentDescription = stringResource(R.string.playlist_cover),
                    placeholder = coverPlaceholder,
                    error = coverPlaceholder,
                    fallback = coverPlaceholder,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AddPhotoAlternate,
                    contentDescription = null,
                    tint = colors.playlistPlaceholderColor(),
                    modifier = Modifier.size(96.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaylistInfo(
    colors: AppColors,
    playlist: Playlist,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = playlist.name,
            color = colors.primaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (playlist.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = playlist.description,
                color = colors.primaryText,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                R.string.playlist_duration_and_count,
                playlist.totalDurationMinutes(),
                stringResource(R.string.playlist_track_count, playlist.tracks.size)
            ),
            color = colors.primaryText,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.delete_playlist),
            tint = colors.primaryMainIcon,
            modifier = Modifier
                .size(24.dp)
                .clickable { onDeleteClick() }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun PlaylistNotFound(
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.playlist_not_found),
            color = colors.primaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun EmptyPlaylistTracks(
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.playlist_empty_tracks),
            color = colors.primaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

private fun AppColors.playlistPlaceholderColor(): Color {
    return if (screenBackground == DarkColors.screenBackground) {
        Color(0xFFAEAFB4)
    } else {
        primaryIcon
    }
}

private fun Playlist.totalDurationMinutes(): Int {
    val totalSeconds = tracks.sumOf { track ->
        track.trackTime.toSeconds()
    }
    return totalSeconds / 60
}

private fun String.toSeconds(): Int {
    val parts = split(":")
    if (parts.size != 2) {
        return 0
    }

    val minutes = parts[0].toIntOrNull() ?: return 0
    val seconds = parts[1].toIntOrNull() ?: return 0
    return minutes * 60 + seconds
}

@DrawableRes
private fun trackArtwork(track: Track): Int {
    return when {
        track.trackName.contains("Let It Be", ignoreCase = true) -> R.drawable.album_beatles_list
        track.trackName.contains("No Reply", ignoreCase = true) -> R.drawable.ic_track_placeholder
        else -> R.drawable.album_yesterday
    }
}

private val previewPlaylist = Playlist(
    id = 1,
    name = "Best songs 2021",
    description = "2022",
    tracks = listOf(
        Track(
            trackName = "Yesterday (Remastered 2009)",
            artistName = "The Beatles",
            trackTime = "2:55"
        ),
        Track(
            trackName = "Here Comes The Sun (Remastered 2009)",
            artistName = "The Beatles",
            trackTime = "2:55"
        ),
        Track(
            trackName = "No Reply",
            artistName = "The Beatles",
            trackTime = "5:41"
        ),
        Track(
            trackName = "Let It Be",
            artistName = "The Beatles",
            trackTime = "3:11"
        )
    )
)

@Preview(showBackground = true)
@Composable
fun PlaylistScreenLightPreview() {
    PlaylistScreenContent(
        colors = LightColors,
        playlist = previewPlaylist,
        onTrackClick = {},
        onBackClick = {},
        onDeletePlaylist = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlaylistScreenDarkPreview() {
    PlaylistScreenContent(
        colors = DarkColors,
        playlist = previewPlaylist,
        onTrackClick = {},
        onBackClick = {},
        onDeletePlaylist = {}
    )
}
