package com.example.ollamaui.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ollamaui.domain.preferences.LocalUserManager
import com.example.ollamaui.utils.Constants
import com.example.ollamaui.utils.Constants.IS_OLLAMA_ADDRESS_SET
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

    override fun readOllamaUrl(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.OLLAMA_ADDRESS]?:"http://localhost:11434"
        }
    }

    override fun readOllamaAddressStatus(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.IS_OLLAMA_ADDRESS_SET]?:false
        }
    }

}
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTING)
private object PreferencesKeys {
    val IS_OLLAMA_ADDRESS_SET = booleanPreferencesKey(name = Constants.IS_OLLAMA_ADDRESS_SET)
    val OLLAMA_ADDRESS = stringPreferencesKey(name = Constants.OLLAMA_ADDRESS)
}