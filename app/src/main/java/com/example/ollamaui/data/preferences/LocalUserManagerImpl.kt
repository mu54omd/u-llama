package com.example.ollamaui.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ollamaui.activity.BaseAddress
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

    override fun readOllamaUrl(): Flow<BaseAddress> {
        return context.dataStore.data.map { preferences ->
            val address = preferences[PreferencesKeys.OLLAMA_ADDRESS]?:"http://localhost:11434"
            val isSet = preferences[PreferencesKeys.IS_OLLAMA_ADDRESS_SET]?:false
            BaseAddress(address, isSet)
        }
    }

}
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTING)
private object PreferencesKeys {
    val IS_OLLAMA_ADDRESS_SET = booleanPreferencesKey(name = Constants.IS_OLLAMA_ADDRESS_SET)
    val OLLAMA_ADDRESS = stringPreferencesKey(name = Constants.OLLAMA_ADDRESS)
}