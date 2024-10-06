package com.example.ollamaui.data.local

import androidx.room.TypeConverter
import com.example.ollamaui.domain.model.MessagesModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChatConverter {
    private val gson = Gson()
    @TypeConverter
    fun messageModelToString(messagesModel: MessagesModel):String{
        return gson.toJson(messagesModel)
    }
    @TypeConverter
    fun stringToMessageModel(text: String):MessagesModel{
        return gson.fromJson(text, MessagesModel::class.java)
    }
    @TypeConverter
    fun fromListToString(input: List<Int>):String{
        return gson.toJson(input)
    }
    @TypeConverter
    fun fromStringToList(text: String):List<Int>{
        return gson.fromJson<List<Int>>(text,object : TypeToken<List<Int>>() {}.type)
    }
}