package com.example.playlist_maker_android_rogachevegor_.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.playlist_maker_android_rogachevegor_.R
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors
import com.example.playlist_maker_android_rogachevegor_.ui.view_model.PlaylistsViewModel

@Composable
fun CreatePlaylistScreen(
    colors: AppColors,
    viewModel: PlaylistsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coverImageUri by viewModel.coverImageUri.collectAsState()
    val context = LocalContext.current
    val permissionRequiredMessage = stringResource(R.string.permission_required)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setCoverImageUri(it.toString())
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, permissionRequiredMessage, Toast.LENGTH_SHORT).show()
        }
    }
    val openImagePicker = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imagePickerLauncher.launch("image/*")
        } else {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*")
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    val navigateBackAndClear = {
        viewModel.setCoverImageUri(null)
        onBackClick()
    }

    CreatePlaylistScreenContent(
        colors = colors,
        coverImageUri = coverImageUri,
        onCoverClick = openImagePicker,
        onSaveClick = { name, description ->
            viewModel.createNewPlayList(namePlaylist = name, description = description)
            onBackClick()
        },
        onBackClick = navigateBackAndClear,
        modifier = modifier
    )
}

@Composable
private fun CreatePlaylistScreenContent(
    colors: AppColors,
    coverImageUri: String?,
    onCoverClick: () -> Unit,
    onSaveClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playlistName by rememberSaveable { mutableStateOf("") }
    var playlistDescription by rememberSaveable { mutableStateOf("") }
    val canSave = playlistName.trim().isNotEmpty()
    val coverPlaceholder = painterResource(id = R.drawable.ic_cover_photo_add)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .statusBarsPadding()
    ) {
        CreatePlaylistToolbar(
            colors = colors,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(132.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onCoverClick() },
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
                Image(
                    painter = coverPlaceholder,
                    contentDescription = stringResource(R.string.select_image),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(138.dp))

        OutlinedTextField(
            value = playlistName,
            onValueChange = { playlistName = it },
            label = { Text(text = stringResource(R.string.playlist_name_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = playlistTextFieldColors(colors),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = playlistDescription,
            onValueChange = { playlistDescription = it },
            label = { Text(text = stringResource(R.string.playlist_description_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = playlistTextFieldColors(colors),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onSaveClick(playlistName, playlistDescription)
            },
            enabled = canSave,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.mainHeaderBackground,
                contentColor = colors.titleOnHeader,
                disabledContainerColor = Color(0xFFAEAFB4),
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp)
                .height(44.dp)
        ) {
            Text(
                text = stringResource(R.string.save),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CreatePlaylistToolbar(
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
            text = stringResource(R.string.new_playlist),
            color = colors.primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
private fun playlistTextFieldColors(colors: AppColors) =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.primaryText,
        unfocusedTextColor = colors.primaryText,
        focusedBorderColor = colors.mainHeaderBackground,
        unfocusedBorderColor = colors.primaryIcon,
        focusedLabelColor = colors.mainHeaderBackground,
        unfocusedLabelColor = colors.primaryIcon,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        cursorColor = colors.mainHeaderBackground
    )

@Preview(showBackground = true)
@Composable
fun CreatePlaylistScreenLightPreview() {
    CreatePlaylistScreenContent(
        colors = LightColors,
        coverImageUri = null,
        onCoverClick = {},
        onSaveClick = { _, _ -> },
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CreatePlaylistScreenDarkPreview() {
    CreatePlaylistScreenContent(
        colors = DarkColors,
        coverImageUri = null,
        onCoverClick = {},
        onSaveClick = { _, _ -> },
        onBackClick = {}
    )
}
