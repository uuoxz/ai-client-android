package com.aiclient.data.remote

import com.aiclient.data.remote.dto.ChatCompletionRequest
import com.aiclient.data.remote.dto.ChatCompletionResponse
import com.aiclient.data.remote.dto.ModelsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AiApiService {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") auth: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse

    @GET("models")
    suspend fun listModels(
        @Header("Authorization") auth: String
    ): ModelsResponse
}
