package com.example.ollamaui.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.repository.OllamaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository
):ViewModel() {
    private val _state = MutableStateFlow(ChatStates())
    val state = _state.asStateFlow()

    init {
        ollamaStatus()
    }

    private fun ollamaStatus(){
        viewModelScope.launch {
            ollamaRepository.getOllamaStatus()
                .onRight { response ->  _state.update { it.copy(ollamaState = response) } }
                .onLeft { error -> _state.update { it.copy(error = error.error.message) } }
        }
    }
}