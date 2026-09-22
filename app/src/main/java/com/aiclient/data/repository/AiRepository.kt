package com.aiclient.data.repository

import android.content.Context
import com.aiclient.data.remote.AiApiService
import com.aiclient.data.remote.dto.ApiMessage
import com.aiclient.data.remote.dto.ChatCompletionRequest
import com.aiclient.data.remote.dto.ChatCompletionResponse
import com.aiclient.data.remote.dto.ModelInfo
import com.aiclient.util.SecurePrefs
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AiRepository(private val ctx: Context) {

    @Volatile private var cached: Pair<String, AiApiService>? = null

    private fun service(): AiApiService {
        val base = SecurePrefs.getBaseUrl(ctx)
        cached?.let { if (it.first == base) return it.second }
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()
        val api = Retrofit.Builder()
            .baseUrl(if (base.endsWith("/")) base else "$base/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApiService::class.java)
        cached = base to api
        return api
    }

    suspend fun complete(history: List<ApiMessage>): ChatCompletionResponse {
        val key = SecurePrefs.getApiKey(ctx)
        require(key.isNotBlank()) { "API key not set. Open Settings and paste your key." }
        val req = ChatCompletionRequest(
            model = SecurePrefs.getModel(ctx),
            messages = history
        )
        return service().createChatCompletion("Bearer $key", req)
    }

    suspend fun listModels(): List<ModelInfo> {
        val key = SecurePrefs.getApiKey(ctx)
        require(key.isNotBlank()) { "API key not set" }
        return service().listModels("Bearer $key").data
            .sortedBy { it.id.lowercase() }
    }
}
