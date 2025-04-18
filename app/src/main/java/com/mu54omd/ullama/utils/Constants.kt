package com.mu54omd.ullama.utils

import androidx.compose.ui.unit.dp

object Constants {
    const val OLLAMA_BASE_URL = "http://127.0.0.1:11434"
    const val OLLAMA_FETCH_EMBEDDING_URL = "https://ollama.com/search?c=embedding"
    const val OLLAMA_BASE_ENDPOINT = "/"
    const val OLLAMA_LIST_ENDPOINT = "/api/tags"
    const val OLLAMA_CHAT_ENDPOINT = "/api/chat"
    const val OLLAMA_EMBED_ENDPOINT = "/api/embed"
    const val OLLAMA_PULL_ENDPOINT = "/api/pull"

    const val OLLAMA_IS_RUNNING = "Ollama is running"

    const val CHAT_DATABASE = "chat_db"
    const val LOG_DATABASE = "log_db"

    const val USER_SETTING = "user_setting"
    const val IS_OLLAMA_ADDRESS_SET = "is_ollama_address_set"
    const val OLLAMA_ADDRESS = "ollama_address"
    const val IS_OLLAMA_EMBEDDING_MODEL_SET = "is_ollama_embedding_model_set"
    const val OLLAMA_EMBEDDING_MODEL = "ollama_embedding_model"
    const val TOP_K = "top_k"
    const val TOP_P = "top_p"
    const val MIN_P = "min_p"
    const val TEMPERATURE = "temperature"
    const val PRESENCE_PENALTY = "presence_penalty"
    const val FREQUENCY_PENALTY = "frequency_penalty"
    const val NUM_CTX = "num_ctx"


    val TOP_BAR_HEIGHT = 85.dp
    const val USER_ROLE = "user"
    const val SYSTEM_ROLE = "system"
    const val ASSISTANT_ROLE = "assistant"

    val EMBEDDING_MODEL_LIST = listOf<String>("Select a Model", "nomic-embed-text", "mxbai-embed-large", "bge-m3", "snowflake-arctic-embed", "all-minilm", "bge-large", "paraphrase-multilingual", "snowflake-arctic-embed2", "granite-embedding")
}