package com.mu54omd.ullama.ui.screen.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mu54omd.ullama.domain.repository.OllamaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository
):ViewModel() {
    private val _logs = ollamaRepository.getLogsFromDb()
    val logs = _logs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
    fun deleteLogs(){
        viewModelScope.launch {
            ollamaRepository.deleteLogFromDb()
        }
    }
}