package com.kagg886.sylu_eoa.ui.componment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Loading(fullScreen:Boolean = true) {
    Box(modifier = if (fullScreen) Modifier.fillMaxSize() else Modifier, contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator()
            Text("Loading...")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingAnimation() {
    Loading()
}