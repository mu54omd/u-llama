package com.example.ollamaui.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ollamaui.activity.BaseAddress
import com.example.ollamaui.activity.EmbeddingModel
import com.example.ollamaui.domain.model.chat.DefaultModelParameters
import com.example.ollamaui.domain.model.chat.ModelParameters
import com.example.ollamaui.domain.preferences.LocalUserManager
import com.example.ollamaui.utils.Constants
import com.example.ollamaui.utils.Constants.USER_SETTING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalUserManagerImpl (
    private val context: Context
): LocalUserManager{
    override suspend fun saveOllamaUrl(url: String) {
        context.dataStore.edit { setting ->
            setting[PreferencesKeys.IS_OLLAMA_ADDRESS_SET] = true
            setting[PreferencesKeys.OLLAMA_ADDRESS] = url
        }
    }

    override fun readOllamaUrl(): Flow<BaseAddress> {
        return context.dataStore.data.map { preferences ->
            val address = preferences[PreferencesKeys.OLLAMA_ADDRESS]?:"http://127.0.0.1:11434"
            val isSet = preferences[PreferencesKeys.IS_OLLAMA_ADDRESS_SET]?:false
            BaseAddress(address, isSet)
        }
    }

    override suspend fun saveOllamaEmbeddingModel(modelName: String) {
        context.dataStore.edit { setting ->
            setting[PreferencesKeys.IS_OLLAMA_EMBEDDING_MODEL_SET] = when(modelName){
                "Select a Model" -> false
                "" -> false
                else -> true
            }
            setting[PreferencesKeys.OLLAMA_EMBEDDING_MODEL] = modelName
        }
    }

    override fun readOllamaEmbeddingModel(): Flow<EmbeddingModel> {
        return context.dataStore.data.map { preferences ->
            val modelName = preferences[PreferencesKeys.OLLAMA_EMBEDDING_MODEL]?:""
            val isSet = preferences[PreferencesKeys.IS_OLLAMA_EMBEDDING_MODEL_SET] == true
            EmbeddingModel(isSet, modelName)
        }
    }

    override suspend fun saveTuningParameters(modelParameters: ModelParameters) {
        context.dataStore.edit { setting ->
            setting[PreferencesKeys.TOP_K] = modelParameters.topK
            setting[PreferencesKeys.Top_P] = modelParameters.topP
            setting[PreferencesKeys.MIN_P] = modelParameters.minP
            setting[PreferencesKeys.TEMPERATURE] = modelParameters.temperature
            setting[PreferencesKeys.PRESENCE_PENALTY] = modelParameters.presencePenalty
            setting[PreferencesKeys.FREQUENCY_PENALTY] = modelParameters.frequencyPenalty
            setting[PreferencesKeys.NUM_CTX] = modelParameters.numCtx
        }
    }

    override fun readTuningParameters(): Flow<ModelParameters> {
        return context.dataStore.data.map { preferences ->
            ModelParameters(
                topK = preferences[PreferencesKeys.TOP_K] ?: DefaultModelParameters.default.topK,
                topP = preferences[PreferencesKeys.Top_P] ?: DefaultModelParameters.default.topP,
                minP = preferences[PreferencesKeys.MIN_P] ?: DefaultModelParameters.default.minP,
                temperature = preferences[PreferencesKeys.TEMPERATURE] ?: DefaultModelParameters.default.temperature,
                presencePenalty = preferences[PreferencesKeys.PRESENCE_PENALTY] ?: DefaultModelParameters.default.presencePenalty,
                frequencyPenalty = preferences[PreferencesKeys.FREQUENCY_PENALTY] ?: DefaultModelParameters.default.frequencyPenalty,
                numCtx = preferences[PreferencesKeys.NUM_CTX] ?: DefaultModelParameters.default.numCtx
            )
        }
    }

}
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTING)
private object PreferencesKeys {
    val IS_OLLAMA_ADDRESS_SET = booleanPreferencesKey(name = Constants.IS_OLLAMA_ADDRESS_SET)
    val OLLAMA_ADDRESS = stringPreferencesKey(name = Constants.OLLAMA_ADDRESS)
    val IS_OLLAMA_EMBEDDING_MODEL_SET = booleanPreferencesKey(name = Constants.IS_OLLAMA_EMBEDDING_MODEL_SET)
    val OLLAMA_EMBEDDING_MODEL = stringPreferencesKey(name = Constants.OLLAMA_EMBEDDING_MODEL)
    val TOP_K = intPreferencesKey(name = Constants.TOP_K)
    val Top_P = floatPreferencesKey(name = Constants.TOP_P)
    val MIN_P = floatPreferencesKey(name = Constants.MIN_P)
    val TEMPERATURE = floatPreferencesKey(name = Constants.TEMPERATURE)
    val PRESENCE_PENALTY = floatPreferencesKey(name = Constants.PRESENCE_PENALTY)
    val FREQUENCY_PENALTY = floatPreferencesKey(name = Constants.FREQUENCY_PENALTY)
    val NUM_CTX = intPreferencesKey(name = Constants.NUM_CTX)
}