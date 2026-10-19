package com.prochy.odesliandroid.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.prochy.odesliandroid.R
import com.prochy.odesliandroid.utils.Utils
import com.prochy.odesliandroid.utils.songlinkWebsiteUrl

@Composable
fun WebsiteFallbackDialog(originalUrl: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.public_api_retired_title)) },
        text = { Text(stringResource(R.string.public_api_retired_message)) },
        confirmButton = {
            TextButton(onClick = {
                Utils.openLink(songlinkWebsiteUrl(originalUrl), context)
                onDismiss()
            }) {
                Text(stringResource(R.string.open_in_songlink))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
