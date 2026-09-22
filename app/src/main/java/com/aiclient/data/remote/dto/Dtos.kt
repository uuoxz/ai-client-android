package com.aiclient.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ApiMessage>,
    val temperature: Double = 0.7
)

data class ApiMessage(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val id: String? = null,
    val model: String? = null,
    val choices: List<Choice> = emptyList(),
    val usage: Usage? = null
)

data class Choice(
    val index: Int = 0,
    val message: ApiMessage? = null,
    @SerializedName("finish_reason") val finishReason: String? = null
)

data class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int = 0,
    @SerializedName("completion_tokens") val completionTokens: Int = 0,
    @SerializedName("total_tokens") val totalTokens: Int = 0
)

data class ModelsResponse(
    val `object`: String? = null,
    val data: List<ModelInfo> = emptyList()
)

data class ModelInfo(
    val id: String,
    val `object`: String? = null,
    val owned_by: String? = null,
    val created: Long? = null
)
