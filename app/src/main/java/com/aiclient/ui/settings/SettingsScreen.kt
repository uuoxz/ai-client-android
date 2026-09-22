package com.aiclient.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aiclient.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                SettingsSection(title = stringResource(R.string.appearance))
                SettingsItem(
                    title = stringResource(R.string.theme),
                    subtitle = stringResource(R.string.system),
                    icon = Icons.Default.Palette,
                    onClick = { /* Theme picker */ }
                )
            }

            item {
                SettingsSection(title = "AI")
                SettingsItem(
                    title = stringResource(R.string.model),
                    subtitle = "GPT-3.5 Turbo",
                    icon = Icons.Default.SmartToy,
                    onClick = { /* Model selector */ }
                )
                SettingsItem(
                    title = stringResource(R.string.provider),
                    subtitle = "OpenAI",
                    icon = Icons.Default.Cloud,
                    onClick = { /* Provider selector */ }
                )
            }

            item {
                SettingsSection(title = "Data")
                SettingsItem(
                    title = stringResource(R.string.memory),
                    subtitle = "Manage memory",
                    icon = Icons.Default.Memory,
                    onClick = { /* Memory settings */ }
                )
            }

            item {
                SettingsSection(title = stringResource(R.string.about))
                SettingsItem(
                    title = "Version",
                    subtitle = "1.0.0",
                    icon = Icons.Default.Info,
                    onClick = { /* About */ }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(icon, contentDescription = null)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
