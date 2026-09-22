package com.aiclient.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.aiclient.R
import com.aiclient.data.remote.dto.ModelInfo
import com.aiclient.data.repository.AiRepository
import com.aiclient.util.SecurePrefs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { AiRepository(ctx) }

    var apiKey by remember { mutableStateOf(SecurePrefs.getApiKey(ctx)) }
    var baseUrl by remember { mutableStateOf(SecurePrefs.getBaseUrl(ctx)) }
    var model by remember { mutableStateOf(SecurePrefs.getModel(ctx)) }

    var models by remember { mutableStateOf<List<ModelInfo>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    fun loadModels() {
        if (apiKey.isBlank()) {
            error = "Сначала введи API-ключ"
            return
        }
        loading = true
        error = null
        scope.launch {
            try {
                models = repo.listModels()
                if (models.isEmpty()) error = "API вернул пустой список"
            } catch (e: Exception) {
                error = e.message ?: e.toString()
            } finally {
                loading = false
            }
        }
    }

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
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("AI Provider", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = baseUrl,
                onValueChange = {
                    baseUrl = it
                    SecurePrefs.setBaseUrl(ctx, it.trim())
                },
                label = { Text("Base URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = {
                    apiKey = it
                    SecurePrefs.setApiKey(ctx, it.trim())
                },
                label = { Text("API Key") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Model", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                IconButton(onClick = { loadModels() }, enabled = !loading) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить список")
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it; SecurePrefs.setModel(ctx, it.trim()) },
                    label = { Text("Модель") },
                    readOnly = false,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (models.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Нажми ⟳ чтобы загрузить") },
                            onClick = { expanded = false }
                        )
                    } else {
                        models.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.id) },
                                onClick = {
                                    model = m.id
                                    SecurePrefs.setModel(ctx, m.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                "Моделей: ${models.size}   •   Default: ${SecurePrefs.DEFAULT_MODEL}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            error?.let {
                Text("⚠️ $it", color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Button(
                onClick = { loadModels() },
                enabled = !loading && apiKey.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Загрузка…" else "Загрузить модели")
            }
        }
    }
}
