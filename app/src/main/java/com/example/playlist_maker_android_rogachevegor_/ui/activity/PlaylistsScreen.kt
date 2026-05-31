package com.example.playlist_maker_android_rogachevegor_.ui.activity

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.PlaylistsViewModel

@Composable
fun PlaylistsScreen(
    colors: AppColors,
    playlistsViewModel: PlaylistsViewModel,
    addNewPlaylist: () -> Unit,
    navigateToPlaylist: (Long) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playlists by playlistsViewModel.playlists.collectAsState(emptyList())

    PlaylistsScreenContent(
        colors = colors,
        playlists = playlists,
        addNewPlaylist = addNewPlaylist,
        navigateToPlaylist = navigateToPlaylist,
        onDeletePlaylist = playlistsViewModel::deletePlaylistById,
        navigateBack = navigateBack,
        modifier = modifier
    )
}

@Composable
private fun PlaylistsScreenContent(
    colors: AppColors,
    playlists: List<Playlist>,
    addNewPlaylist: () -> Unit,
    navigateToPlaylist: (Long) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playlistToDelete by remember { mutableStateOf<Playlist?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlaylistsToolbar(
                colors = colors,
                navigateBack = navigateBack
            )

            if (playlists.isEmpty()) {
                EmptyPlaylists(colors = colors)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(
                        items = playlists,
                        key = { playlist -> playlist.id }
                    ) { playlist ->
                        PlaylistListItem(
                            colors = colors,
                            playlist = playlist,
                            onClick = { navigateToPlaylist(playlist.id) },
                            onLongClick = { playlistToDelete = playlist }
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = colors.primaryIcon.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd),
            onClick = addNewPlaylist,
            containerColor = colors.mainHeaderBackground,
            contentColor = colors.titleOnHeader,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_playlist)
            )
        }
    }

    playlistToDelete?.let { playlist ->
        PlaylistDeleteDialog(
            colors = colors,
            playlist = playlist,
            onConfirm = {
                onDeletePlaylist(playlist.id)
                playlistToDelete = null
            },
            onDismiss = {
                playlistToDelete = null
            }
        )
    }
}

@Composable
private fun PlaylistsToolbar(
    colors: AppColors,
    navigateBack: () -> Unit
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
                .clickable { navigateBack() }
        )

        Text(
            text = stringResource(R.string.playlists),
            color = colors.primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PlaylistListItem(
    colors: AppColors,
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val placeholder = painterResource(id = R.drawable.ic_music)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (playlist.coverImageUri != null) {
            AsyncImage(
                model = Uri.parse(playlist.coverImageUri),
                contentDescription = playlist.name,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        } else {
            Image(
                painter = placeholder,
                contentDescription = playlist.name,
                colorFilter = ColorFilter.tint(colors.primaryIcon),
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp),
            horizontalAlignment = Alignment.Start
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
                color = colors.secondaryIcon,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun PlaylistDeleteDialog(
    colors: AppColors,
    playlist: Playlist,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.screenBackground,
        titleContentColor = colors.primaryText,
        textContentColor = colors.primaryText,
        title = {
            Text(text = stringResource(R.string.delete_playlist_title))
        },
        text = {
            Text(text = stringResource(R.string.delete_playlist_message, playlist.name))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.delete),
                    color = colors.mainHeaderBackground
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = colors.mainHeaderBackground
                )
            }
        }
    )
}

@Composable
private fun EmptyPlaylists(
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(144.dp))

        Text(
            text = stringResource(R.string.empty_playlists),
            color = colors.primaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistsScreenLightPreview() {
    PlaylistsScreenContent(
        colors = LightColors,
        playlists = listOf(
            Playlist(
                id = 1,
                name = "Best songs",
                description = "",
                tracks = emptyList()
            )
        ),
        addNewPlaylist = {},
        navigateToPlaylist = {},
        onDeletePlaylist = {},
        navigateBack = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PlaylistsScreenDarkPreview() {
    PlaylistsScreenContent(
        colors = DarkColors,
        playlists = emptyList(),
        addNewPlaylist = {},
        navigateToPlaylist = {},
        onDeletePlaylist = {},
        navigateBack = {}
    )
}
