package com.example.playlist_maker_android_rogachevegor_.ui.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlist_maker_android_rogachevegor_.R
import com.example.playlist_maker_android_rogachevegor_.domain.models.Track
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.SearchState
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.SearchViewModel

@Composable
fun SearchScreen(
    colors: AppColors,
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val historyEntries by viewModel.historyEntries.collectAsState()
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    SearchScreenContent(
        colors = colors,
        screenState = screenState,
        historyEntries = if (searchQuery.isEmpty()) historyEntries else emptyList(),
        searchQuery = searchQuery,
        onQueryChange = { newQuery ->
            searchQuery = newQuery
            if (newQuery.isEmpty()) {
                viewModel.clearSearch()
            }
        },
        onClearClick = {
            searchQuery = ""
            keyboardController?.hide()
            focusManager.clearFocus()
            viewModel.clearSearch()
        },
        onSearchClick = {
            viewModel.search(searchQuery)
        },
        onRefreshClick = {
            viewModel.retryLastFailedSearch()
        },
        onHistoryClick = { historyEntry ->
            searchQuery = historyEntry
            keyboardController?.hide()
            focusManager.clearFocus()
            viewModel.search(historyEntry)
        },
        onTrackClick = onTrackClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun SearchScreenContent(
    colors: AppColors,
    screenState: SearchState,
    historyEntries: List<String>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onHistoryClick: (String) -> Unit,
    onTrackClick: (Track) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .statusBarsPadding()
    ) {
        SearchToolbar(
            colors = colors,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        SearchTextField(
            colors = colors,
            query = searchQuery,
            onQueryChange = onQueryChange,
            onClearClick = onClearClick,
            onSearchClick = onSearchClick,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        SearchStateContent(
            state = screenState,
            colors = colors,
            historyEntries = historyEntries,
            onRefreshClick = onRefreshClick,
            onHistoryClick = onHistoryClick,
            onTrackClick = onTrackClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
fun SearchToolbar(
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
            text = stringResource(R.string.search),
            color = colors.primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
fun SearchTextField(
    colors: AppColors,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val searchText = stringResource(R.string.search)
    val searchFieldIconColor = if (colors.screenBackground == DarkColors.screenBackground) {
        Color(0xFF1A1B22)
    } else {
        Color(0xFFAEAFB4)
    }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
        textStyle = TextStyle(
            color = Color(0xFF1A1B22),
            fontSize = 16.sp
        ),
        cursorBrush = SolidColor(colors.mainHeaderBackground),
        modifier = modifier
            .height(36.dp)
            .background(
                color = colors.searchFieldBackground,
                shape = RoundedCornerShape(8.dp)
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = searchText,
                    tint = searchFieldIconColor,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onSearchClick()
                        }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = searchText,
                            color = Color(0xFFAEAFB4),
                            fontSize = 16.sp
                        )
                    }

                    innerTextField()
                }

                if (query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search),
                        tint = searchFieldIconColor,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onClearClick()
                            }
                    )
                }
            }
        }
    )
}

@Composable
private fun SearchStateContent(
    state: SearchState,
    colors: AppColors,
    historyEntries: List<String>,
    onRefreshClick: () -> Unit,
    onHistoryClick: (String) -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        SearchState.Initial -> {
            if (historyEntries.isEmpty()) {
                Box(modifier = modifier.fillMaxSize())
            } else {
                SearchHistoryContent(
                    colors = colors,
                    entries = historyEntries,
                    onHistoryClick = onHistoryClick,
                    modifier = modifier
                )
            }
        }

        SearchState.Searching -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.mainHeaderBackground)
            }
        }

        is SearchState.Success -> {
            if (state.foundList.isEmpty()) {
                EmptySearchResult(
                    colors = colors,
                    modifier = modifier
                )
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    items(
                        items = state.foundList,
                        key = { track -> "${track.artistName}-${track.trackName}-${track.trackTime}" }
                    ) { track ->
                        TrackListItem(
                            track = track,
                            colors = colors,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
        }

        is SearchState.Fail -> {
            ConnectionErrorResult(
                colors = colors,
                onRefreshClick = onRefreshClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun SearchHistoryContent(
    colors: AppColors,
    entries: List<String>,
    onHistoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        items(
            items = entries,
            key = { entry -> entry }
        ) { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onHistoryClick(entry) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = colors.secondaryIcon,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = entry,
                    color = colors.primaryText,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun ConnectionErrorResult(
    colors: AppColors,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = if (colors.screenBackground == DarkColors.screenBackground) {
        R.drawable.ic_connection_error_dark
    } else {
        R.drawable.ic_connection_error_light
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 113.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.connection_problem_title),
            color = colors.primaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.connection_problem_message),
            color = colors.primaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRefreshClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.mainHeaderBackground,
                contentColor = colors.titleOnHeader
            ),
            shape = RoundedCornerShape(54.dp)
        ) {
            Text(
                text = stringResource(R.string.refresh),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptySearchResult(
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
            .padding(top = 113.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.search_empty_result),
            color = colors.primaryText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenLightPreview() {
    SearchScreenContent(
        colors = LightColors,
        screenState = SearchState.Initial,
        historyEntries = emptyList(),
        searchQuery = "",
        onQueryChange = {},
        onClearClick = {},
        onSearchClick = {},
        onRefreshClick = {},
        onHistoryClick = {},
        onTrackClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenConnectionErrorLightPreview() {
    SearchScreenContent(
        colors = LightColors,
        screenState = SearchState.Fail(""),
        historyEntries = emptyList(),
        searchQuery = "",
        onQueryChange = {},
        onClearClick = {},
        onSearchClick = {},
        onRefreshClick = {},
        onHistoryClick = {},
        onTrackClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenDarkPreview() {
    SearchScreenContent(
        colors = DarkColors,
        screenState = SearchState.Initial,
        historyEntries = emptyList(),
        searchQuery = "",
        onQueryChange = {},
        onClearClick = {},
        onSearchClick = {},
        onRefreshClick = {},
        onHistoryClick = {},
        onTrackClick = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenConnectionErrorDarkPreview() {
    SearchScreenContent(
        colors = DarkColors,
        screenState = SearchState.Fail(""),
        historyEntries = emptyList(),
        searchQuery = "",
        onQueryChange = {},
        onClearClick = {},
        onSearchClick = {},
        onRefreshClick = {},
        onHistoryClick = {},
        onTrackClick = {},
        onBackClick = {}
    )
}
