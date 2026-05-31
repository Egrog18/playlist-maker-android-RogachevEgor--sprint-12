package com.example.playlist_maker_android_rogachevegor_.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlist_maker_android_rogachevegor_.R
import com.example.playlist_maker_android_rogachevegor_.ui.theme.AppColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.DarkColors
import com.example.playlist_maker_android_rogachevegor_.ui.theme.LightColors

@Composable
fun SettingsScreen(
    colors: AppColors,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val shareAppMessage = stringResource(R.string.share_app_message)
    val shareChooserTitle = stringResource(R.string.share_chooser_title)
    val developerEmail = stringResource(R.string.developer_email)
    val emailSubject = stringResource(R.string.email_subject)
    val emailBody = stringResource(R.string.email_body)
    val agreementUrl = stringResource(R.string.agreement_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.screenBackground)
            .statusBarsPadding()
    ) {
        SettingsToolbar(
            colors = colors,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSwitchItem(
            colors = colors,
            title = stringResource(R.string.dark_theme),
            checked = darkTheme,
            onCheckedChange = onDarkThemeChange
        )

        SettingsIconItem(
            colors = colors,
            title = stringResource(R.string.share_app),
            icon = R.drawable.ic_share,
            onClick = {
                context.shareApp(
                    message = shareAppMessage,
                    chooserTitle = shareChooserTitle
                )
            }
        )

        SettingsIconItem(
            colors = colors,
            title = stringResource(R.string.write_to_developers),
            icon = R.drawable.ic_support_light_mode,
            onClick = {
                context.writeToDevelopers(
                    email = developerEmail,
                    subject = emailSubject,
                    body = emailBody
                )
            }
        )

        SettingsIconItem(
            colors = colors,
            title = stringResource(R.string.user_agreement),
            icon = R.drawable.ic_arrow_forward,
            onClick = {
                context.openAgreement(agreementUrl)
            }
        )
    }
}

@Composable
fun SettingsToolbar(
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
            text = stringResource(R.string.settings),
            color = colors.primaryText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    colors: AppColors,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .padding(start = 16.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = colors.primaryText,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        ThemeSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsIconItem(
    colors: AppColors,
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .clickable { onClick() }
            .padding(start = 16.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = colors.primaryText,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = colors.primaryIcon,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ThemeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val knobOffset by animateDpAsState(
        targetValue = if (checked) 29.dp else 9.dp,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "knobOffset"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF3772E7) else Color(0xFFE6E8EB),
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "trackColor"
    )

    val knobColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF3772E7) else Color(0xFFAEAFB4),
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "knobColor"
    )

    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 40.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onCheckedChange(!checked)
            }
    ) {
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 12.dp)
                .align(Alignment.Center)
                .alpha(if (checked) 0.48f else 0.6f)
                .background(
                    color = trackColor,
                    shape = RoundedCornerShape(50)
                )
        )

        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 11.dp)
                .size(18.dp)
                .background(
                    color = knobColor,
                    shape = CircleShape
                )
        )
    }
}

private fun Context.shareApp(
    message: String,
    chooserTitle: String
) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }

    startActivitySafely(Intent.createChooser(sendIntent, chooserTitle))
}

private fun Context.writeToDevelopers(
    email: String,
    subject: String,
    body: String
) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    startActivitySafely(emailIntent)
}

private fun Context.openAgreement(url: String) {
    startActivitySafely(
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    )
}

private fun Context.startActivitySafely(intent: Intent) {
    runCatching {
        startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenLightPreview() {
    SettingsScreen(
        colors = LightColors,
        darkTheme = false,
        onDarkThemeChange = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenDarkPreview() {
    SettingsScreen(
        colors = DarkColors,
        darkTheme = true,
        onDarkThemeChange = {},
        onBackClick = {}
    )
}
