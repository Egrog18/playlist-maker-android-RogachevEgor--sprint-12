package com.example.playlist_maker_android_rogachevegor_.ui.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.playlist_maker_android_rogachevegor_.domain.models.Track
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.PlaylistViewModel
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.PlaylistsViewModel
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.SearchViewModel

enum class Destination {
    Main,
    Search,
    Playlists,
    Playlist,
    NewPlaylist,
    FavoriteTracks,
    Player,
    Settings
}

@Composable
fun PlaylistHost(
    navController: NavHostController,
    colors: AppColors,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    searchViewModel: SearchViewModel,
    playlistsViewModel: PlaylistsViewModel
) {
    var selectedTrack by remember {
        mutableStateOf(
            Track(
                trackName = "",
                artistName = "",
                trackTime = ""
            )
        )
    }

    val navigateToSearch: () -> Unit = {
        navController.navigate(Destination.Search.name)
    }

    val navigateToPlaylists: () -> Unit = {
        navController.navigate(Destination.Playlists.name)
    }

    val navigateToNewPlaylist: () -> Unit = {
        navController.navigate(Destination.NewPlaylist.name)
    }

    val navigateToFavorites: () -> Unit = {
        navController.navigate(Destination.FavoriteTracks.name)
    }

    val navigateToSettings: () -> Unit = {
        navController.navigate(Destination.Settings.name)
    }

    val navigateToPlayer: (Track) -> Unit = { track ->
        selectedTrack = track
        navController.navigate(Destination.Player.name)
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val deletePlaylistAndNavigateToList: (Long) -> Unit = { playlistId ->
        playlistsViewModel.deletePlaylistById(playlistId)
        if (!navController.popBackStack(Destination.Playlists.name, false)) {
            navController.navigate(Destination.Playlists.name)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Destination.Main.name
    ) {
        composable(route = Destination.Main.name) {
            MainScreen(
                colors = colors,
                onSearchClick = navigateToSearch,
                onPlaylistsClick = navigateToPlaylists,
                onFavoritesClick = navigateToFavorites,
                onSettingsClick = navigateToSettings
            )
        }

        composable(route = Destination.Search.name) {
            SearchScreen(
                colors = colors,
                viewModel = searchViewModel,
                onTrackClick = navigateToPlayer,
                onBackClick = navigateBack
            )
        }

        composable(route = Destination.Playlists.name) {
            PlaylistsScreen(
                colors = colors,
                playlistsViewModel = playlistsViewModel,
                addNewPlaylist = navigateToNewPlaylist,
                navigateToPlaylist = { playlistId ->
                    navController.navigate("${Destination.Playlist.name}/$playlistId")
                },
                navigateBack = navigateBack
            )
        }

        composable(
            route = "${Destination.Playlist.name}/{playlistId}",
            arguments = listOf(
                navArgument("playlistId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            val playlistViewModel = remember(backStackEntry, playlistId) {
                ViewModelProvider(
                    backStackEntry,
                    PlaylistViewModel.getViewModelFactory(playlistId)
                )[PlaylistViewModel::class.java]
            }

            PlaylistScreen(
                colors = colors,
                viewModel = playlistViewModel,
                onTrackClick = navigateToPlayer,
                onBackClick = navigateBack,
                onDeletePlaylist = deletePlaylistAndNavigateToList
            )
        }

        composable(route = Destination.NewPlaylist.name) {
            CreatePlaylistScreen(
                colors = colors,
                viewModel = playlistsViewModel,
                onBackClick = navigateBack
            )
        }

        composable(route = Destination.FavoriteTracks.name) {
            FavoritesScreen(
                colors = colors,
                viewModel = playlistsViewModel,
                onTrackClick = navigateToPlayer,
                onBackClick = navigateBack
            )
        }

        composable(route = Destination.Player.name) {
            val playlists by playlistsViewModel.playlists.collectAsState(emptyList())

            LaunchedEffect(selectedTrack.id) {
                playlistsViewModel.isExist(selectedTrack)?.let { savedTrack ->
                    selectedTrack = savedTrack
                }
            }

            PlayerScreen(
                colors = colors,
                track = selectedTrack,
                playlists = playlists,
                onBackClick = navigateBack,
                onFavoriteClick = { isFavorite ->
                    selectedTrack = selectedTrack.copy(favorite = isFavorite)
                    playlistsViewModel.toggleFavorite(
                        track = selectedTrack,
                        isFavorite = isFavorite
                    )
                },
                onPlaylistClick = { playlist ->
                    playlistsViewModel.insertTrackToPlaylist(
                        track = selectedTrack,
                        playlistId = playlist.id
                    )
                }
            )
        }

        composable(route = Destination.Settings.name) {
            SettingsScreen(
                colors = colors,
                darkTheme = darkTheme,
                onDarkThemeChange = onDarkThemeChange,
                onBackClick = navigateBack
            )
        }
    }
}
