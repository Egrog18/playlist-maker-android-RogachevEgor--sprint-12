package com.example.playlist_maker_android_rogachevegor_.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.example.playlist_maker_android_rogachevegor_.R
import com.example.playlist_maker_android_rogachevegor_.creator.Creator
import com.example.playlist_maker_android_rogachevegor_.domain.models.Track
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.isDarkThemeEnabled
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.AllTracksViewModel
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.SearchState

class AllTracksActivity : ComponentActivity() {
    private val viewModel: AllTracksViewModel by lazy {
        ViewModelProvider(
            this,
            AllTracksViewModel.getViewModelFactory()
        )[AllTracksViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Creator.initialize(applicationContext)

        setContent {
            val colors = if (isDarkThemeEnabled()) DarkColors else LightColors

            AllTracksScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                colors = colors,
                onBackClick = ::finish
            )
        }
    }
}

@Composable
fun AllTracksScreen(
    modifier: Modifier = Modifier,
    viewModel: AllTracksViewModel,
    colors: AppColors = LightColors,
    onBackClick: () -> Unit = {}
) {
    val screenState by viewModel.allTracksScreenState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    Column(
        modifier = modifier
            .background(colors.screenBackground)
            .statusBarsPadding()
    ) {
        AllTracksToolbar(
            colors = colors,
            onBackClick = onBackClick
        )

        when (val state = screenState) {
            SearchState.Initial,
            SearchState.Searching -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SearchState.Success -> {
                TrackList(
                    tracks = state.foundList,
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }

            is SearchState.Fail -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.search_error, state.error),
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AllTracksToolbar(
    colors: AppColors,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onBackClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            tint = colors.primaryIcon,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = stringResource(R.string.all_tracks),
            color = colors.primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    colors: AppColors,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = tracks,
            key = { track -> "${track.artistName}-${track.trackName}" }
        ) { track ->
            TrackListItem(
                track = track,
                colors = colors
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun TrackListItem(
    track: Track,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit = {},
    colors: AppColors = LightColors,
    @DrawableRes artwork: Int = R.drawable.ic_track_placeholder
) {
    val placeholder = painterResource(id = artwork)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(start = 13.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = stringResource(R.string.track_content_description, track.trackName),
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = track.trackName,
                color = colors.primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artistName} \u00B7 ${track.trackTime}",
                color = colors.secondaryIcon,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            contentDescription = null,
            tint = colors.secondaryIcon,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTrackListItem() {
    TrackListItem(
        track = Track(
            trackName = stringResource(R.string.preview_track_name),
            artistName = stringResource(R.string.preview_artist_name),
            trackTime = "2:38"
        )
    )
}
