package com.example.ollamaui.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.data.local.objectbox.ChunkDatabase
import com.example.ollamaui.data.local.objectbox.FileDatabase
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.SYSTEM_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
    private val fileDatabase: FileDatabase,
    private val chunkDatabase: ChunkDatabase
):ViewModel(){

    private val _chatsList = ollamaRepository.getChats().map { it }
    val chatsList = _chatsList
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    fun addNewChat(
        chatTitle: String,
        systemPrompt: String,
        selectedModel: String
    ){
        val startMessage = MessageModel(
            content = systemPrompt,
            role = SYSTEM_ROLE,

        )
        viewModelScope.launch {
            val chatModel = ChatModel(
                chatTitle = chatTitle,
                modelName = selectedModel,
                chatMessages = MessagesModel(messageModels = listOf(startMessage)),
            )
            ollamaRepository.insertToDb(chatModel)

        }
    }

    fun deleteChat(chatModel: ChatModel){
        viewModelScope.launch {
            val fileIds = fileDatabase.getFileIds(chatModel.chatId)
            ollamaRepository.deleteFromDb(chatModel)
            fileIds.forEach {
                fileDatabase.removeFile(fileId = it)
                chunkDatabase.removeChunk(docId = it)
            }
        }
    }
    fun deleteChatById(chatId: Int){
        viewModelScope.launch {
            val fileIds = fileDatabase.getFileIds(chatId)
            ollamaRepository.deleteFromDbById(chatId)
            fileIds.forEach {
                fileDatabase.removeFile(fileId = it)
                chunkDatabase.removeChunk(docId = it)
            }
        }
    }
    fun findChat(chatId: Int): ChatModel?{
        return chatsList.value.find { item -> item.chatId == chatId }
    }


    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
}